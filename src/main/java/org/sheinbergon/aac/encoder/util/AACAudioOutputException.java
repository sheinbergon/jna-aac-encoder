package org.sheinbergon.aac.encoder.util;

public class AACAudioOutputException extends RuntimeException {

    public AACAudioOutputException(String paramter, String value) {
        super(String.format("Invalid AAC output : '%s' - %s", paramter, value));
    }
}
