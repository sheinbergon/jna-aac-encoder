package org.sheinbergon.aac.encoder;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Range;
import org.sheinbergon.aac.encoder.util.AACAudioEncoderException;
import org.sheinbergon.aac.encoder.util.AACEncodingChannelMode;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;
import org.sheinbergon.aac.jna.FdkAACLibFacade;
import org.sheinbergon.aac.jna.structure.AACEncBufDesc;
import org.sheinbergon.aac.jna.structure.AACEncInfo;
import org.sheinbergon.aac.jna.structure.AACEncoder;
import org.sheinbergon.aac.jna.util.AACEncParam;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@NotThreadSafe
@Accessors(fluent = true)
public class AACAudioEncoder implements AutoCloseable {

    /**
     * Safe, reasonable boundaries according to @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.5/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
     */
    private final static Range<Integer> BITRATE_RANGE = Range.between(64000, 640000);

    private final static Set<Integer> SAMPLE_RATES = Set.of(8000, 11025, 12000, 16000, 22050, 24000, 32000, 44100, 48000);

    // Some fdk-aac internal constants
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
    private final AACEncBufDesc inBufferDescriptor;
    private final AACEncBufDesc outBufferDescriptor;

    private boolean closed = false;

    private AACAudioEncoder(AACEncoder encoder, AACEncInfo info) {
        this.encoder = encoder;
        this.inputBufferSize = info.inputChannels * info.frameLength * 2;
        this.inBuffer = new Memory(inputBufferSize);
        this.outBuffer = new Memory(OUT_BUFFER_SIZE);
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

        // Arbitrary quality factor safety measure. This might change in the future
        private final static float SAMPLES_TO_BIT_RATE_FACTOR = 1.5f;

        // Defaults
        private boolean afterBurner = true;
        private AACEncodingProfile profile = AACEncodingProfile.AAC_LC;
        private int channels = 2;
        private int bitRate = 64000;
        private int sampleRate = 44100;

        private void setEncoderParams(AACEncoder encoder) {
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AFTERBURNER, afterBurner ? 1 : 0);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE, sampleRate);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_BITRATE, bitRate);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_TRANSMUX, ADTS_TRANSMUX);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AOT, profile.getAot());
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELORDER, WAV_INPUT_CHANNEL_ORDER);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE, AACEncodingChannelMode.valueOf(channels).getMode());
        }

        private void adaptBitRate() {
            float minimalBitRate = channels * sampleRate / SAMPLES_TO_BIT_RATE_FACTOR;
            bitRate = minimalBitRate > bitRate ? (int) minimalBitRate : bitRate;
        }

        // TODO - add AAC profile verification
        public AACAudioEncoder build() {
            adaptBitRate();
            if (!SAMPLE_RATES.contains(sampleRate)) {
                throw new AACAudioEncoderException("sampleRate", sampleRate);
            } else if (!BITRATE_RANGE.contains(bitRate)) {
                throw new AACAudioEncoderException("bitRate", bitRate);
            } else if (AACEncodingChannelMode.valueOf(channels) == AACEncodingChannelMode.MODE_INVALID) {
                throw new AACAudioEncoderException("channels", channels);
            } else {
                AACEncoder encoder = FdkAACLibFacade.openEncoder(ENCODER_MODULES_MASK, MAX_ENCODER_CHANNELS);
                setEncoderParams(encoder);
                FdkAACLibFacade.initEncoder(encoder);
                AACEncInfo info = FdkAACLibFacade.getEncoderInfo(encoder);
                return new AACAudioEncoder(encoder, info);
            }
        }
    }

    public final AACAudioOutput encode(WAVAudioInput input) throws AACAudioEncoderException {
        int read;
        verifyState();
        try {
            AACAudioOutput.Accumulator accumlator = AACAudioOutput.accumulator();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(input.data());
            byte[] buffer = new byte[inputBufferSize()];
            while ((read = inputStream.read(buffer)) != WAVAudioSupport.EOS) {
                populateInputBuffer(buffer, read);
                byte[] encoded = FdkAACLibFacade.encode(encoder, inBufferDescriptor, outBufferDescriptor, read)
                        .orElseThrow(() -> new IllegalStateException("No encoded audio data returned"));
                accumlator.accumulate(encoded);
            }
            return accumlator.done();
        } catch (IOException | RuntimeException x) {
            throw new AACAudioEncoderException("Could not encode WAV audio to AAC audio", x);
        }
    }

    public final AACAudioOutput conclude() throws AACAudioEncoderException {
        Optional<byte[]> optional;
        verifyState();
        try {
            inBufferDescriptor.clear();
            AACAudioOutput.Accumulator accumlator = AACAudioOutput.accumulator();
            while ((optional = FdkAACLibFacade.encode(encoder, inBufferDescriptor, outBufferDescriptor, WAVAudioSupport.EOS)).isPresent()) {
                accumlator.accumulate(optional.get());
            }
            return accumlator.done();
        } catch (RuntimeException x) {
            throw new AACAudioEncoderException("Could not conclude WAV audio to AAC audio", x);
        } finally {
            close(); // Once conclusion has taken place, this encoder instance should be discarded
        }
    }

    private void populateInputBuffer(byte[] buffer, int size) {
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
        encoder.write();
        encoder.setAutoSynch(false);
        inBufferDescriptor.write();
        inBufferDescriptor.setAutoSynch(false);
        outBufferDescriptor.write();
        outBufferDescriptor.setAutoSynch(false);
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