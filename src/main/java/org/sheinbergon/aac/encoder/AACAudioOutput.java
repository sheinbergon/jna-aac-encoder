package org.sheinbergon.aac.encoder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;

@Getter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AACAudioOutput {

    public static Accumulator accumulator() {
        return new Accumulator();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static class Accumulator {

        // Default values
        private byte[] data = null;
        private int length = 0;

        // It is guaranteed the encoded bytes array is properly sized to exactly match its actual content
        void accumulate(byte[] data) {
            if (data.length != 0) {
                this.data = ArrayUtils.addAll(this.data, data);
                this.length += data.length;
            }
        }

        AACAudioOutput done() {
            AACAudioOutput output = new AACAudioOutput();
            output.data = data;
            output.length = length;
            return output;
        }
    }

    private byte[] data;
    private int length;
}
