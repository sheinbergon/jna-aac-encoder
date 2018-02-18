package org.sheinbergon.aac.jna.v015.structure;

import com.sun.jna.Structure;
import lombok.ToString;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;

@ToString
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
