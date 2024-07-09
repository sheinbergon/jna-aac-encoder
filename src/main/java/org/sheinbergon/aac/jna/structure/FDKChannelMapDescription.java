package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to FDK_channelMapDescr struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libSYS/include/syslib_channelMapDescr.h">fdk-aac/libSYS/include/syslib_channelMapDescr.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
public class FDKChannelMapDescription extends Structure {

    private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(FDKChannelMapDescription.class);
    public Pointer pMapInfoTab;
    public int mapInfoTabLen;
    public int fPassThrough;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
