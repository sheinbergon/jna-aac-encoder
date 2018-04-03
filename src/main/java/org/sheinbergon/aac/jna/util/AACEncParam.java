package org.sheinbergon.aac.jna.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
/**
 * Maps to AACENC_PARAM enum in @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
 */
public enum AACEncParam {

    AACENC_AOT(0x0100),
    AACENC_BITRATE(0x0101),
    AACENC_BITRATEMODE(0x0102),
    AACENC_SAMPLERATE(0x0103),
    AACENC_SBR_MODE(0x0104),
    AACENC_GRANULE_LENGTH(0x0105),
    AACENC_CHANNELMODE(0x0106),
    AACENC_CHANNELORDER(0x0107),
    AACENC_SBR_RATIO(0x0108),
    AACENC_AFTERBURNER(0x0200),
    AACENC_BANDWIDTH(0x0203),
    AACENC_PEAK_BITRATE(0x0207),
    AACENC_TRANSMUX(0x0300),
    AACENC_HEADER_PERIOD(0x0301),
    AACENC_SIGNALING_MODE(0x0302),
    AACENC_TPSUBFRAMES(0x0303),
    AACENC_AUDIOMUXVER(0x0304),
    AACENC_PROTECTION(0x0306),
    AACENC_ANCILLARY_BITRATE(0x0500),
    AACENC_METADATA_MODE(0x0600),
    AACENC_CONTROL_STATE(0xFF00),
    AACENC_NONE(0xFFFF);

    private final int value;
}
