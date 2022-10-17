package org.sheinbergon.aac.encoder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import org.sheinbergon.aac.encoder.util.WAVInputException;

import javax.annotation.Nonnull;
import java.util.Arrays;

@Getter
@Accessors(chain = true, fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WAVInput {

  /**
   * Wrap the given input parameters with a  {@link WAVInput}.
   *
   * @param data   wav audio bytes
   * @param length guaranteed read length
   * @return the valid audio-input instance
   * @throws WAVInputException if the input paramters given did not make sense
   */
  @Nonnull
  public static WAVInput wrap(final byte[] data, final int length) {
    if (ArrayUtils.isEmpty(data)) {
      throw new WAVInputException("data", "Empty/Null array");
    } else if (length < 0 || length > data.length) {
      throw new WAVInputException("length", String.valueOf(length));
    } else {
      return new WAVInput(Arrays.copyOf(data, length));
    }
  }

  private final byte[] data;
}
