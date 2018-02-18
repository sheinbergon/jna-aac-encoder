package org.sheinbergon.aac.jna.v015.structure;

import com.sun.jna.Structure;
import lombok.ToString;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;

@ToString
public class UserParam extends Structure {

    private final static List<String> FIELD_ORDER = JNAUtil.structureFieldOrder(UserParam.class);

    /**
     * Audio Object Type.
     */
    public int userAOT;
    /**
     * Sampling frequency.
     */
    public int userSamplerate;
    /**
     * will be set via channelMode.
     */
    public int nChannels;
    public int userChannelMode;
    public int userBitrate;
    public int userBitrateMode;
    public int userBandwidth;
    public int userAfterburner;
    public int userFramelength;
    public int userAncDataRate;
    public int userPeakBitrate;
    /**
     * Use TNS coding.
     */
    public byte userTns;
    /**
     * Use PNS coding.
     */
    public byte userPns;
    /**
     * Use Intensity coding.
     */
    public byte userIntensity;
    /**
     * Transport type
     */
    public int userTpType;
    /**
     * Extension AOT signaling mode.
     */
    public byte userTpSignaling;
    /**
     * Number of sub frames in a transport frame for LOAS/LATM or ADTS (default 1).
     */
    public byte userTpNsubFrames;
    /**
     * AudioMuxVersion to be used for LATM (default 0).
     */
    public byte userTpAmxv;
    public byte userTpProtection;
    /**
     * Parameter used to configure LATM/LOAS SMC rate. Moreover this parameters is
     * used to configure repetition rate of PCE in raw_data_block.
     */
    public byte userTpHeaderPeriod;

    /**
     * Use VCB11, HCR and/or RVLC ER tool.
     */
    public byte userErTools;
    /**
     * Configure additional bits in PCE.
     */
    public int userPceAdditions;
    /**
     * Meta data library configuration.
     */
    public byte userMetaDataMode;
    /**
     * Enable SBR for ELD.
     */
    public byte userSbrEnabled;
    /**
     * SBR sampling rate ratio. Dual- or single-rate.
     */
    public int userSbrRatio;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
