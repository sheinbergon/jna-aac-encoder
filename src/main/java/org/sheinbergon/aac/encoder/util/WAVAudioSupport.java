package org.sheinbergon.aac.encoder.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WAVAudioSupport {

    public final static Set<Float> SUPPORTED_SAMPLE_RATES = Set.of(16000.0f, 22050.0f, 24000.0f, 32000.0f, 44100.0f, 48000.0f);

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum SampleSize {
        _24(24),
        _16(16);

        public final static int _24_BITS = 24;
        public final static int _16_BITS = 16;

        private final int bits;

        private final static Map<Integer, SampleSize> byBits = Arrays
                .stream(values())
                .collect(Collectors.toMap(SampleSize::bits, size -> size));

        public static boolean valid(int bits) {
            return byBits.containsKey(bits);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    /*  WAV header AudioFormat 16 bit value
     *  ONLY PCM is supported in Fdk-AAC. Other values such as
     *  - ADPCM((short) 2),
     *  - IEEE_FLOAT((short) 3)
     *  - ALAW((short) 6)
     *  are considered invalid by the Fdk-AAC library
     */
    public enum Format {
        PCM((short) 1);
        private final short value;
    }

    public final static Range<Integer> SUPPORTED_CHANNELS_RANGE = Range.between(1, 6);

    public final static int EOS = -1;

    /* Input is assumed to be comprised out of 24 bit samples, meaning each samples is 3 bytes long.
     * This means the input array data length is expected to be a multiple of 3.
     * Input is also always assumed to be have little-endian byte-order, because that's how WAV files are rolling
     * these days.
     */
    public static byte[] downsample24To16Bits(@Nonnull byte[] data) {
        val buffer = ByteBuffer
                .allocate(data.length * 2 / 3) // allocate only 2/3 of the given input size as 8 bits of each samples will be discarded
                .order(ByteOrder.LITTLE_ENDIAN); // Maintain WAV byte-order
        for (int index = 0; index < data.length; index += 3) {
            buffer.put(data, index + 1, 2);
        }
        return buffer.rewind().array();
    }
}