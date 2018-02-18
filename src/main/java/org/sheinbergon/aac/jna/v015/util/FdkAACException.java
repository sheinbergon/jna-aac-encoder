package org.sheinbergon.aac.jna.v015.util;

public class FdkAACException extends RuntimeException {

    public FdkAACException(AACEncError error, String method) {
        super(String.format("Error %s returned from calling method '%s'", error.name(), method));
    }
}
