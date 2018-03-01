package org.sheinbergon.aac.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AACEncodingProfile {
    AAC_LC(2),
    HE_AAC(5),
    HE_AAC_V2(29),
    AAC_LD(23);
    //AAC_ELD(39) - Not yet supported

    private final int aot;
}