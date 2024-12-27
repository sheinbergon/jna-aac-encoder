package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;

/**
 * Maps to AACENC_InArgs struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
@Structure.FieldOrder({"numInSamples", "numAncBytes"})
public class AACEncInArgs extends Structure {

  public int numInSamples;
  public int numAncBytes;
}
