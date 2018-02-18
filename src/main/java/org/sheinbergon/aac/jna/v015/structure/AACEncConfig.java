package org.sheinbergon.aac.jna.v015.structure;

import com.sun.jna.Structure;
import lombok.ToString;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.List;

@ToString
public class AACEncConfig extends Structure {

    private final static List<String> FIELD_ORDER = JNAUtil.structureFieldOrder(AACEncConfig.class);

    /**
     * encoder sample rate
     */
    public int sampleRate;
    /**
     * encoder bit rate in bits/sec
     */
    public int bitRate;
    /**
     * additional bits consumed by anc data or sbr have to be consiedered while configuration
     */
    public int ancDataBitRate;
    /**
     * number of frames in super frame (not ADTS/LATM subframes !)
     */
    public int nSubFrames;
    /**
     * Audio Object Type
     */
    public int audioObjectType;
    /**
     * encoder bit rate in bits/superframe
     */
    public int averageBits;
    /**
     * encoder bitrate mode (CBR/VBR)
     */
    public int bitrateMode;
    /**
     * number of channels to process
     */
    public int nChannels;
    /**
     * Input Channel ordering scheme.
     */
    public int channelOrder;
    /**
     * targeted audio bandwidth in Hz
     */
    public int bandWidth;
    /**
     * encoder channel mode configuration
     */
    public int channelMode;
    /**
     * used frame size
     */
    public int framelength;
    /**
     * bitstreams syntax configuration
     */
    public int syntaxFlags;
    /**
     * error protection configuration
     */
    public byte epConfig;
    /**
     * ancillary rate, 0 (disabled), -1 (default) else desired rate
     */
    public int anc_Rate;
    public int maxAncBytesPerAU;
    /**
     * minimum number of bits in AU
     */
    public int minBitsPerFrame;
    /**
     * maximum number of bits in AU
     */
    public int maxBitsPerFrame;
    /**
     * size of bitreservoir
     */
    public int bitreservoir;
    /**
     * audio mux version in loas/latm transport format
     */
    public int audioMuxVersion;
    /**
     * sbr sampling rate ratio: dual- or single-rate
     */
    public int sbrRatio;
    /**
     * flag: use temporal noise shaping
     */
    public byte useTns;
    /**
     * flag: use perceptual noise substitution
     */
    public byte usePns;
    /**
     * flag: use intensity coding
     */
    public byte useIS;
    /**
     * flag: use afterburner
     */
    public byte useRequant;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}