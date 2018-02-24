package org.sheinbergon.aac.util;

public class FdkAACEncoderParamsException extends RuntimeException {

    public FdkAACEncoderParamsException(String paramter, int value) {
        super(String.format("Invalid encoder parameter '%s' - %d", paramter, value));
    }
}
