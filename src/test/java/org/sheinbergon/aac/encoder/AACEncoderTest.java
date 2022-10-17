package org.sheinbergon.aac.encoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.encoder.util.AACEncoderException;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;

import java.util.Optional;


@SuppressWarnings("MissingJavadocMethod")
@DisplayName("AAC encoder")
public final class AACEncoderTest {

  private static final int INVALID_SAMPLE_RATE = 8000;
  private static final int INVALID_CHANNEL_COUNT = 10;
  private static final int INVALID_HE_AAC_V2_CHANNEL_COUNT = 1;

  private static final int VALID_SAMPLE_RATE = 16000;

  private AACEncoder.Builder builder;
  private AACEncoder encoder;

  @BeforeEach
  public void setup() {
    builder = AACEncoder.builder();
  }

  @AfterEach
  public void teardown() {
    Optional.ofNullable(encoder).ifPresent(AACEncoder::close);
  }

  @Test
  @DisplayName("Invalid sample-rate")
  public void invalidSampleRate() {
    Assertions.assertThrows(AACEncoderException.class, () ->
        builder.sampleRate(INVALID_SAMPLE_RATE)
            .build());
  }

  @Test
  @DisplayName("Invalid channel count")
  public void invalidChannelCount() {
    Assertions.assertThrows(AACEncoderException.class, () ->
        builder.sampleRate(VALID_SAMPLE_RATE)
            .channels(INVALID_CHANNEL_COUNT)
            .build());
  }

  @Test
  @DisplayName("Invalid HE-AACv2 configuration")
  public void invalidHEAACV2configuration() {
    Assertions.assertThrows(AACEncoderException.class, () -> builder.sampleRate(VALID_SAMPLE_RATE)
        .profile(AACEncodingProfile.HE_AAC_V2)
        .channels(INVALID_HE_AAC_V2_CHANNEL_COUNT)
        .build());
  }

  @Test
  @DisplayName("Stale/closed encoder encoding")
  public void closedEncoderEncoding() {
    Assertions.assertThrows(AACEncoderException.class, () -> {
      encoder = builder.build();
      encoder.close();
      encoder.conclude();
    });
  }
}
