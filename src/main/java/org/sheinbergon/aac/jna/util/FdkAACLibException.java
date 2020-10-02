package org.sheinbergon.aac.jna.util;

public class FdkAACLibException extends RuntimeException {

    /**
     * @param error    the library error
     * @param function the library function call that triggered the error
     */
    public FdkAACLibException(final AACEncError error, final String function) {
        super(String.format("Error %s returned from calling function '%s'", error.name(), function));
    }
}
