package org.sheinbergon.aac.jna.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Maps to AAC_MD_PROFILE enum.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACdec/include/aacdecoder_lib.h">fdk-aac/aacdecoder_lib.h</a>
 */
@Getter
@RequiredArgsConstructor
public enum AACMdProfile {
  /** 
   * The standard profile creates a mixdown signal based on the advanced downmix metadata (from a DSE).
   * The equations and default values are defined in ISO/IEC 14496:3 Ammendment 4.
   * Any other (legacy) downmix metadata will be ignored. No other parameter will be modified.
   */
  AAC_MD_PROFILE_MPEG_STANDARD(0),
  /**
   * This profile behaves identical to the standard profile if advanced downmix metadata (from a DSE) is available.
   * If not, the matrix_mixdown information embedded in the program configuration element (PCE) will be applied.
   * If neither is the case, the module creates a mixdown using the default coefficients as defined in
   * ISO/IEC 14496:3 AMD 4. The profile can be used to support legacy digital TV (e.g. DVB) streams.
   */
  AAC_MD_PROFILE_MPEG_LEGACY(1),
  /**
   * Similar to the {@link #AAC_MD_PROFILE_MPEG_LEGACY} profile but if both
   * the advanced (ISO/IEC 14496:3 AMD 4) and the legacy (PCE) MPEG
   * downmix metadata are available the latter will be applied.
   */
  AAC_MD_PROFILE_MPEG_LEGACY_PRIO(2),
  /** 
   * Downmix creation as described in ABNT NBR 15602-2.
   * But if advanced downmix metadata (ISO/IEC 14496:3 AMD 4) is available
   * it will be preferred because of the higher resolutions.
   * In addition the metadata expiry time will be set to the value defined in the ARIB
   * standard (see {@link AACDecParam#AAC_METADATA_EXPIRY_TIME}).
   */
  AAC_MD_PROFILE_ARIB_JAPAN(3);
  
  private final int value;
}
