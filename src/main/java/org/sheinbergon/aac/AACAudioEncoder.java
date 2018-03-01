package org.sheinbergon.aac;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Range;
import org.sheinbergon.aac.jna.FdkAACLibFacade;
import org.sheinbergon.aac.jna.structure.AACEncInfo;
import org.sheinbergon.aac.jna.structure.AACEncoder;
import org.sheinbergon.aac.jna.util.AACEncParam;
import org.sheinbergon.aac.util.AACAudioEncoderException;
import org.sheinbergon.aac.util.AACEncodingChannelMode;
import org.sheinbergon.aac.util.AACEncodingProfile;
import org.sheinbergon.aac.util.WAVAudioSupport;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AACAudioEncoder implements AutoCloseable {

    /**
     * Safe boundaries according to @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.5/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
     */
    private final static Range<Integer> BITRATE_RANGE = Range.between(64000, 640000);
    private final static Set<Integer> SAMPLE_RATES = Set.of(8000, 11025, 12000, 16000, 22050, 24000, 32000, 44100, 48000);

    private final static int WAV_INPUT_CHANNEL_ORDER = 1;
    private final static int MAX_ENCODER_CHANNELS = 0;
    private final static int ENCODER_MODULES_MASK = 0;

    private final AACEncoder encoder;
    private final AACEncInfo info;

    public final static AACAudioEncoder DEFAULT = builder().build();

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        // Arbitrary quality factor safety measure. This might change in the future
        private final static float SAMPLES_TO_BIT_RATE_FACTOR = 1.25f;

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
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AOT, profile.getAot());
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELORDER, WAV_INPUT_CHANNEL_ORDER);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE, AACEncodingChannelMode.valueOf(channels).getMode());
        }

        private void adaptBitRate() {
            float minimalBitRate = channels * sampleRate / SAMPLES_TO_BIT_RATE_FACTOR;
            bitRate = minimalBitRate > bitRate ? (int) minimalBitRate : bitRate;
        }

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
                if (info != null) {
                    return new AACAudioEncoder(encoder, info);
                } else {
                    throw new AACAudioEncoderException("Could obtain encoder info");
                }
            }
        }
    }

    public final int inputBufferSize(int channels) {
        return channels * info.frameLength * 2;
    }


    public final AACAudioOutput encode(WAVAudioInput input, boolean conclude) throws AACAudioEncoderException {
        int dataRead;
        AACAudioOutput.Accumulator accumlator = AACAudioOutput.accumulator();
        try {
            int dataBufferSize = inputBufferSize(input.channels());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(input.data());
            byte[] dataBuffer = new byte[dataBufferSize];
            while ((dataRead = inputStream.read(dataBuffer)) != WAVAudioSupport.EOS) {
                accumlator.accumulate(FdkAACLibFacade.encode(encoder, dataRead, dataBuffer));
            }
            if (conclude) {
                accumlator.accumulate(conclude().data());
            }
            return accumlator.done();
        } catch (IOException | RuntimeException x) {
            throw new AACAudioEncoderException("Could not encode WAV audio to AAC audio", x);
        }
    }

    public AACAudioOutput conclude() throws AACAudioEncoderException {
        try {
            AACAudioOutput.Accumulator accumlator = AACAudioOutput.accumulator();
            accumlator.accumulate(FdkAACLibFacade.encode(encoder, WAVAudioSupport.EOS, null));
            return accumlator.done();
        } catch (RuntimeException x) {
            throw new AACAudioEncoderException("Could not conclude WAV audio to AAC audio", x);
        }
    }

    @Override
    public void close() {
        FdkAACLibFacade.closeEncoder(encoder);
    }

    public static void main(String[] args) throws Exception {
        AACAudioEncoder encoder = AACAudioEncoder.builder().build();
        AudioInputStream ais = AudioSystem.getAudioInputStream(new File("/home/idans/Downloads/sample.wav"));
        FileOutputStream fos = new FileOutputStream(new File("/tmp/file.aac"));
        int base = encoder.inputBufferSize(ais.getFormat().getChannels());
        try (fos; ais) {
            int read = -1;
            byte[] data = new byte[base * 3];
            while ((read = ais.read(data)) != -1) {
                WAVAudioInput input = WAVAudioInput.pcms16le(data, read, ais.getFormat().getChannels());
                boolean conclude = read < base * 3;
                fos.write(encoder.encode(input, conclude).data());
            }
        }
    }
}