package org.sheinbergon.aac.util;

public class AACAudioEncoderException extends RuntimeException {
    public AACAudioEncoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public AACAudioEncoderException(String message) {
        super(message);
    }

    public AACAudioEncoderException(String paramter, int value) {
        super(String.format("Invalid encoder parameter '%s' - %d", paramter, value));
    }
}
