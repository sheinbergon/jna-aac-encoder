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
        private int size = 0;

        Accumulator accumulate(byte[] data) {
            this.data = ArrayUtils.addAll(this.data);
            this.size += data.length;
            return this;
        }

        AACAudioOutput done() {
            AACAudioOutput output = new AACAudioOutput();
            if (ArrayUtils.isNotEmpty(data)) {
                output.data = data;
            } else {
                throw new AACAudioOutputException("data", "Empty/Null array");
            }
            if (size >= 0 && size <= data.length) {
                output.size = size;
            } else {
                throw new AACAudioOutputException("size", String.valueOf(size));
            }
            return output;
        }
    }

    private byte[] data;
    private int size;
}
