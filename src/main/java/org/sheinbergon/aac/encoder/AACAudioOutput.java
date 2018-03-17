package org.sheinbergon.aac.encoder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import org.sheinbergon.aac.encoder.util.AACAudioOutputException;

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

        void accumulate(byte[] data) {
            if (data.length != 0) {
                this.data = ArrayUtils.addAll(this.data, data);
                this.length += data.length;
            }
        }

        AACAudioOutput done() {
            AACAudioOutput output = new AACAudioOutput();
            if (ArrayUtils.isNotEmpty(data)) {
                output.data = data;
            } else {
                throw new AACAudioOutputException("data", "Empty/Null array");
            }
            output.length = length;
            return output;
        }
    }

    private byte[] data;
    private int length;
}
