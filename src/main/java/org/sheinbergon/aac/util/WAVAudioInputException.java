package org.sheinbergon.aac.util;

public class WAVAudioInputException extends RuntimeException {

    public WAVAudioInputException(String paramter, String value) {
        super(String.format("Invalid WAV input: '%s' - %s", paramter, value));
    }
}
