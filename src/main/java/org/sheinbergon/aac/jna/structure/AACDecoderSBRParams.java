package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import com.sun.jna.ptr.ShortByReference;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to SamplingRateInfo struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACdec/src/aacdecoder.h">fdk-aac/libAACdec/src/aacdecoder.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
public class AACDecoderSBRParams extends Structure {

    private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(AACDecoderSBRParams.class);
    public int nsDelay;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
