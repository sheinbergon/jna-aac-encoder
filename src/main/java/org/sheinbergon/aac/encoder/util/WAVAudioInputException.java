package org.sheinbergon.aac.encoder.util;

public class WAVAudioInputException extends RuntimeException {
    
    public WAVAudioInputException(String message) {
        super(message);
    }

    public WAVAudioInputException(String paramter, String value) {
        super(String.format("Invalid WAV input: '%s' - %s", paramter, value));
    }
}
