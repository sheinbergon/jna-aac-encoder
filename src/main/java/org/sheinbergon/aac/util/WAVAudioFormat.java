package org.sheinbergon.aac.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum WAVAudioFormat {
    PCM((short) 1),
    ADPCM((short) 2),
    IEEE_FLOAT((short) 3),
    ALAW((short) 6),
    MULAW((short) 7);

    private final short value;

    private final Map<Short, WAVAudioFormat> valueToEnumMap = Arrays
            .stream(values())
            .collect(Collectors.toMap(WAVAudioFormat::getValue, waf -> waf));
}