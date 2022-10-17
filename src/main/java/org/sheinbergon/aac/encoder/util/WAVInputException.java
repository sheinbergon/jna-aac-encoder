package org.sheinbergon.aac.encoder.util;

public class WAVInputException extends RuntimeException {

  /**
   * @param parameter the audio input parameter name that caused the error
   * @param value     the audio input parameter value that caused the error
   */
  public WAVInputException(final String parameter, final String value) {
    super(String.format("Invalid WAV input: '%s' - %s", parameter, value));
  }
}
