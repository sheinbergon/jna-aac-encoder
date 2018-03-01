package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
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
        AACEncoder encoder = new AACEncoder(pointerReference.getValue());
        encoder.read();
        return encoder;
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
    public ByteByReference outBuffer;
    public int outBufferInBytes;
    public ShortByReference inputBuffer;
    public int inputBufferOffset;
    public int nSamplesToRead;
    public int nSamplesRead;
    public int nZerosAppended;
    public int nDelay;
    /*
        Arrays - 1D, 2D or even 3D, are initialized as 1D to accommodate for size allocation.
        This is due to the fact JNA doesn't support mapping multi-dimensional array inside structs with simple
        preservation of size/memory allocation for primitives. As we won't be using these values, I'm
        perfectly fine with it
     */
    public AACEncExtPayload[] extPayload = new AACEncExtPayload[MAX_TOTAL_EXT_PAYLOADS];
    public byte[] extPayloadData = new byte[8 * MAX_PAYLOAD_SIZE];
    public int[] extPayloadSize = new int[8];
    public long InitFlags;
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
