package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import lombok.ToString;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;

/**
 * Maps to AACENC_EXT_PAYLOAD struct in @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.5/libAACenc/src/aacenc.h">fdk-aac/libAACenc/src/aacenc.h</a>
 */
public class AACEncExtPayload extends Structure {

    private final static List<String> FIELD_ORDER = JNAUtil.structureFieldOrder(AACEncExtPayload.class);

    public ByteByReference pData;
    public int dataSize;
    public int dataType;
    public int associatedChElement;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}