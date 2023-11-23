package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to AACENC_BufDesc struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier"})
public final class AACEncBufDesc extends Structure {

  private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(AACEncBufDesc.class);

  public int numBufs;
  public PointerByReference bufs;
  public IntByReference bufferIdentifiers;
  public IntByReference bufSizes;
  public IntByReference bufElSizes;

  @Override
  protected List<String> getFieldOrder() {
    return FIELD_ORDER;
  }
}
