package org.sheinbergon.aac.encoder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;

@Getter
@Accessors(chain = true, fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AACAudioOutput {

    /**
     * Return an {@link Accumulator} instance, used to retain encoded AAC audio bytes across various call to the
     * library encode endpoint.
     *
     * @return the accumulator instance.
     */
    @Nonnull
    public static Accumulator accumulator() {
        return new Accumulator();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static class Accumulator {

        // Default values
        private byte[] data = null;
        private int length = 0;

        // It is guaranteed the encoded bytes array is properly sized to exactly match its actual content
        void accumulate(final @Nonnull byte[] bytes) {
            if (bytes.length != 0) {
                this.data = ArrayUtils.addAll(this.data, bytes);
                this.length += bytes.length;
            }
        }

        AACAudioOutput done() {
            return new AACAudioOutput(data, length);
        }
    }

    private final byte[] data;
    private final int length;
}
