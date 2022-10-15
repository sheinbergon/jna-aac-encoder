package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to CODER_CONFIG struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libSYS/include/FDK_audio.h">fdk-aac/libSYS/include/FDK_audio.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier", "MemberName"})
public class CoderConfig extends Structure {

  private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(CoderConfig.class);

  /**
   * Audio Object Type (AOT).
   */
  public int aot;
  /**
   * Extension Audio Object Type (SBR).
   */
  public int extAOT;
  /**
   * Channel mode.
   */
  public int channelMode;
  /**
   * Sampling rate.
   */
  public int samplingRate;
  /**
   * Extended samplerate (SBR).
   */
  public int extSamplingRate;
  /**
   * Average bitRate.
   */
  public int bitRate;
  /**
   * Number of PCM samples per codec frame and audio channel.
   */
  public int samplesPerFrame;
  /**
   * Number of audio channels.
   */
  public int noChannels;
  public int bitsFrame;
  /**
   * Amount of encoder subframes. 1 means no subframing.
   */
  public int nSubFrames;
  /**
   * The number of the sub-frames which are grouped and transmitted in a super-frame (BSAC).
   */
  public int BSACnumOfSubFrame;
  /**
   * The average length of the large-step layers in bytes (BSAC).
   */
  public int BSAClayerLength;
  /**
   * Encoding flags.
   */
  public int flags;
  /**
   * Matrix mixdown index to put into PCE. Default value 0 means no mixdown coefficient,
   * valid values are 1-4 which correspond to matrix_mixdown_idx 0-3.
   */
  public byte matrixMixdownA;
  /**
   * Frame period for sending in band configuration buffers in the transport layer.
   */
  public byte headerPeriod;
  /**
   * USAC MPS stereo mode.
   */
  public byte stereoConfigIndex;
  /**
   * USAC SBR mode.
   */
  public byte sbrMode;
  /**
   * 0: implicit signaling, 1: backwards compatible explicit signaling, 2: hierarcical explicit signaling.
   */
  public int sbrSignaling;
  public byte sbrPresent;
  public byte psPresent;

  @Override
  protected final List<String> getFieldOrder() {
    return FIELD_ORDER;
  }
}
