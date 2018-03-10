package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import lombok.ToString;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;

/**
 * Maps to AACENC_InArgs struct in @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.5/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
 */
public class AACEncInArgs extends Structure {

    private final static List<String> FIELD_ORDER = JNAUtil.structureFieldOrder(AACEncInArgs.class);

    public int numInSamples;
    public int numAncBytes;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
