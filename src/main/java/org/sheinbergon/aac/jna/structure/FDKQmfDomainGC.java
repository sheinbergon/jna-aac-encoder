package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to FDK_QMF_DOMAIN_GC struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libFDK/include/FDK_qmf_domain.h">fdk-aac/libFDK/include/FDK_qmf_domain.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
public class FDKQmfDomainGC extends Structure {

    private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(FDKQmfDomainGC.class);
    /*
     * Flag to signal that QMF domain is set
     * explicitly instead of SBR and MPS init
     * routines.
     */
    public byte qmfDomainExplicitConfig;
    // Number of QMF input channels. */
    public byte nInputChannels;
    // Corresponding requested not yet active configuration parameter.
    public byte nInputChannels_requested;
    // Number of QMF output channels.
    public byte nOutputChannels;
    public byte nOutputChannels_requested; /*!< Corresponding requested not yet active
                                      configuration parameter. */
    public byte
            parkChannel; /*!< signal to automatically allocate additional memory to
                  park a channel if only one processing channel is
                  available. */
    public byte parkChannel_requested;
    FIXP_DBL *
    pWorkBuffer[QMF_MAX_WB_SECTIONS]; /*!< Pointerarray to volatile memory. */
    UINT flags; /*!< Flags to be set on all QMF analysis/synthesis filter
                 instances. */
    UINT flags_requested; /*!< Corresponding requested not yet active
                           configuration parameter. */
    public byte nBandsAnalysis; /*!< Number of QMF analysis bands for all input
                           channels. */
    public byte nBandsAnalysis_requested; /*!< Corresponding requested not yet active
                                     configuration parameter. */
    USHORT nBandsSynthesis; /*!< Number of QMF synthesis bands for all output
                             channels. */
    USHORT
            nBandsSynthesis_requested; /*!< Corresponding requested not yet active
                                configuration parameter. */

    // Number of QMF time slots (stored in work buffer memory).
    public byte nQmfTimeSlots;

    // Corresponding requested not yet active configuration parameter.
    public byte nQmfTimeSlots_requested;
    public byte
            nQmfOvTimeSlots; /*!< Number of QMF overlap/delay time slots (stored in
                      persistent memory). */
    public byte nQmfOvTimeSlots_requested; /*!< Corresponding requested not yet active
                                      configuration parameter. */

    /* Number of QMF bands which are processed by the decoder.
     * Typically this is equal to nBandsSynthesis.
     * But, it may differ if the QMF based resampler is being used.
     */
    public byte nQmfProcBands;
    public byte nQmfProcBands_requested; /*!< Corresponding requested not yet active
                                    configuration parameter. */

    /*
     * Number of complete QMF channels which need to
     * coexist in memory at the same time. For most cases
     * this is 1 which means the work buffer can be shared
     * between audio channels.
     */
    public byte nQmfProcChannels;

    // Corresponding requested not yet active configuration parameter.
    public byte nQmfProcChannels_requested;

    @Override
    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}
