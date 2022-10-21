package org.sheinbergon.aac.encoder;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.sheinbergon.aac.encoder.util.AACEncoderException;
import org.sheinbergon.aac.encoder.util.AACEncodingChannelMode;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;
import org.sheinbergon.aac.encoder.util.WAVSupport;
import org.sheinbergon.aac.jna.FdkAACLibFacade;
import org.sheinbergon.aac.jna.structure.AACEncBufDesc;
import org.sheinbergon.aac.jna.structure.AACEncInArgs;
import org.sheinbergon.aac.jna.structure.AACEncInfo;
import org.sheinbergon.aac.jna.structure.AACEncOutArgs;
import org.sheinbergon.aac.jna.structure.AACEncoderHandle;
import org.sheinbergon.aac.jna.util.AACEncParam;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@NotThreadSafe
@Accessors(fluent = true)
public final class AACEncoder implements AutoCloseable {

  private static final Set<Integer> SAMPLE_RATES = Set.of(16000, 22050, 24000, 32000, 44100, 48000);

  // Some fdk-aac internal constants
  private static final int PARAMETRIC_STEREO_CHANNEL_COUNT = 2;
  private static final int DEFAULT_SAMPLE_RATE = 44100;
  private static final int ADTS_TRANSMUX = 2;
  private static final int WAV_INPUT_CHANNEL_ORDER = 1;
  private static final int MAX_ENCODER_CHANNELS = 0;
  private static final int ENCODER_MODULES_MASK = 0;

  // This buffer just needs to be big enough to contain the encoded data
  private static final int OUT_BUFFER_SIZE = 20480;

  private final AACEncoderHandle encoder;
  @Getter
  private final int inputBufferSize;

  // These are all created upon construciton/build in order to utilize memory allocations efficiently
  // ----------------------------------------------
  // Hard references are advised for memory buffers
  private final Memory inBuffer;
  private final Memory outBuffer;
  private final AACEncInArgs inArgs;
  private final AACEncOutArgs outArgs;
  private final AACEncBufDesc inBufferDescriptor;
  private final AACEncBufDesc outBufferDescriptor;

  private volatile boolean closed = false;

  private AACEncoder(
      final @Nonnull AACEncoderHandle aacEncoder,
      final @Nonnull AACEncInfo aacEncoderInfo) {
    this.encoder = aacEncoder;
    this.inputBufferSize = aacEncoderInfo.inputChannels * aacEncoderInfo.frameLength * 2;
    this.inBuffer = new Memory(inputBufferSize);
    this.outBuffer = new Memory(OUT_BUFFER_SIZE);
    this.inArgs = new AACEncInArgs();
    this.outArgs = new AACEncOutArgs();
    this.inBufferDescriptor = FdkAACLibFacade.inBufferDescriptor(inBuffer);
    this.outBufferDescriptor = FdkAACLibFacade.outBufferDescriptor(outBuffer);
    disableStructureSynchronization();
  }

  /**
   * Create a new {@link AACEncoder.Builder} instance.
   *
   * @return the builder instance
   */
  @Nonnull
  public static Builder builder() {
    return new Builder();
  }

