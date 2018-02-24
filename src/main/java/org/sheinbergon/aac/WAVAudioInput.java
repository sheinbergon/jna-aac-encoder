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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
            WAVAudioInput input = new WAVAudioInput();
            if (ArrayUtils.isNotEmpty(data)) {
                input.data = data;
            } else {
                throw new WAVAudioInputException("data", "Empty/Null array");
            }
            if (size >= 0 && size <= data.length) {
                input.size = size;
            } else {
                throw new WAVAudioInputException("size", String.valueOf(size));
            }
            if (sampleSize == SUPPORTED_SAMPLE_SIZE) {
                input.sampleSize = sampleSize;
            } else {
                throw new WAVAudioInputException("sampleSize", String.valueOf(sampleSize));
            }
            if (SUPPORTED_CHANNELS_RANGE.contains(channels)) {
                input.channels = channels;
            } else {
                throw new WAVAudioInputException("channels", String.valueOf(channels));
            }
            if (Objects.equals(audioFormat, WAVAudioFormat.PCM)) {
                input.audioFormat = audioFormat;
            } else {
                throw new WAVAudioInputException("audioFormat", String.valueOf(audioFormat));
            }
            return input;
        }
    }

    private byte[] data;
    private int size;
    private int sampleSize;
    private int channels;
    private WAVAudioFormat audioFormat;
}
