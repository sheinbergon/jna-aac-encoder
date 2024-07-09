package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to FDK_channelMapDescr struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libMpegTPDec/include/tp_data.h">fdk-aac/libMpegTPDec/include/tp_data.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
public class DecoderCProgramConfig extends Structure {

    private static final int PC_COMMENTLENGTH = 256;
    private static final int PC_FSB_CHANNELS_MAX = 16;
    private static final int PC_LFE_CHANNELS_MAX = 4;
    private static final int PC_ASSOCDATA_MAX = 8;

    private static final int PC_CCEL_MAX = 16;
    private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(DecoderCProgramConfig.class);

    /* PCE bitstream elements: */
    public byte ElementInstanceTag;
    public byte Profile;
    public byte SamplingFrequencyIndex;
    public byte NumFrontChannelElements;
    public byte NumSideChannelElements;
    public byte NumBackChannelElements;
    public byte NumLfeChannelElements;
    public byte NumAssocDataElements;
    public byte NumValidCcElements;

    public byte MonoMixdownPresent;
    public byte MonoMixdownElementNumber;

    public byte StereoMixdownPresent;
    public byte StereoMixdownElementNumber;

    public byte MatrixMixdownIndexPresent;
    public byte MatrixMixdownIndex;
    public byte PseudoSurroundEnable;

    public byte[] FrontElementIsCpe = new byte[PC_FSB_CHANNELS_MAX];
    ;
    public byte[] FrontElementTagSelect = new byte[PC_FSB_CHANNELS_MAX];
    ;
    public byte[] FrontElementHeightInfo = new byte[PC_FSB_CHANNELS_MAX];
    ;
    public byte[] SideElementIsCpe = new byte[PC_FSB_CHANNELS_MAX];
    ;
    public byte[] SideElementTagSelect = new byte[PC_FSB_CHANNELS_MAX];
    ;
    public byte[] SideElementHeightInfo = new byte[PC_FSB_CHANNELS_MAX];
    ;
    public byte[] BackElementIsCpe = new byte[PC_FSB_CHANNELS_MAX];
    ;
    public byte[] BackElementTagSelect = new byte[PC_FSB_CHANNELS_MAX];
    ;
    public byte[] BackElementHeightInfo = new byte[PC_FSB_CHANNELS_MAX];
    ;
    public byte[] LfeElementTagSelect = new byte[PC_LFE_CHANNELS_MAX];
    public byte[] AssocDataElementTagSelect = new byte[PC_ASSOCDATA_MAX];
    public byte[] CcElementIsIndSw = new byte[PC_CCEL_MAX];
    public byte[] ValidCcElementTagSelect = new byte[PC_CCEL_MAX];
    public byte CommentFieldBytes;
    public byte[] Comment = new byte[PC_COMMENTLENGTH];
    public byte isValid;
    public byte NumChannels;
    public byte NumEffectiveChannels;
    public byte elCounter;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