  @Setter
  @Accessors(chain = true, fluent = true)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {

    /**
     * Reasonable minimal ratios according to documentation found in the source-code.
     *
     * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
     */
    private static final Map<AACEncodingProfile, Float> SAMPLES_TO_BIT_RATE_RATIO = Map.of(
        AACEncodingProfile.AAC_LC, 1.5f,
        AACEncodingProfile.HE_AAC, 0.625f,
        AACEncodingProfile.HE_AAC_V2, 0.5f);

    // Defaults
    private boolean afterBurner = true;
    private AACEncodingProfile profile = AACEncodingProfile.AAC_LC;
    private int channels = 2;
    private int sampleRate = DEFAULT_SAMPLE_RATE;

    private void setEncoderParams(final @Nonnull AACEncoderHandle encoder) {
      FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AFTERBURNER, afterBurner ? 1 : 0);
      FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE, sampleRate);
      FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_BITRATE, deduceBitRate());
      FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_TRANSMUX, ADTS_TRANSMUX);
      FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AOT, profile.aot());
      FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELORDER, WAV_INPUT_CHANNEL_ORDER);
      FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE,
          AACEncodingChannelMode.valueOf(channels).mode());
    }

    private int deduceBitRate() {
      return (int) (channels * sampleRate * SAMPLES_TO_BIT_RATE_RATIO.get(profile));
    }

    // TODO - add AAC profile verification

    /**
     * Verify and build an {@link AACEncoder} instance.
     *
     * @return the verified encoder instance
     */
    @Nonnull
    public AACEncoder build() {
      if (!SAMPLE_RATES.contains(sampleRate)) {
        throw new AACEncoderException("sampleRate", sampleRate);
      } else if (AACEncodingChannelMode.valueOf(channels) == AACEncodingChannelMode.MODE_INVALID) {
        throw new AACEncoderException("channels", channels);
      } else if (profile == AACEncodingProfile.HE_AAC_V2 && channels != PARAMETRIC_STEREO_CHANNEL_COUNT) {
        throw new AACEncoderException("HE-AACv2 only supports 2 channels (stereo) mode");
      } else {
        AACEncoderHandle encoder = FdkAACLibFacade.openEncoder(ENCODER_MODULES_MASK, MAX_ENCODER_CHANNELS);
        setEncoderParams(encoder);
        FdkAACLibFacade.initEncoder(encoder);
        AACEncInfo info = FdkAACLibFacade.getEncoderInfo(encoder);
        return new AACEncoder(encoder, info);
      }
    }
  }

  /**
   * Encode the given wav audio input to AAC audio output.
   *
   * @param input wav audio-input to encode
   * @return the encoded audio output data
   * @throws AACEncoderException if any unexpected encoding error was encountered
   */
  public AACOutput encode(final WAVInput input) {
    int read;
    verifyState();
    try {
      AACOutput.Accumulator accumulator = AACOutput.accumulator();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(input.data());
      byte[] buffer = new byte[inputBufferSize()];
      while ((read = inputStream.read(buffer)) != WAVSupport.EOS) {
        populateInputBuffer(buffer, read);
        byte[] encoded = FdkAACLibFacade
            .encode(encoder, inBufferDescriptor, outBufferDescriptor, inArgs, outArgs, read)
            .orElseThrow(() -> new IllegalStateException("No encoded audio data returned"));
        accumulator.accumulate(encoded);
      }
      return accumulator.done();
    } catch (IOException | RuntimeException x) {
      throw new AACEncoderException("Could not encode WAV to AAC", x);
    }
  }

  /**
   * Conclude encoded audio. To be called when no more audio-input data is available.
   *
   * @return the concluded audio output data. No further encoding is expected to take place from here on out.
   * @throws AACEncoderException if any unexpected encoding error was encountered
   */
  public AACOutput conclude() {
    Optional<byte[]> optional;
    verifyState();
    try {
      inBufferDescriptor.clear();
      AACOutput.Accumulator accumulator = AACOutput.accumulator();
      while ((optional = FdkAACLibFacade
          .encode(encoder, inBufferDescriptor, outBufferDescriptor, inArgs, outArgs, WAVSupport.EOS))
          .isPresent()) {
        accumulator.accumulate(optional.get());
      }
      return accumulator.done();
    } catch (RuntimeException x) {
      throw new AACEncoderException("Could not conclude WAV to AAC encoding", x);
    } finally {
      close(); // Once conclusion has taken place, this encoder instance should be discarded
    }
  }

  private void populateInputBuffer(final @Nonnull byte[] buffer, final int size) {
    inBuffer.write(0, buffer, 0, size);
    if (size != inputBufferSize) {
      inBufferDescriptor.bufSizes = new IntByReference(size);
      inBufferDescriptor.writeField("bufSizes");
    }
  }

  /*
   * This disables non-crucial synchronization for irrelevant structs.
   * In order to dramatically(!!!) boost performance and solve JNA memory pressure issues
   */
  private void disableStructureSynchronization() {
    // These require writing them initialy prior to disable automatic synchronization
    encoder.write();
    encoder.setAutoSynch(false);
    inBufferDescriptor.write();
    inBufferDescriptor.setAutoSynch(false);
    outBufferDescriptor.write();
    outBufferDescriptor.setAutoSynch(false);

    // In/Out args do not contain anything worth writing initially
    inArgs.setAutoSynch(false);
    outArgs.setAutoSynch(false);
  }

  private void verifyState() {
    if (closed) {
      throw new AACEncoderException("Encoder instance already closed");
    }
  }

  @Override
  public void close() {
    if (!closed) {
      FdkAACLibFacade.closeEncoder(encoder);
      closed = true;
    }
  }
}
