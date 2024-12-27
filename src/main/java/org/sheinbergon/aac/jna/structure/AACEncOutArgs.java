package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;

/**
 * Maps to AACENC_OutArgs struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
@Structure.FieldOrder({"numOutBytes", "numInSamples", "numAncBytes", "bitResState"})
public class AACEncOutArgs extends Structure {

  public int numOutBytes;
  public int numInSamples;
  public int numAncBytes;
  public int bitResState;
}
