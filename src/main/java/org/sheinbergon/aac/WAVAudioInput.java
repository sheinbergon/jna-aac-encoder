package org.sheinbergon.aac;

import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Range;
import org.sheinbergon.aac.util.WAVAudioInputException;
import org.sheinbergon.aac.util.WAVAudioFormat;

import java.util.Objects;

@Getter
@Accessors(chain = true, fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WAVAudioInput {

    private final static Integer SUPPORTED_SAMPLE_SIZE = 16;
    private final static WAVAudioFormat SUPPORTED_AUDIO_FORMATS = WAVAudioFormat.PCM;
    private final static Range<Integer> SUPPORTED_CHANNELS_RANGE = Range.between(1, 6);

    public static WAVAudioInput pcms16le(byte[] data, int size) {
        return builder().audioFormat(WAVAudioFormat.PCM)
                .channels(2)
                .sampleSize(SUPPORTED_SAMPLE_SIZE)
                .data(data)
                .size(size)
                .build();
    }


    public static Builder builder() {
        return new Builder();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        // Default values
        private byte[] data = null;
        private int size = 0;
        private int sampleSize = 0;
        private int channels = 0;
        private WAVAudioFormat audioFormat = null;

        public WAVAudioInput build() {
            if (ArrayUtils.isEmpty(data)) {
                throw new WAVAudioInputException("data", "Empty/Null array");
            } else if (size < 0 || size > data.length) {
                throw new WAVAudioInputException("size", String.valueOf(size));
            } else if (sampleSize != SUPPORTED_SAMPLE_SIZE) {
                throw new WAVAudioInputException("sampleSize", String.valueOf(sampleSize));
            } else if (!SUPPORTED_CHANNELS_RANGE.contains(channels)) {
                throw new WAVAudioInputException("channels", String.valueOf(channels));
            } else if (!Objects.equals(audioFormat, WAVAudioFormat.PCM)) {
                throw new WAVAudioInputException("audioFormat", String.valueOf(audioFormat));
            } else {
                return new WAVAudioInput(data, channels);
            }
        }
    }

    private final byte[] data;
    private final int channels;
}
