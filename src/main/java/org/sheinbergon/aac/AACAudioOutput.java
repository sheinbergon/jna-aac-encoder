package org.sheinbergon.aac;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import org.sheinbergon.aac.util.AACAudioOutputException;

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
            this.data = ArrayUtils.addAll(this.data,data);
            this.length += data.length;
        }

        AACAudioOutput done() {
            AACAudioOutput output = new AACAudioOutput();
            if (ArrayUtils.isNotEmpty(data)) {
                output.data = data;
            } else {
                throw new AACAudioOutputException("data", "Empty/Null array");
            }
            if (length >= 0 && length <= data.length) {
                output.length = length;
            } else {
                throw new AACAudioOutputException("length", String.valueOf(length));
            }
            return output;
        }
    }

    private byte[] data;
    private int length;
}
