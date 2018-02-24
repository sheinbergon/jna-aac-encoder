package org.sheinbergon.aac.util;

public class AACAudioEncoderParamsException extends RuntimeException {

    public AACAudioEncoderParamsException(String paramter, int value) {
        super(String.format("Invalid encoder parameter '%s' - %d", paramter, value));
    }
}
