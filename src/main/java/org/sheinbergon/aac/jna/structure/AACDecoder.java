package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;
import org.sheinbergon.aac.jna.util.JNASupport;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Maps to AAC_DECODER_INSTANCE  struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACdec/src/aacdecoder.h">fdk-aac/libAACdec/src/aacdecoder.h</a>
 */
@SuppressWarnings({
        "JavadocVariable",
        "VisibilityModifier",
        "MissingJavadocMethod",
        "MagicNumber",
        "MemberName",
        "PointlessArithmeticExpression",
        "unused"})
public final class AACDecoder extends Structure {

    private static final int AACDEC_MAX_NUM_PREROLL_AU = 3;
    private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(AACDecoder.class);

    public static AACDecoder of(final @Nonnull PointerByReference pointerReference) {
        return new AACDecoder(pointerReference.getValue());
    }

    private AACDecoder(final @Nonnull Pointer pointer) {
        super(pointer);
        setAlignType(Structure.ALIGN_NONE); // Make sure field size alignments are as expected
        read(); // Read once after initialize from provided pointer
    }

    public int aacChannels;
    public int[] ascChannels = new int[1];
    public int blockNumber;
    public int nrOfLayers;
    public int outputInterleaved;
    public int aacOutDataHeadroom;
    public Pointer hInput;
    public AACDecoderSamplingRateInfo[] samplingRateInfo = new AACDecoderSamplingRateInfo[1];
    public byte frameOK;
    public int[] flags = new int[1];
    public int[] elFlags = new int[(3 * ((8) * 2) + (((8) * 2)) / 2 + 4 * (1) + 1)];

    public int[] elements = new int[(3 * ((8) * 2) + (((8) * 2)) / 2 + 4 * (1) + 1)];

    public byte[] elTags = new byte[(3 * ((8) * 2) + (((8) * 2)) / 2 + 4 * (1) + 1)];
    public byte[] chMapping = new byte[((8) * 2)];
    public int[] channelType = new int[(8)];
    public byte[] channelIndices = new byte[(8)];
    public FDKChannelMapDescription mapDescr;
    public byte chMapIndex;
    public int sbrDataLen;
    public DecoderCProgramConfig pce;
    CStreamInfo
            streamInfo; /*!< Pointer to StreamInfo data (read from the bitstream) */
    CAacDecoderChannelInfo
      *pAacDecoderChannelInfo[(8)]; /*!< Temporal channel memory */
    CAacDecoderStaticChannelInfo
      *pAacDecoderStaticChannelInfo[(8)]; /*!< Persistent channel memory */

    public Pointer workBufferCore1;
    public Pointer workBufferCore2;
    public Pointer pTimeData2;
    public int timeData2Size;

    CpePersistentData *cpeStaticData[(
            3*((8)*2)+(((8)*2))/2+4*(1)+
            1)]; /*!< Pointer to persistent data shared by both channels of a CPE.
This structure is allocated once for each CPE. */

    CConcealParams concealCommonData;
    CConcealmentMethod concealMethodUser;

    CUsacCoreExtensions usacCoreExt; /*!< Data and handles to extend USAC FD/LPD
                                      core decoder (SBR, MPS, ...) */
    public int[] numUsacElements = new int[(1 * 1)];
    public byte[] usacStereoConfigIndex = new byte[(3 * ((8) * 2) + (((8) * 2)) / 2 + 4 * (1) + 1)];
    public Pointer[] pUsacConfig = new Pointer[(1 * 1)];
    public int nbDiv;
    public byte useLdQmfTimeAlign;
    public int aacChannelsPrev;
    public int[] channelTypePrev = new int[(8)];
    public byte[] channelIndicesPrev = new byte[(8)];
    public byte downscaleFactor;
    public byte downscaleFactorInBS;
    public Pointer hSbrDecoder;
    public byte sbrEnabled;
    public byte sbrEnabledPrev;
    public byte psPossible;
    public AACDecoderSBRParams sbrParams;
    public ByteByReference pDrmBsBuffer;
    public short drmBsBufferSize;
    FDK_QMF_DOMAIN
            qmfDomain; /*!< Instance of module for QMF domain data handling */
    public int qmfModeCurr;
    public int qmfModeUser;
    public Pointer hDrcInfo;
    public int metadataExpiry;
    public Pointer pMpegSurroundDecoder;
    public byte mpsEnableUser;
    public byte mpsEnableCurr;
    public byte mpsApplicable;
    public byte mpsOutputMode;
    public int mpsOutChannelsLast;
    public int mpsFrameSizeLast;
    public AACDecoderCAncData ancData;
    public Pointer hPcmUtils;
    public Pointer hLimiter;
    public byte limiterEnableUser;
    public byte limiterEnableCurr;
    // TODO - This might need to use smarter size of types, as signed long is 32BIT in windows platforms
    public long[] extGain = new long[1];
    public int extGainDelay;
    public Pointer hUniDrcDecoder;
    public byte multibandDrcPresent;
    public byte numTimeSlots;
    public int[] loudnessInfoSetPosition = new int[3];
    public byte defaultTargetLoudness;
    public Pointer[] pTimeDataFlush = new Pointer[((8) * 2)];
    public byte flushStatus;
    public byte flushCnt;
    public byte buildUpStatus;
    public byte buildUpCnt;
    public byte hasAudioPreRoll;
    public int[] prerollAULength = new int[AACDEC_MAX_NUM_PREROLL_AU + 1];
    public int accessUnit;
    public byte applyCrossfade;
    public AACDecoderFDKSignalDelay usacResidualDelay;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
