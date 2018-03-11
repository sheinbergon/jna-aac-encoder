package org.sheinbergon.aac.jna;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.jna.structure.AACEncoder;
import org.sheinbergon.aac.jna.util.FdkAACLibException;

@DisplayName("Fdk-AAC Library Facade")
public class FdkAACLibFacadeTest {

    private final static int SAMPLE_DATA_LENGTH = 128;
    private final byte[] SAMPLE_DATA = new byte[SAMPLE_DATA_LENGTH];

    @Test
    @DisplayName("Invalid call result verification")
    public void invalidLibCallResultVerification() {
        Assertions.assertThrows(FdkAACLibException.class, () ->
                // Passing a null encoder instance triggers an invalid return code;
                FdkAACLibFacade.encode(null, SAMPLE_DATA_LENGTH, SAMPLE_DATA));
    }
}