package org.sheinbergon.aac.jna.v015.structure;

import com.sun.jna.Structure;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;


/**
 * Maps to AACENC_InfoStruct struct in @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.5/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
 */
public class AACEncInfo extends Structure {

    private final static List<String> FIELD_ORDER = JNAUtil.structureFieldOrder(AACEncInfo.class);

    public int maxOutBufBytes;
    public int maxAncBytes;
    public int inBufFillLevel;
    public int inputChannels;
    public int frameLength;
    public int encoderDelay;
    public byte[] confBuf = new byte[64];
    public int confSize;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
