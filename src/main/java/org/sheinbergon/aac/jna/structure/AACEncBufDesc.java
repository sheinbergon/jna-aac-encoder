package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;
/**
 * Maps to AACENC_BufDesc struct in @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
 */
public class AACEncBufDesc extends Structure {

    private final static List<String> FIELD_ORDER = JNAUtil.structureFieldOrder(AACEncBufDesc.class);

    public int numBufs;
    public PointerByReference bufs;
    public IntByReference bufferIdentifiers;
    public IntByReference bufSizes;
    public IntByReference bufElSizes;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
