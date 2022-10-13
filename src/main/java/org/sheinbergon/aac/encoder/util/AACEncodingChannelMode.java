package org.sheinbergon.aac.encoder.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum AACEncodingChannelMode {
  MODE_INVALID(-1, -1),
  MODE_1(1, 1),
  MODE_2(2, 2),
  MODE_1_2(3, 3),
  MODE_1_2_1(4, 4),
  MODE_1_2_2(5, 5),
  MODE_1_2_2_1(6, 6);

  private static final Map<Integer, AACEncodingChannelMode> BY_CHANNEL_COUNT = Stream.of(values())
      .collect(Collectors.toMap(AACEncodingChannelMode::count, facm -> facm));

  /**
   * Match an {@link AACEncodingChannelMode} from a channel count.
   *
   * @param count the channel count to match
   * @return the matching encoding channel-mode
   */
  public static AACEncodingChannelMode valueOf(final int count) {
    return BY_CHANNEL_COUNT.getOrDefault(count, MODE_INVALID);
  }

  private final int count;
  private final int mode;
}
