package org.sheinbergon.aac.jna;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.jna.structure.AACEncBufDesc;
import org.sheinbergon.aac.jna.structure.AACEncInArgs;
import org.sheinbergon.aac.jna.structure.AACEncOutArgs;
import org.sheinbergon.aac.jna.util.FdkAACLibException;

@SuppressWarnings("MissingJavadocMethod")
@DisplayName("Fdk-AAC Library Facade")
public final class FdkAACLibFacadeTest {

  private static final int SAMPLE_DATA_LENGTH = 128;
  private static final byte[] SAMPLE_DATA = new byte[SAMPLE_DATA_LENGTH];

  @Test
  @DisplayName("Invalid call result verification")
  public void invalidLibCallResultVerification() {
    // Passing a null encoder instance triggers an invalid return code;
    Assertions.assertThrows(FdkAACLibException.class, () ->
        FdkAACLibFacade.encode(null,
            new AACEncBufDesc(),
            new AACEncBufDesc(),
            new AACEncInArgs(),
            new AACEncOutArgs(),
            SAMPLE_DATA_LENGTH));
  }
}
