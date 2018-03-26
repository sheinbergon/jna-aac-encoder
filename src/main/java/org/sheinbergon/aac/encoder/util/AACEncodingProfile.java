package org.sheinbergon.aac.encoder.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AACEncodingProfile {
    AAC_LC(2, "LC"),
    HE_AAC(5, "HE-AAC"),
    HE_AAC_V2(29, "HE-AACv2");
    //AAC_LD(23, "LD" ) - Not yet supported
    //AAC_ELD(39) - Not yet supported

    private final int aot;
    private final String profile;
}