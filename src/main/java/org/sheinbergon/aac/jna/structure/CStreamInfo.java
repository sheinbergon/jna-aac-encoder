package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.sheinbergon.aac.jna.util.AACDecParam;
import org.sheinbergon.aac.jna.util.AACDecoderError;

@FieldDefaults(level = AccessLevel.PUBLIC)
@Structure.FieldOrder({"sampleRate", "frameSize", "numChannels", "pChannelType", "pChannelIndices",
    "aacSampleRate", "profile", "aot", "channelConfig", "bitRate",
    "aacSamplesPerFrame", "aacNumChannels", "extAot", "extSamplingRate", "outputDelay",
    "flags", "epConfig", "numLostAccessUnits", "numTotalBytes", "numBadBytes",
    "numTotalAccessUnits", "numBadAccessUnits", "drcProgRefLev", "drcPresMode", "outputLoudness"})
public final class CStreamInfo extends Structure {
    
  // These five members are the only really relevant ones for the user.
    
  /** The sample rate in Hz of the decoded PCM audio signal. */
  int sampleRate;
  /** The frame size of the decoded PCM audio signal.
   * <p> Typically this is:
   * <li> 1024 or 960 for AAC-LC
   * <li> 2048 or 1920 for HE-AAC (v2)
   * <li> 512 or 480 for AAC-LD and AAC-ELD
   * <li> 768, 1024, 2048 or 4096 for USAC
   */
  int frameSize;
  /** The number of output audio channels before the rendering module, ie the original channel configuration. */
  int numChannels;
  /**
   * Audio channel type of each output audio channel.
   */
  Pointer pChannelType;
  /** Audio channel index for each output audio channel.
   * @see ISO/IEC 13818-7:2005(E), 8.5.3.2 Explicit channel mapping using a program_config_element()
   */
  Pointer pChannelIndices;
  
  
  // Decoder internal members.
  
  /** Sampling rate in Hz without SBR (from configuration info) divided by a (ELD) downscale factor if present. */
  int aacSampleRate;
  /** MPEG-2 profile (from file header) (-1: not applicable (eg MPEG-4)). */
  int profile;
  /** Audio Object Type (from ASC). Set to the appropriate value for MPEG-2 bitstreams (eg 2 for AAC-LC). */
  int aot;
  /** Channel configuration. (0: PCE defined, 1: mono, 2: stereo, ...) */
  int channelConfig;
  /** Instantaneous bit rate. */
  int bitRate;
  /** Samples per frame for the AAC core (from ASC) divided by a (ELD) downscale factor if present.
   * <p> Typically this is (with a downscale factor of 1):
   * <li> 1024 or 960 for AAC-LC
   * <li> 512 or 480 for AAC-LD   and AAC-ELD
   */
  int aacSamplesPerFrame;
  /** The number of audio channels after AAC core processing (before PS or MPS processing).
   * CAUTION: This are not the final number of output channels! */
  int aacNumChannels;
  /** Extension Audio Object Type (from ASC)   */
  int extAot;
  /** Extension sampling rate in Hz (from ASC) divided by a (ELD) downscale factor if present. */
  int extSamplingRate;
  /** The number of samples the output is additionally delayed by.the decoder. */
  int outputDelay;
  /** Copy of internal flags. Only to be written by the decoder, and only to be read externally. */
  int flags;
  /** epConfig level (from ASC). only level 0 supported, -1 means no ER (e. g. AOT=2, MPEG-2 AAC, etc.)  */
  byte epConfig;
  
  
  // Statistics 
  
  /** This integer will reflect the estimated amount of lost access units in case aacDecoder_DecodeFrame()
   *  returns {@link AACDecoderError#AAC_DEC_TRANSPORT_SYNC_ERROR}. It will be {@code < 0} if the estimation failed. */
  int numLostAccessUnits;
  /** This is the number of total bytes that have passed through the decoder. */
  long numTotalBytes;
  /** This is the number of total bytes that were considered with errors from numTotalBytes. */
  long numBadBytes;
  /** This is the number of total access units that have passed through the decoder. */
  long numTotalAccessUnits;
  /** This is the number of total access units that were considered with errors from numTotalBytes. */
  long numBadAccessUnits;

  
// Metadata
  
  /**
   * DRC program reference level.
   * Defines the reference level below full-scale.
   * It is quantized in steps of 0.25dB.
   * The valid values range from 0 (0 dBFS) to 127 (-31.75 dBFS).
   * It is used to reflect the average loudness of the audio in LKFS according to ITU-R BS 1770.
   * If no level has been found in the bitstream the value is -1. */
  byte drcProgRefLev;
  /** DRC presentation mode. 
   * According to ETSI TS 101 154, this field indicates whether light (MPEG-4 Dynamic Range
   * Control tool) or heavy compression (DVB heavy compression) dynamic range control shall take priority
   * on the outputs. For details, see ETSI TS 101 154, table C.33. Possible values are:
   * <li> -1: No corresponding metadata found in the bitstream
   * <li>  0: DRC presentation mode not indicated
   * <li>  1: DRC presentation mode 1
   * <li>  2: DRC presentation mode 2
   * <li>  3: Reserved
   */
  byte drcPresMode;
  /**
   * Audio output loudness in steps of -0.25 dB. Range: 0 (0 dBFS) to 231 (-57.75 dBFS).
   * <p> A value of -1 indicates that no loudness metadata is present.
   * <p> If loudness normalization is active, the value corresponds to the target loudness value set with {@link AACDecParam#AAC_DRC_REFERENCE_LEVEL}.
   * <p> If loudness normalization is not active, the output loudness value corresponds to
   *     the loudness metadata given in the bitstream.
   * <p> Loudness metadata can originate from MPEG-4 DRC or MPEG-D DRC. */
  int outputLoudness;
  
  public @Override void useMemory(Pointer pointer) {
      super.useMemory(pointer);
  }
}
