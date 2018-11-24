package org.sheinbergon.aac.encoder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.encoder.util.WAVAudioInputException;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;

import java.nio.ByteOrder;

@DisplayName("WAV audio input composition")
public class WAVAudioInputTest {

    private final static byte[] VALID_DATA = new byte[4];
    private final static int VALID_LENGTH = 4;
    private final static WAVAudioSupport.SampleSize VALID_SAMPLE_SIZE = WAVAudioSupport.SampleSize._16;
    private final static ByteOrder VALID_ENDIANNESS = ByteOrder.LITTLE_ENDIAN;

    private final static int INVALID_LENGTH = 100;
    private final static ByteOrder INVALID_ENDIANNESS = ByteOrder.BIG_ENDIAN;

    private WAVAudioInput.Builder builder;

    @BeforeEach
    public void setup() {
        builder = WAVAudioInput.builder();
    }

    @Test
    @DisplayName("No/empty WAV data")
    public void NoData() {
        Assertions.assertThrows(WAVAudioInputException.class, () ->
                builder.build());
    }

    @Test
    @DisplayName("Invalid WAV data length")
    public void InvalidLength() {
        Assertions.assertThrows(WAVAudioInputException.class, () ->
                builder.data(VALID_DATA)
                        .length(INVALID_LENGTH).build());
    }

    @Test
    @DisplayName("No sample size")
    public void invalidSampleSize() {
        Assertions.assertThrows(WAVAudioInputException.class, () ->
                builder.data(VALID_DATA)
                        .length(VALID_LENGTH)
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
    @DisplayName("No WAV format")
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
                        .audioFormat(WAVAudioSupport.Format.PCM)
                        .endianness(INVALID_ENDIANNESS)
                        .build());
    }
}
