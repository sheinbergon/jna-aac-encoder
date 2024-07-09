package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to CAncData struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACdec/src/aacdecoder.h">fdk-aac/libAACdec/src/aacdecoder.h</a>
 */
@SuppressWarnings("MagicNumber")
public class AACDecoderCAncData extends Structure {

    private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(AACDecoderCAncData.class);

    public Pointer buffer;
    public int bufferSize;
    public int[] offset = new int[8];
    public int nrElements;

    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}

