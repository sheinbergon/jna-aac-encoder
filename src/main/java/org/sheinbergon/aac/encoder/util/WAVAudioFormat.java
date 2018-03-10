package org.sheinbergon.aac.encoder.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
/*
    WAV header AudioFormat 16 bit value
    ONLY PCM is supported in Fdk-AAC. Other values such as
    - ADPCM((short) 2),
    - IEEE_FLOAT((short) 3)
    - ALAW((short) 6)
    are considered invalid by the Fdk-AAC library
 */
public enum WAVAudioFormat {
    PCM((short) 1);
    private final short value;
}