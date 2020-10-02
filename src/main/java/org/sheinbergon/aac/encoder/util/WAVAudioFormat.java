package org.sheinbergon.aac.encoder.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * WAV header AudioFormat 16 bit value.
 * <p>
 * ONLY PCM is supported in Fdk-AAC. Other values such as
 * <ul>
 * <li>ADPCM((short) 2) </li>
 * <li>IEEE_FLOAT((short) 3) </li>
 * <li>ALAW((short) 6) </li>
 * </ul>
 * are considered invalid by the Fdk-AAC library
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum WAVAudioFormat {
    PCM((short) 1);
    private final short value;
}
