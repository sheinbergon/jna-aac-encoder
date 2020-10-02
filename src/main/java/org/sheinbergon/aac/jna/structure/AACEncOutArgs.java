package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to AACENC_OutArgs struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
 */
public class AACEncOutArgs extends Structure {

    private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(AACEncOutArgs.class);

    public int numOutBytes;
    public int numInSamples;
    public int numAncBytes;

    @Override
    protected final List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
