package org.sheinbergon.aac.jna.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Maps to AACENC_ERROR enum.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
 */
@Getter
@RequiredArgsConstructor
public enum AACEncError implements AACError {

  AACENC_UNKNOWN(-0x0001),
  AACENC_OK(0x0000),
  AACENC_INVALID_HANDLE(0x0020),
  AACENC_MEMORY_ERROR(0x0021),
  AACENC_UNSUPPORTED_PARAMETER(0x0022),
  AACENC_INVALID_CONFIG(0x0023),
  AACENC_INIT_ERROR(0x0040),
  AACENC_INIT_AAC_ERROR(0x0041),
  AACENC_INIT_SBR_ERROR(0x0042),
  AACENC_INIT_TP_ERROR(0x0043),
  AACENC_INIT_META_ERROR(0x0044),
  /** MPS library initialization error. */
  AACENC_INIT_MPS_ERROR(0x0045),
  /** The encoding process was interrupted by an unexpected error. */
  AACENC_ENCODE_ERROR(0x0060),
  AACENC_ENCODE_EOF(0x0080);

  private static final Map<Integer, AACEncError> BY_CODE = Stream.of(values())
      .collect(Collectors.toMap(AACEncError::getValue, err -> err));

  /**
   * Match a {@link AACEncError} from a given numeric error code.
   *
   * @param value numeric library error code
   * @return the matched library error descriptor
   */
  public static AACEncError valueOf(final int value) {
    return BY_CODE.getOrDefault(value, AACENC_UNKNOWN);
  }

  private final int value;
}
