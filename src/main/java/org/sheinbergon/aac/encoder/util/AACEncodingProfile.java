package org.sheinbergon.aac.encoder.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum AACEncodingProfile {
    AAC_LC(2, "LC" ),
    HE_AAC(5, "HE" ),
    HE_AAC_V2(29, "HE_V2" ),
    AAC_LD(23, "LD" );
    //AAC_ELD(39) - Not yet supported
    
    private final int aot;
    private final String profile;
}