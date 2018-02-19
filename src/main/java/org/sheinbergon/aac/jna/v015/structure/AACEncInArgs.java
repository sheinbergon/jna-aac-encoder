package org.sheinbergon.aac.jna.v015.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;

public class AACEncInArgs extends Structure {

    private final static List<String> FIELD_ORDER = JNAUtil.structureFieldOrder(AACEncInArgs.class);

    public final static AACEncInArgs NULL = new AACEncInArgs(Pointer.NULL);

    private AACEncInArgs(Pointer pointer) {
        super(pointer);
    }

    public AACEncInArgs() {
    }

    /**
     * Number of valid input audio samples (multiple of input channels).
     */
    public int numInSamples;
    /**
     * Number of ancillary data bytes to be encoded.
     */
    public int numAncBytes;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
