package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;

public class AACEncBufDesc extends Structure {

    private final static List<String> FIELD_ORDER = JNAUtil.structureFieldOrder(AACEncBufDesc.class);

    /**
     * Number of buffers.
     */
    public int numBufs;
    /**
     * Pointer to vector containing buffer addresses.
     */
    public PointerByReference bufs;
    /**
     * Identifier of each buffer element. See ::AACENC_BufferIdentifier.
     */
    public IntByReference bufferIdentifiers;
    /**
     * Size of each buffer in 8-bit bytes.
     */
    public IntByReference bufSizes;
    /*
     *Size of each buffer element in bytes.
     */
    public IntByReference bufElSizes;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
