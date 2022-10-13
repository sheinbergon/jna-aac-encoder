package org.sheinbergon.aac.encoder.util;

public class AACAudioEncoderException extends RuntimeException {
  /**
   * @param message the error message
   * @param cause   the error cause
   */
  public AACAudioEncoderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message the error message
   */
  public AACAudioEncoderException(final String message) {
    super(message);
  }

  /**
   * @param parameter the erroneous parameter name
   * @param value     the erroneous parameter value
   */
  public AACAudioEncoderException(final String parameter, final int value) {
    super(String.format("Invalid encoder parameter '%s' - %d", parameter, value));
  }
}

