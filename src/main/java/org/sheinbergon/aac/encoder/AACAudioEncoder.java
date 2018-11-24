package org.sheinbergon.aac.encoder;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import lombok.*;
import lombok.experimental.Accessors;
import org.sheinbergon.aac.encoder.util.AACAudioEncoderException;
import org.sheinbergon.aac.encoder.util.AACEncodingChannelMode;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;
import org.sheinbergon.aac.jna.FdkAACLibFacade;
import org.sheinbergon.aac.jna.structure.*;
import org.sheinbergon.aac.jna.util.AACEncParam;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NotThreadSafe
@Accessors(fluent = true)
public class AACAudioEncoder implements AutoCloseable {

    private final static Set<Integer> SAMPLE_RATES = WAVAudioSupport.SUPPORTED_SAMPLE_RATES.stream()
            .map(Number::intValue)
            .collect(Collectors.toSet());

    // Some fdk-aac internal constants
    private final static int PARAMETRIC_STEREO_CHANNEL_COUNT = 2;
    private final static int ADTS_TRANSMUX = 2;
    private final static int WAV_INPUT_CHANNEL_ORDER = 1;
    private final static int MAX_ENCODER_CHANNELS = 0;
    private final static int ENCODER_MODULES_MASK = 0;

    // This buffer just needs to be big enough to contain the encoded data
    private final static int OUT_BUFFER_SIZE = 20480;

    private final AACEncoder encoder;
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

    private AACAudioEncoder(AACEncoder encoder, AACEncInfo info) {
        this.encoder = encoder;
        this.inputBufferSize = info.inputChannels * info.frameLength * 2;
        this.inBuffer = new Memory(inputBufferSize);
        this.outBuffer = new Memory(OUT_BUFFER_SIZE);
        this.inArgs = new AACEncInArgs();
        this.outArgs = new AACEncOutArgs();
        this.inBufferDescriptor = FdkAACLibFacade.inBufferDescriptor(inBuffer);
        this.outBufferDescriptor = FdkAACLibFacade.outBufferDescriptor(outBuffer);
        disableStructureSynchronization();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        /**
         * Reasonable minimal ratios according to @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
         */
        private final static Map<AACEncodingProfile, Float> SAMPLES_TO_BIT_RATE_RATIO = Map.of(
                AACEncodingProfile.AAC_LC, 1.5f,
                AACEncodingProfile.HE_AAC, 0.625f,
                AACEncodingProfile.HE_AAC_V2, 0.5f);

        // Defaults
        private boolean afterBurner = true;
        private AACEncodingProfile profile = AACEncodingProfile.AAC_LC;
        private int channels = 2;
        private int sampleRate = 44100;

        private void setEncoderParams(@Nonnull AACEncoder encoder) {
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AFTERBURNER, afterBurner ? 1 : 0);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE, sampleRate);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_BITRATE, deduceBitRate());
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_TRANSMUX, ADTS_TRANSMUX);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AOT, profile.getAot());
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELORDER, WAV_INPUT_CHANNEL_ORDER);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE, AACEncodingChannelMode.valueOf(channels).getMode());
        }

        private int deduceBitRate() {
            return (int) (channels * sampleRate * SAMPLES_TO_BIT_RATE_RATIO.get(profile));
        }

        // TODO - add AAC profile verification
        public AACAudioEncoder build() {
            if (!SAMPLE_RATES.contains(sampleRate)) {
                throw new AACAudioEncoderException("sampleRate", sampleRate);
            } else if (AACEncodingChannelMode.valueOf(channels) == AACEncodingChannelMode.MODE_INVALID) {
                throw new AACAudioEncoderException("channels", channels);
            } else if (profile == AACEncodingProfile.HE_AAC_V2 && channels != PARAMETRIC_STEREO_CHANNEL_COUNT) {
                throw new AACAudioEncoderException("HE-AACv2 only supports 2 channels (stereo) mode");
            } else {
                AACEncoder encoder = FdkAACLibFacade.openEncoder(ENCODER_MODULES_MASK, MAX_ENCODER_CHANNELS);
                setEncoderParams(encoder);
                FdkAACLibFacade.initEncoder(encoder);
                AACEncInfo info = FdkAACLibFacade.getEncoderInfo(encoder);
                return new AACAudioEncoder(encoder, info);
            }
        }
    }

    @Nonnull
    public final AACAudioOutput encode(@NonNull WAVAudioInput input) throws AACAudioEncoderException {
        int read;
        verifyState();
        try {
            val inputData = inputData(input);
            val accumulator = AACAudioOutput.accumulator();
            val inputStream = new ByteArrayInputStream(inputData);
            byte[] buffer = new byte[inputBufferSize()];
            while ((read = inputStream.read(buffer)) != WAVAudioSupport.EOS) {
                populateInputBuffer(buffer, read);
                byte[] encoded = FdkAACLibFacade.encode(encoder, inBufferDescriptor, outBufferDescriptor, inArgs, outArgs, read)
                        .orElseThrow(() -> new IllegalStateException("No encoded audio data returned"));
                accumulator.accumulate(encoded);
            }
            return accumulator.done();
        } catch (IOException | RuntimeException x) {
            throw new AACAudioEncoderException("Could not encode WAV audio to AAC audio", x);
        }
    }

    public final AACAudioOutput conclude() throws AACAudioEncoderException {
        Optional<byte[]> optional;
        verifyState();
        try {
            inBufferDescriptor.clear();
            val accumulator = AACAudioOutput.accumulator();
            while ((optional = FdkAACLibFacade.encode(encoder, inBufferDescriptor, outBufferDescriptor, inArgs, outArgs, WAVAudioSupport.EOS)).isPresent()) {
                accumulator.accumulate(optional.get());
            }
            return accumulator.done();
        } catch (RuntimeException x) {
            throw new AACAudioEncoderException("Could not conclude WAV audio to AAC audio", x);
        } finally {
            close(); // Once conclusion has taken place, this encoder instance should be discarded
        }
    }

    private void populateInputBuffer(@Nonnull byte[] buffer, int size) {
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
        // These require writing them initially prior to disabling automatic synchronization
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

    @Nonnull
    private byte[] inputData(WAVAudioInput input) {
        switch (input.sampleSize()) {
            case _16:
                return input.data();
            case _24:
                return WAVAudioSupport.downsample24To16Bits(input.data());
            default:
                throw new AACAudioEncoderException(String.format(
                        "Unsupported samples size - '%d'",
                        input.sampleSize().bits()));
        }
    }

    private void verifyState() {
        if (closed) {
            throw new AACAudioEncoderException("Encoder instance already closed");
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