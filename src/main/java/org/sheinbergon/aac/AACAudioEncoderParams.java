package org.sheinbergon.aac;

import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Range;
import org.sheinbergon.aac.util.AACChannelMode;
import org.sheinbergon.aac.util.AACAudioEncoderParamsException;
import org.sheinbergon.aac.util.AACProfile;

import java.util.Set;

@Getter(AccessLevel.PACKAGE)
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AACAudioEncoderParams {

    public final static AACAudioEncoderParams DEFAULT = builder().build();

    /**
     * Safe boundaries according to @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.5/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
     */
    private final static Range<Integer> BITRATE_RANGE = Range.between(64000, 320000);
    private final static Set<Integer> SAMPLE_RATES = Set.of(8000, 11025, 12000, 16000, 22050, 24000, 32000, 44100, 48000);

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

        public AACAudioEncoderParams build() {
            AACAudioEncoderParams specification = new AACAudioEncoderParams();
            AACChannelMode channelMode;
            if (SAMPLE_RATES.contains(sampleRate)) {
                specification.sampleRate = sampleRate;
            } else {
                throw new AACAudioEncoderParamsException("sampleRate", sampleRate);
            }
            if (BITRATE_RANGE.contains(bitRate)) {
                specification.bitRate = bitRate;
            } else {
                throw new AACAudioEncoderParamsException("bitRate", bitRate);
            }
            if ((channelMode = AACChannelMode.valueOf(channels)) != AACChannelMode.MODE_INVALID) {
                specification.channelMode = channelMode.getMode();
            } else {
                throw new AACAudioEncoderParamsException("channels", channels);
            }
            specification.afterBurner = afterBurner ? 1 : 0;
            specification.aot = profile.getAot();
            return specification;
        }
    }

    private int afterBurner;
    private int aot;
    private int channelMode;
    private int bitRate;
    private int sampleRate;

}
