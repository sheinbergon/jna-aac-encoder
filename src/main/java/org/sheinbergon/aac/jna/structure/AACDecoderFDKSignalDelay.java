package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to FDK_SignalDelay struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACdec/src/FDK_delay.h">fdk-aac/libAACdec/src/FDK_delay.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
public class AACDecoderFDKSignalDelay extends Structure {

    private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(AACDecoderFDKSignalDelay.class);
    public Pointer delay_line;
    public short delay;
    public byte num_channels;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
