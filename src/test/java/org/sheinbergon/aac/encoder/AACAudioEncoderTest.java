package org.sheinbergon.aac.encoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.encoder.util.AACAudioEncoderException;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;

import java.util.Optional;


@DisplayName("AAC audio encoder")
public final class AACAudioEncoderTest {

    private static final int INVALID_SAMPLE_RATE = 8000;
    private static final int INVALID_CHANNEL_COUNT = 10;
    private static final int INVALID_HE_AAC_V2_CHANNEL_COUNT = 1;

    private static final int VALID_SAMPLE_RATE = 16000;

    private AACAudioEncoder.Builder builder;
    private AACAudioEncoder encoder;

    @BeforeEach
    public void setup() {
        builder = AACAudioEncoder.builder();
    }

    @AfterEach
    public void teardown() {
        Optional.ofNullable(encoder).ifPresent(AACAudioEncoder::close);
    }

    @Test
    @DisplayName("Invalid sample-rate")
    public void invalidSampleRate() {
        Assertions.assertThrows(AACAudioEncoderException.class, () ->
                builder.sampleRate(INVALID_SAMPLE_RATE)
                        .build());
    }

    @Test
    @DisplayName("Invalid channel count")
    public void invalidChannelCount() {
        Assertions.assertThrows(AACAudioEncoderException.class, () ->
                builder.sampleRate(VALID_SAMPLE_RATE)
                        .channels(INVALID_CHANNEL_COUNT)
                        .build());
    }

    @Test
    @DisplayName("Invalid HE-AACv2 configuration")
    public void invalidHEAACV2configuration() {
        Assertions.assertThrows(AACAudioEncoderException.class, () ->
                builder.sampleRate(VALID_SAMPLE_RATE)
                        .profile(AACEncodingProfile.HE_AAC_V2)
                        .channels(INVALID_HE_AAC_V2_CHANNEL_COUNT)
                        .build());
    }

    @Test
    @DisplayName("Stale/closed encoder encoding")
    public void closedEncoderEncoding() {
        Assertions.assertThrows(AACAudioEncoderException.class, () -> {
            encoder = builder.build();
            encoder.close();
            encoder.conclude();
        });
    }
}
