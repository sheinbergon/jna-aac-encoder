package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import lombok.ToString;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;

@ToString
/**
 * Maps to LIB_INFO struct in @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.5/libSYS/include/FDK_audio.h">fdk-aac/libSYS/include/FDK_audio.h</a>
 */
public class LibInfo extends Structure {

    private final static int LAST_MODULE = 32;
    private final static List<String> FIELD_ORDER = JNAUtil.structureFieldOrder(LibInfo.class);

    public static LibInfo[] allocate() {
        return (LibInfo[]) new LibInfo().toArray(LAST_MODULE);
    }

    public String title;
    public String build_date;
    public String build_time;
    public int module_id;
    public int version;
    public int flags;
    public byte[] versionStr = new byte[LAST_MODULE];

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
