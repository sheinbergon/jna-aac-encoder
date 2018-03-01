package org.sheinbergon.aac.jna.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
/**
 * Maps to AACENC_ERROR enum in @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.5/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
 */
public enum AACEncError {

    AACENC_OK(0x0000),
    AACENC_INVALID_HANDLE(0x0020),
    AACENC_MEMORY_ERROR(0x0021),
    AACENC_UNSUPPORTED_PARAMETER(0x0022),
    AACENC_INVALID_CONFIG(0x0023),
    AACENC_INIT_ERROR(0x0040),
    AACENC_INIT_AAC_ERROR(0x0041),
    AACENC_INIT_SBR_ERROR(0x0042),
    AACENC_INIT_TP_ERROR(0x0043),
    AACENC_INIT_META_ERROR(0x0044),
    AACENC_ENCODE_ERROR(0x0060),
    AACENC_ENCODE_EOF(0x0080);

    private final static Map<Integer, AACEncError> valueToEnumMap = new HashMap<>();

    static {
        Stream.of(AACEncError.values()).forEach(constant -> valueToEnumMap.put(constant.value, constant));
    }

    public static AACEncError valueOf(Integer value) {
        return valueToEnumMap.get(value);
    }

    private final int value;
}