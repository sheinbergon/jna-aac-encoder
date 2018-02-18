package org.sheinbergon.aac.jna.v015.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import lombok.ToString;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;

@ToString
public class AACEncoder extends Structure {

    private final static int MAX_TOTAL_EXT_PAYLOADS = 12;
    private final static int MAX_PAYLOAD_SIZE = 256;

    private final static List<String> FIELD_ORDER = JNAUtil.structureFieldOrder(AACEncoder.class);

    // TODO - Protect against null values
    public static AACEncoder of(PointerByReference pointerReference) {
        return new AACEncoder(pointerReference.getValue());
    }

    private AACEncoder(Pointer pointer) {
        super(pointer);
    }

    public UserParam extParam;
    public CoderConfig coderConfig;
    public AACEncConfig aacConfig;
    public Pointer hAacEnc;
    public Pointer hEnvEnc;
    public Pointer hMetadataEnc;
    public int metaDataAllowed;
    public Pointer hTpEnc;
    public Pointer outBuffer;
    public int outBufferInBytes;
    public Pointer inputBuffer;
    public int inputBufferOffset;
    public int nSamplesToRead;
    public int nSamplesRead;
    public int nZerosAppended;
    public int nDelay;
    public AACEncExtPayload[] extPayload = new AACEncExtPayload[MAX_TOTAL_EXT_PAYLOADS];
    public Pointer extPayloadData;
    public Pointer extPayloadSize;
    public int InitFlags;
    public int nMaxAacElements;
    public int nMaxAacChannels;
    public int nMaxSbrElements;
    public int nMaxSbrChannels;
    public int nMaxSubFrames;
    public int encoder_modis;
    public int CAPF_tpEnc;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
