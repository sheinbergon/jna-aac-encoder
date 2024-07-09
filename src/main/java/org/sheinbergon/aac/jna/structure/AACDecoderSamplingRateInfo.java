package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to SamplingRateInfo struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACdec/include/channelinfo.h">fdk-aac/libAACdec/include/channelinfo.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
public class AACDecoderSamplingRateInfo extends Structure {

    private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(AACDecoderSamplingRateInfo.class);

    public ShortByReference ScaleFactorBands_Long;
    public ShortByReference ScaleFactorBands_Short;
    public byte NumberOfScaleFactorBands_Long;
    public byte NumberOfScaleFactorBands_Short;
    public int samplingRateIndex;
    public int samplingRate;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
