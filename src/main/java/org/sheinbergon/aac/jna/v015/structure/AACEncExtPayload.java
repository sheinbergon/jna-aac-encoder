package org.sheinbergon.aac.jna.v015.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import lombok.ToString;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;

@ToString
public class AACEncExtPayload extends Structure {

    private final static List<String> FIELD_ORDER = JNAUtil.structureFieldOrder(AACEncExtPayload.class);

    /**
     * pointer to extension payload data
     */
    public ByteByReference pData;
    /**
     * extension payload data size in bits
     */
    public int dataSize;
    /**
     * extension payload data type
     */
    public int dataType;
    /**
     * number of the channel element the data is assigned to
     */
    public int associatedChElement;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}