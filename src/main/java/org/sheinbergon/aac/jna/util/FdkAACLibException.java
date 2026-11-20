package org.sheinbergon.aac.jna.util;

public class FdkAACLibException extends RuntimeException {

  /**
   * @param error    the library error
   * @param function the library function call that triggered the error
   */
  public FdkAACLibException(final AACEncError error, final String function) {
    super(String.format("Error %s returned from calling function '%s'", error.name(), function));
  }

  /**
   * @param code     the library (decoder) error code, see AAC_DECODER_ERROR in aacdecoder_lib.h
   * @param function the library function call that triggered the error
   */
  public FdkAACLibException(final int code, final String function) {
    super(String.format("Error 0x%04x returned from calling function '%s'", code, function));
  }
}
