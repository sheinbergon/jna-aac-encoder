package org.sheinbergon.aac.encoder;

import org.junit.jupiter.api.*;
import org.sheinbergon.aac.encoder.util.AACAudioEncoderException;

import java.util.Optional;


@DisplayName("AAC audio encoder")
public class AACAudioEncoderTest {

    private final static int INVALID_SAMPLE_RATE = 4000;
    private final static int INVALID_BITRATE = 6000;
    private final static int INVALID_CHANNEL_COUNT = 10;

    private final static int VALID_SAMPLE_RATE = 8000;
    private final static int VALID_BITRATE = 64000;

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
    public void invalidSampleRate() {
        Assertions.assertThrows(AACAudioEncoderException.class, () ->
                builder.sampleRate(INVALID_SAMPLE_RATE)
                        .build());
    }

    @Test
    public void invalidBitRate() {
        Assertions.assertThrows(AACAudioEncoderException.class, () ->
                builder.sampleRate(VALID_SAMPLE_RATE)
                        .bitRate(INVALID_BITRATE)
                        .build());
    }

    @Test
    public void invalidChannelCount() {
        Assertions.assertThrows(AACAudioEncoderException.class, () ->
                builder.sampleRate(VALID_SAMPLE_RATE)
                        .bitRate(VALID_BITRATE)
                        .channels(INVALID_CHANNEL_COUNT)
                        .build());
    }

    @Test
    public void closedEncoderEncoding() {
        Assertions.assertThrows(AACAudioEncoderException.class, () -> {
            encoder = builder.build();
            encoder.close();
            encoder.conclude();
        });
    }
}
