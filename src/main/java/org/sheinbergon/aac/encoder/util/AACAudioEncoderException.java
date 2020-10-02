package org.sheinbergon.aac.encoder.util;

public class AACAudioEncoderException extends RuntimeException {
    /**
     * @param message
     * @param cause
     */
    public AACAudioEncoderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public AACAudioEncoderException(final String message) {
        super(message);
    }

    /**
     * @param paramter
     * @param value
     */
    public AACAudioEncoderException(final String paramter, final int value) {
        super(String.format("Invalid encoder parameter '%s' - %d", paramter, value));
    }
}

