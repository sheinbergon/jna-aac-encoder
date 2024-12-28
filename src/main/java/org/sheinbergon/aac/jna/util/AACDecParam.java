package org.sheinbergon.aac.jna.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Maps to AACDEC_PARAM enum.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACdec/include/aacdecoder_lib.h">fdk-aac/aacdecoder_lib.h</a>
 */
@Getter
@RequiredArgsConstructor
public enum AACDecParam {

  /**
   * Defines how the decoder processes two channel signals.
   * <li> 0: Leave both signals as they are (default).
   * <li> 1: Create a dual mono output signal from channel 1.
   * <li> 2: Create a dual mono output signal from channel 2.
   * <li> 3: Create a dual mono output signal by mixing both channels
   *            (L' = R'(0).5*Ch1 + 0.5*Ch2).
   */
  AAC_PCM_DUAL_CHANNEL_OUTPUT_MODE(0x0002),
  /** 
   * Output buffer channel ordering.
   * <li> 0: MPEG PCE style order,
   * <li> 1: WAV file channel order (default). 
   */
  AAC_PCM_OUTPUT_CHANNEL_MAPPING(0x0003),
  /** 
   * Enable signal level limiting.
   * <li> -1: Auto-config. Enable limiter for all non-lowdelay configurations by default.
   * <li> 0: Disable limiter in general.
   * <li> 1: Enable limiter always. It is recommended to call the decoder with 
   *    a AACDEC_CLRHIST flag to reset all states when the limiter switch is changed explicitly.
   */
  AAC_PCM_LIMITER_ENABLE(0x0004),
  /** 
   * Signal level limiting attack time in ms.
   * Default configuration is 15 ms. Adjustable range from 1 ms to 15 ms.
   */
  AAC_PCM_LIMITER_ATTACK_TIME(0x0005),
  /** 
   * Signal level limiting release time in ms.
   * Default configuration is 50 ms. Adjustable time must be larger than 0 ms.
   */
  AAC_PCM_LIMITER_RELEAS_TIME(0x0006),
  /** 
   * Minimum number of PCM output channels. If higher than the number of encoded audio channels,
   * a simple channel extension is applied (see note 4 for exceptions).
   * <ul>
   * <li> -1, 0: Disable channel extension feature. 
   *             The decoder output contains the same number of channels as the encoded bitstream.
   * <li> 1:    This value is currently needed only together with the mix-down feature.
   *            See {@link #AAC_PCM_MAX_OUTPUT_CHANNELS} and note 2 below.
   * <li> 2:    Encoded mono signals will be duplicated to achieve a 2/0/0.0 channel output configuration.
   * <li> 6:    The decoder tries to reorder encoded signals with less than six channels to
   *            achieve a 3/0/2.1 channel output signal. Missing channels will
   *            be filled with a zero signal. If reordering is not possible the
   *            empty channels will simply be appended. Only available if
   *            instance is configured to support multichannel output.
   * <li> 8:    The decoder tries to reorder encoded signals with less than
   *            eight channels to achieve a 3/0/4.1 channel output signal.
   *            Missing channels will be filled with a zero signal.
   *            If reordering is not possible the empty channels will simply be
   *                      appended. Only available if instance is configured to
   *             support multichannel output.
   * </ul>
   * <p>NOTE:
   * <li> The channel signaling (CStreamInfo::pChannelType and
   *             CStreamInfo::pChannelIndices) will not be modified. Added empty
   *             channels will be signaled with channel type
   *                    AUDIO_CHANNEL_TYPE::ACT_NONE. 
   * <li> If the parameter value is greater than that of
   *             {@link #AAC_PCM_MAX_OUTPUT_CHANNELS} both will be set to the same
   *             value.
   * <li> This parameter will be ignored if the number of encoded
   *             audio channels is greater than 8.
   */
  AAC_PCM_MIN_OUTPUT_CHANNELS(0x0011),
  /** 
   * Maximum number of PCM output channels.
   * If lower than the number of encoded audio channels, downmixing is applied
   * accordingly (see note 5 for exceptions). If dedicated metadata
   * is available in the stream it will be used to achieve better
   * mixing results.
   * <ul>
   * <li> -1, 0: Disable downmixing feature. The
   *             decoder output contains the same number of channels as the
   *             encoded bitstream.
   * <li> 1:    All encoded audio configurations
   *             with more than one channel will be mixed down to one mono
   *             output signal.
   * <li> 2:    The decoder performs a stereo mix-down
   *             if the number encoded audio channels is greater than two. \n 6:
   *             If the number of encoded audio channels is greater than six the
   *             decoder performs a mix-down to meet the target output
   *             configuration of 3/0/2.1 channels. Only available if instance
   *             is configured to support multichannel output. \n 8:    This
   *             value is currently needed only together with the channel
   *             extension feature. See {@link #AAC_PCM_MIN_OUTPUT_CHANNELS} and note 2
   *             below. Only available if instance is configured to support
   *             multichannel output.
   * </ul>
   * <p> NOTE:
   * <li> Down-mixing of any seven or eight channel configuration
   *             not defined in ISO/IEC 14496-3 PDAM 4 is not supported by this
   *             software version.
   * <li> 2. If the parameter value is greater than zero but smaller
   *             than {@link #AAC_PCM_MIN_OUTPUT_CHANNELS} both will be set to same
   *             value.
   * <li> 3. This parameter will be ignored if the number of encoded
   *             audio channels is greater than 8.
   */
  AAC_PCM_MAX_OUTPUT_CHANNELS(0x0012),
  /** See {@link AACMdProfile} for all available values. */
  AAC_METADATA_PROFILE(0x0020),
  /**
   * Defines the time in ms after which all the bitstream associated meta-data
   * (DRC, downmix coefficients, ...) will be reset to default if no update has been received.
   * Negative values disable the feature.
   */
  AAC_METADATA_EXPIRY_TIME(0x0021),
  /** Error concealment: Processing method.
   * <li> 0: Spectral muting.
   * <li> 1: Noise substitution (see ::CONCEAL_NOISE).
   * <li> 2: Energy interpolation (adds additional signal delay of one frame,
   *         see ::CONCEAL_INTER. only some AOTs are supported).
   */
  AAC_CONCEAL_METHOD(0x0100),
  /** 
   * MPEG-4 / MPEG-D Dynamic Range Control (DRC): Scaling factor for boosting gain values.
   * Defines how the boosting DRC factors (conveyed in the bitstream) will be applied to the decoded signal.
   * The valid values range from 0 (don't apply boost factors) to 127 (fully apply boost factors).
   * Default value is 0 for MPEG-4 DRC and 127 for MPEG-D DRC.
   */
  AAC_DRC_BOOST_FACTOR(0x0200),
  /** 
   * MPEG-4 / MPEG-D DRC: Scaling factor for attenuating gain values.
   * Same as {@link #AAC_DRC_BOOST_FACTOR) but for attenuating DRC factors.
   */
  AAC_DRC_ATTENUATION_FACTOR(0x0201),
  /** 
   * MPEG-4 / MPEG-D DRC: Target reference level / decoder target loudness.
   * Defines the level below full-scale (quantized in steps of 0.25dB) to which the output audio signal will be
   * normalized to by the DRC module.
   * <p>The parameter controls loudness normalization for both MPEG-4 DRC and MPEG-D DRC.
   * The valid values range from 40 (-10 dBFS) to 127 (-31.75 dBFS).
   * <p> Example values:
   * <li>        124 (-31 dBFS) for audio/video receivers (AVR) or other
                 devices allowing audio playback with high dynamic range,
   * <li>        96 (-24 dBFS) for TV sets or equivalent devices (default),
   * <li>        64 (-16 dBFS) for mobile devices where the dynamic range of audio playback is restricted.
   * <p>Any value smaller than 0 switches off loudness normalization and MPEG-4 DRC.
   */
  AAC_DRC_REFERENCE_LEVEL(0x0202),
  /**
   * MPEG-4 DRC: En-/Disable DVB specific heavy compression (aka RF mode).
   * If set to 1, the decoder will apply the compression
   * values from the DVB specific ancillary data field.
   * At the same time the MPEG-4 Dynamic Range Control tool will be disabled.
   * By default, heavy compression is disabled.
   */
  AAC_DRC_HEAVY_COMPRESSION(0x0203),
  /**
   * MPEG-4 DRC: Default presentation mode (DRC parameter handling).
   * Defines the handling of the DRC parameters boost factor,
   * attenuation factor and heavy compression, if no presentation mode is indicated in the bitstream.
   * For options, see {@link AACDrcDefaultPresentationModeOptions}.
   * Default: {@link AACDrcDefaultPresentationModeOptions#AAC_DRC_PARAMETER_HANDLING_DISABLED}
   */
  AAC_DRC_DEFAULT_PRESENTATION_MODE(0x0204),
  /**
   * MPEG-4 DRC: Encoder target level for light (ie not heavy) compression.
   * If known, this declares the target reference level that was assumed at the encoder
   * for calculation of limiting gains. The valid values range from 0 (full-scale) to
   * 127 (31.75 dB below full-scale).
   * This parameter is used only with {@link AACDrcDefaultPresentationModeOptions#AAC_DRC_PARAMETER_HANDLING_ENABLED}
   * and ignored otherwise.
   * Default: 127 (worst-case assumption).
   */
  AAC_DRC_ENC_TARGET_LEVEL(0x0205),
  /**
   * MPEG-D DRC: Request a DRC effect type for selection of a DRC set.
   * Supported indices are:
   * <li> 1: DRC off. Completely disables MPEG-D DRC.
   * <li> 0: None (default). Disables MPEG-D DRC,
   *         but automatically enables DRC if necessary to prevent clipping.
   * <li> 1: Late night
   * <li> 2: Noisy environment
   * <li> 3: Limited playback range
   * <li> 4: Low playback level
   * <li> 5: Dialog enhancement
   * <li> 6: General compression. Used for generally enabling MPEG-D DRC without particular request.
   */
  AAC_UNIDRC_SET_EFFECT(0x0206),
  /** 
   * MPEG-D DRC: Enable album mode.
   * <li> 0: Disabled (default),
   * <li> 1: Enabled.
   * <p>Disabled album mode leads to application of gain sequences for fading in and out,
   * if provided in the bitstream.
   * Enabled album mode makes use of dedicated album loudness information, if provided in the bitstream.
   */
  AAC_UNIDRC_ALBUM_MODE(0x0207),
  /**
   * Quadrature Mirror Filter (QMF) Bank processing mode.
   * <li> -1: Use internal default.
   * <li> 0: Use complex QMF data mode.
   * <li> 1: Use real (low power) QMF data mode.
   */
  AAC_QMF_LOWPOWER(0x0300),
  /** 
   * Clear internal bit stream buffer of transport layers.
   * The decoder will start decoding at new data passed after this event
   * and any previous data is discarded.
   */
  AAC_TPDEC_CLEAR_BUFFER(0x0603);

  private final int value;
}
