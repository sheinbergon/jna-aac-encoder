package org.sheinbergon.aac.encoder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import org.sheinbergon.aac.encoder.util.WAVAudioFormat;
import org.sheinbergon.aac.encoder.util.WAVAudioInputException;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;

import javax.annotation.Nonnull;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;

@Getter
@Accessors(chain = true, fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WAVAudioInput {

  private static final WAVAudioFormat SUPPORTED_AUDIO_FORMATS = WAVAudioFormat.PCM;

  /**
   * WAV audio input descriptor factory method aligning to the pcm_s16le standard.
   *
   * @param data   raw audio data bytes
   * @param length valid bytes count
   * @return returns the audio-input descriptor
   */
  @Nonnull
  public static WAVAudioInput pcms16le(final byte[] data, final int length) {
    return builder().audioFormat(WAVAudioFormat.PCM)
        .endianness(ByteOrder.LITTLE_ENDIAN)
        .sampleSize(WAVAudioSupport.SUPPORTED_SAMPLE_SIZE)
        .data(data)
        .length(length)
        .build();
  }

  /**
   * Create a {@link WAVAudioInput.Builder} instance.
   *
   * @return returns the builder instance
   */
  @Nonnull
  public static Builder builder() {
    return new Builder();
  }

  @Setter
  @Accessors(chain = true, fluent = true)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {

    // Default values
    private byte[] data = null;
    private int length = 0;
    private int sampleSize = 0;
    private ByteOrder endianness = null;
    private WAVAudioFormat audioFormat = null;

    /**
     * Build, verify and assert a {@link WAVAudioInput} instance.
     *
     * @return the valid audio-input instance
     */
    @Nonnull
    public WAVAudioInput build() {
      if (ArrayUtils.isEmpty(data)) {
        throw new WAVAudioInputException("data", "Empty/Null array");
      } else if (length < 0 || length > data.length) {
        throw new WAVAudioInputException("length", String.valueOf(length));
      } else if (sampleSize != WAVAudioSupport.SUPPORTED_SAMPLE_SIZE) {
        throw new WAVAudioInputException("sampleSize", String.valueOf(sampleSize));
      } else if (!Objects.equals(audioFormat, WAVAudioFormat.PCM)) {
        throw new WAVAudioInputException("audioFormat", String.valueOf(audioFormat));
      } else if (!Objects.equals(endianness, ByteOrder.LITTLE_ENDIAN)) {
        throw new WAVAudioInputException("endianness", String.valueOf(endianness));
      } else {
        return new WAVAudioInput(Arrays.copyOf(data, length));
      }
    }
  }

  private final byte[] data;
}
