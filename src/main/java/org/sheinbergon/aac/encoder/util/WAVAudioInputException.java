package org.sheinbergon.aac.encoder.util;

public class WAVAudioInputException extends RuntimeException {

    /**
     * @param paramter
     * @param value
     */
    public WAVAudioInputException(final String paramter, final String value) {
        super(String.format("Invalid WAV input: '%s' - %s", paramter, value));
    }
}
