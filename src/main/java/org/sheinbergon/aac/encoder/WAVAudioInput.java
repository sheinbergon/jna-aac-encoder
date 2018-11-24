package org.sheinbergon.aac.encoder;

import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import org.sheinbergon.aac.encoder.util.WAVAudioInputException;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;

import javax.annotation.Nonnull;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;

@Getter(AccessLevel.PACKAGE)
@Accessors(chain = true, fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WAVAudioInput {

    public static WAVAudioInput pcms16le(byte[] data, int length) {
        return builder().audioFormat(WAVAudioSupport.Format.PCM)
                .endianness(ByteOrder.LITTLE_ENDIAN)
                .sampleSize(WAVAudioSupport.SampleSize._16)
                .data(data)
                .length(length)
                .build();
    }

    public static WAVAudioInput pcms24le(byte[] data, int length) {
        return builder().audioFormat(WAVAudioSupport.Format.PCM)
                .endianness(ByteOrder.LITTLE_ENDIAN)
                .sampleSize(WAVAudioSupport.SampleSize._24)
                .data(data)
                .length(length)
                .build();
    }

    static Builder builder() {
        return new Builder();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static class Builder {

        // Default values
        private byte[] data = null;
        private int length = 0;
        private WAVAudioSupport.SampleSize sampleSize = null;
        private ByteOrder endianness = null;
        private WAVAudioSupport.Format audioFormat = null;

        WAVAudioInput build() {
            if (ArrayUtils.isEmpty(data)) {
                throw new WAVAudioInputException("data", "Empty/Null array");
            } else if (length < 0 || length > data.length) {
                throw new WAVAudioInputException("length", String.valueOf(length));
            } else if (Objects.isNull(sampleSize)) {
                throw new WAVAudioInputException("sampleSize", "Null value");
            } else if (Objects.isNull(audioFormat)) {
                throw new WAVAudioInputException("audioFormat", "Null value");
            } else if (!Objects.equals(endianness, ByteOrder.LITTLE_ENDIAN)) {
                throw new WAVAudioInputException("endianness", String.valueOf(endianness));
            } else {
                return new WAVAudioInput(Arrays.copyOf(data, length), sampleSize);
            }
        }
    }

    @Nonnull
    private final byte[] data;
    @Nonnull
    private final WAVAudioSupport.SampleSize sampleSize;
}
