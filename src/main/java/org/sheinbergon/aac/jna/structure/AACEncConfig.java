package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;

/**
 * Maps to AACENC_CONFIG struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACenc/src/aacenc.h">fdk-aac/libAACenc/src/aacenc.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier", "MemberName"})
@Structure.FieldOrder({"sampleRate", "bitRate", "ancDataBitRate", "nSubFrames", "audioObjectType",
    "averageBits", "bitrateMode", "nChannels", "channelOrder", "bandWidth",
    "channelMode", "framelength", "syntaxFlags", "epConfig", "anc_Rate",
    "maxAncBytesPerAU", "minBitsPerFrame", "maxBitsPerFrame", "audioMuxVersion", "sbrRatio",
    "useTns", "usePns", "useIS", "useMS", "useRequant",
    "downscaleFactor"})
public class AACEncConfig extends Structure {

  public int sampleRate;
  public int bitRate;
  public int ancDataBitRate;
  public int nSubFrames;
  public int audioObjectType;
  public int averageBits;
  public int bitrateMode;
  public int nChannels;
  public int channelOrder;
  public int bandWidth;
  public int channelMode;
  public int framelength;
  public int syntaxFlags;
  public byte epConfig;
  public int anc_Rate;
  public int maxAncBytesPerAU;
  public int minBitsPerFrame;
  public int maxBitsPerFrame;
  public int audioMuxVersion;
  public int sbrRatio;
  public byte useTns;
  public byte usePns;
  public byte useIS;
  public byte useMS;
  public byte useRequant;
  public int downscaleFactor;
}
