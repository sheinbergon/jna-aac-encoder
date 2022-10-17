package org.sheinbergon.aac.encoder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.encoder.util.WAVInputException;

@SuppressWarnings({"MissingJavadocMethod", "MagicNumber"})
@DisplayName("WAV input composition")
public final class WAVInputTest {

  @Test
  @DisplayName("No/empty WAV data")
  public void noData() {
    Assertions.assertThrows(WAVInputException.class, () ->
        WAVInput.wrap(new byte[0], 0));
  }

  @Test
  @DisplayName("Invalid WAV data length")
  public void invalidLength() {
    Assertions.assertThrows(WAVInputException.class, () ->
        WAVInput.wrap(new byte[] {1, 2}, 5));
  }
}
