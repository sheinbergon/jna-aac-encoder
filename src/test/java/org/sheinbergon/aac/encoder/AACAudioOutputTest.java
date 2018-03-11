package org.sheinbergon.aac.encoder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.encoder.util.AACAudioOutputException;

@DisplayName("WAV audio input composition")
public class AACAudioOutputTest {

    private final static byte[] VALID_DATA = new byte[4];
    private final static int VALID_LENGTH = 4;


    private final static int INVALID_LENGTH = 100;

    private AACAudioOutput.Accumulator accumulator;

    @BeforeEach
    public void setup() {
        accumulator = AACAudioOutput.accumulator();
    }

    @Test
    @DisplayName("No/empty AAC data")
    public void NoData() {
        Assertions.assertThrows(AACAudioOutputException.class, () ->
                accumulator.done());
    }
}
