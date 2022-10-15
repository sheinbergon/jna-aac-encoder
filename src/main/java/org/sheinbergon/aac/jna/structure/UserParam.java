package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to USER_PARAM struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libAACenc/src/aacenc_lib.cpp">fdk-aac/libAACenc/src/aacenc_lib.cpp</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
public class UserParam extends Structure {

  private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(UserParam.class);

  public int userAOT;
  public int userSamplerate;
  public int nChannels;
  public int userChannelMode;
  public int userBitrate;
  public int userBitrateMode;
  public int userBandwidth;
  public int userAfterburner;
  public int userFramelength;
  public int userAncDataRate;
  public int userPeakBitrate;
  public byte userTns;
  public byte userPns;
  public byte userIntensity;
  public int userTpType;
  public byte userTpSignaling;
  public byte userTpNsubFrames;
  public byte userTpAmxv;
  public byte userTpProtection;
  public byte userTpHeaderPeriod;
  public byte userErTools;
  public int userPceAdditions;
  public byte userMetaDataMode;
  public byte userSbrEnabled;
  public int userSbrRatio;

  @Override
  protected final List<String> getFieldOrder() {
    return FIELD_ORDER;
  }
}
