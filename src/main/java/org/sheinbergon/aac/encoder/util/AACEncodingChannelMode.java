package org.sheinbergon.aac.encoder.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum AACEncodingChannelMode {
    MODE_INVALID(-1, -1),
    MODE_1(1, 1),
    MODE_2(2, 2),
    MODE_1_2(3, 3),
    MODE_1_2_1(4, 4),
    MODE_1_2_2(5, 5),
    MODE_1_2_2_1(6, 6);

    private final static Map<Integer, AACEncodingChannelMode> countToEnumMap = Stream.of(values())
            .collect(Collectors.toMap(AACEncodingChannelMode::getCount, facm -> facm));

    public static AACEncodingChannelMode valueOf(int count) {
        return countToEnumMap.getOrDefault(count, MODE_INVALID);
    }

    private final int count;
    private final int mode;
}