package org.sheinbergon.aac.encoder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.encoder.util.WAVAudioFormat;
import org.sheinbergon.aac.encoder.util.WAVAudioInputException;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;

import java.nio.ByteOrder;

@DisplayName("WAV audio input composition")
public final class WAVAudioInputTest {

    private static final byte[] VALID_DATA = new byte[4];
    private static final int VALID_LENGTH = 4;
    private static final int VALID_SAMPLE_SIZE = WAVAudioSupport.SUPPORTED_SAMPLE_SIZE;
    private static final ByteOrder VALID_ENDIANNESS = ByteOrder.LITTLE_ENDIAN;

    private static final int INVALID_LENGTH = 100;
    private static final int INVALID_SAMPLE_SIZE = 24;
    private static final ByteOrder INVALID_ENDIANNESS = ByteOrder.BIG_ENDIAN;

    private WAVAudioInput.Builder builder;

    @BeforeEach
    public void setup() {
        builder = WAVAudioInput.builder();
    }

    @Test
    @DisplayName("No/empty WAV data")
    public void noData() {
        Assertions.assertThrows(WAVAudioInputException.class, () ->
                builder.build());
    }

    @Test
    @DisplayName("Invalid WAV data length")
    public void invalidLength() {
        Assertions.assertThrows(WAVAudioInputException.class, () ->
                builder.data(VALID_DATA)
                        .length(INVALID_LENGTH).build());
    }

    @Test
    @DisplayName("Invalid input sample size")
    public void invalidSampleSize() {
        Assertions.assertThrows(WAVAudioInputException.class, () ->
                builder.data(VALID_DATA)
                        .length(VALID_LENGTH)
                        .sampleSize(INVALID_SAMPLE_SIZE)
                        .build());
    }

    @Test
    @DisplayName("Invalid channel count")
    public void invalidChannelCount() {
        Assertions.assertThrows(WAVAudioInputException.class, () ->
                builder.data(VALID_DATA)
                        .length(VALID_LENGTH)
                        .sampleSize(VALID_SAMPLE_SIZE)
                        .build());
    }


    @Test
    @DisplayName("Invalid WAV format")
    public void invalidWAVFormat() {
        Assertions.assertThrows(WAVAudioInputException.class, () ->
                builder.data(VALID_DATA)
                        .length(VALID_LENGTH)
                        .sampleSize(VALID_SAMPLE_SIZE)
                        .endianness(VALID_ENDIANNESS)
                        .build());
    }

    @Test
    @DisplayName("Invalid byte order")
    public void invalidEndianness() {
        Assertions.assertThrows(WAVAudioInputException.class, () ->
                builder.data(VALID_DATA)
                        .length(VALID_LENGTH)
                        .sampleSize(VALID_SAMPLE_SIZE)
                        .audioFormat(WAVAudioFormat.PCM)
                        .endianness(INVALID_ENDIANNESS)
                        .build());
    }
}
