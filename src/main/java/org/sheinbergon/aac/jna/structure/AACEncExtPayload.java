package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;

/**
 * Maps to AACENC_EXT_PAYLOAD struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACenc/src/aacenc.h">fdk-aac/libAACenc/src/aacenc.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
@Structure.FieldOrder({"pData", "dataSize", "dataType", "associatedChElement"})
public class AACEncExtPayload extends Structure {

  public ByteByReference pData;
  public int dataSize;
  public int dataType;
  public int associatedChElement;
}
