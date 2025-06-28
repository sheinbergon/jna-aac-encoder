package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

import javax.annotation.Nonnull;

/**
 * Maps to AACENCODER struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACenc/src/aacenc_lib.cpp">fdk-aac/libAACenc/src/aacenc_lib.cpp</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier", "MissingJavadocMethod", "MagicNumber", "MemberName"})
@Structure.FieldOrder({"extParam", "coderConfig", "aacConfig", "hAacEnc", "hEnvEnc", "pSbrPayload", "hMetadataEnc", "metaDataAllowed", "hMpsEnc",
  "hTpEnc", "inputBuffer", "outBuffer", "inputBufferSize", "inputBufferSizePerChannel", "outBufferInBytes", "inputBufferOffset", "nSamplesToRead",
  "nSamplesRead", "nZerosAppended", "nDelay", "nDelayCore", "extPayload", "InitFlags", "nMaxAacElements", "nMaxAacChannels", "nMaxSbrElements",
  "nMaxSbrChannels", "encoder_modis", "CAPF_tpEnc"})
public final class AACEncoder extends Structure {

  private static final int MAX_TOTAL_EXT_PAYLOADS = 12;

  // TODO - Protect against null values
  public static AACEncoder of(final PointerByReference pointerReference) {
    return new AACEncoder(pointerReference.getValue());
  }

  private AACEncoder(final @Nonnull Pointer pointer) {
    super(pointer);
    setAlignType(Structure.ALIGN_NONE); // Make sure field size alignments are as expected
    read(); // Read once after initialize from provided pointer
  }

  public UserParam extParam;
  public CoderConfig coderConfig;
  public AACEncConfig aacConfig;
  public Pointer hAacEnc;
  public Pointer hEnvEnc;
  public Pointer pSbrPayload;
  public Pointer hMetadataEnc;
  public int metaDataAllowed;
  public Pointer hMpsEnc;
  public Pointer hTpEnc;
  public ShortByReference inputBuffer;
  public ByteByReference outBuffer;
  public int inputBufferSize;
  public int inputBufferSizePerChannel;
  public int outBufferInBytes;
  public int inputBufferOffset;
  public int nSamplesToRead;
  public int nSamplesRead;
  public int nZerosAppended;
  public int nDelay;
  public int nDelayCore;
  /*
      Arrays - 1D, 2D or even 3D, are initialized as 1D to accommodate for size allocation.
      This is due to the fact JNA doesn't support mapping multidimensional array inside structs with simple
      preservation of size/memory allocation for primitives. As we won't be using these values, I'm
      perfectly fine with it
   */
  public AACEncExtPayload[] extPayload = new AACEncExtPayload[MAX_TOTAL_EXT_PAYLOADS];
  public long InitFlags;
  public int nMaxAacElements;
  public int nMaxAacChannels;
  public int nMaxSbrElements;
  public int nMaxSbrChannels;
  public int encoder_modis;
  public int CAPF_tpEnc;
}
