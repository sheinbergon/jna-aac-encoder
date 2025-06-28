package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;


/**
 * Maps to AACENC_InfoStruct struct.
 * <p>
 * in @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
@Structure.FieldOrder({"maxOutBufBytes", "maxAncBytes", "inBufFillLevel", "inputChannels", "frameLength",
  "nDelay", "nDelayCore", "confBuf", "confSize"})
public class AACEncInfo extends Structure {

  private static final int CONF_BUF_SIZE = 64;

  public int maxOutBufBytes;
  public int maxAncBytes;
  public int inBufFillLevel;
  public int inputChannels;
  public int frameLength;
  public int nDelay;
  public int nDelayCore;
  public byte[] confBuf = new byte[CONF_BUF_SIZE];
  public int confSize;
}
