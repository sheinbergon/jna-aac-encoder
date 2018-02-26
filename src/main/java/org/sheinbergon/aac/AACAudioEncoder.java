package org.sheinbergon.aac;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Range;
import org.sheinbergon.aac.jna.v015.FdkAACLibFacade;
import org.sheinbergon.aac.jna.v015.structure.AACEncInfo;
import org.sheinbergon.aac.jna.v015.structure.AACEncoder;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;
import org.sheinbergon.aac.util.AACAudioEncoderException;
import org.sheinbergon.aac.util.AACChannelMode;
import org.sheinbergon.aac.util.AACProfile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AACAudioEncoder implements AutoCloseable {

    /**
     * Safe boundaries according to @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.5/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
     */
    private final static Range<Integer> BITRATE_RANGE = Range.between(64000, 320000);
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

        // Defaults
        private boolean afterBurner = true;
        private AACProfile profile = AACProfile.AAC_LC;
        private int channels = 2;
        private int bitRate = 64000;
        private int sampleRate = 44100;

        private void setEncoderParams(AACEncoder encoder) {
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AFTERBURNER, afterBurner ? 1 : 0);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE, sampleRate);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_BITRATE, bitRate);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AOT, profile.getAot());
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELORDER, WAV_INPUT_CHANNEL_ORDER);
            FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE, AACChannelMode.valueOf(channels).getMode());
        }

        public AACAudioEncoder build() {
            if (!SAMPLE_RATES.contains(sampleRate)) {
                throw new AACAudioEncoderException("sampleRate", sampleRate);
            } else if (!BITRATE_RANGE.contains(bitRate)) {
                throw new AACAudioEncoderException("bitRate", bitRate);
            } else if (AACChannelMode.valueOf(channels) != AACChannelMode.MODE_INVALID) {
                throw new AACAudioEncoderException("channels", channels);
            } else {
                AACEncoder encoder = FdkAACLibFacade.openEncoder(ENCODER_MODULES_MASK, MAX_ENCODER_CHANNELS);
                AACEncInfo info = FdkAACLibFacade.getEncoderInfo(encoder);
                if (info != null) {
                    setEncoderParams(encoder);
                    return new AACAudioEncoder(encoder, info);
                } else {
                    throw new AACAudioEncoderException("Could obtain encoder info");
                }
            }
        }
    }

    public final AACAudioOutput encode(WAVAudioInput input, boolean terminal) throws AACAudioEncoderException {
        AACAudioOutput.Accumulator accumlator = AACAudioOutput.accumulator();
        try {
            int dataRead;
            int dataBufferSize = input.channels() * info.frameLength * 2;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(input.data());
            byte[] dataBuffer = new byte[dataBufferSize];
            while ((dataRead = inputStream.read(dataBuffer)) != -1) {
                accumlator.accumulate(FdkAACLibFacade.encode(encoder, dataRead, dataBuffer));
            }
            if (terminal) {
                accumlator.accumulate(FdkAACLibFacade.encode(encoder, dataRead, null));
            }
            return accumlator.done();
        } catch (IOException | RuntimeException x) {
            throw new AACAudioEncoderException("Could not encode WAV audio to AAC audio", x);
        }
    }

    @Override
    public void close() throws Exception {
        FdkAACLibFacade.closeEncoder(encoder);
    }
}