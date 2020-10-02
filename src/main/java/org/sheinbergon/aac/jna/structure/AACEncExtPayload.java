package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to AACENC_EXT_PAYLOAD struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libAACenc/src/aacenc.h">fdk-aac/libAACenc/src/aacenc.h</a>
 */
public class AACEncExtPayload extends Structure {

    private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(AACEncExtPayload.class);

    public ByteByReference pData;
    public int dataSize;
    public int dataType;
    public int associatedChElement;

    @Override
    protected final List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
