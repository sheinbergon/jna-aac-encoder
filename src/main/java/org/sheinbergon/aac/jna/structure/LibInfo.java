package org.sheinbergon.aac.jna.structure;

import com.sun.jna.Structure;
import org.sheinbergon.aac.jna.util.JNASupport;

import java.util.List;

/**
 * Maps to LIB_INFO struct.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libSYS/include/FDK_audio.h">fdk-aac/libSYS/include/FDK_audio.h</a>
 */
@SuppressWarnings({"JavadocVariable", "VisibilityModifier", "MemberName"})
public final class LibInfo extends Structure {

  private static final int VERSION_STRING_SIZE = 32;

  private static final List<String> FIELD_ORDER = JNASupport.structureFieldOrder(LibInfo.class);

  /**
   * Library Info mapping.
   */
  public LibInfo() {
    setAlignType(Structure.ALIGN_NONE); // Make sure field size alignments are as expected
    read(); // Read once after initialize from provided pointer
  }

  public String title;
  public String build_date;
  public String build_time;
  public int module_id;
  public int version;
  public int flags;
  public byte[] versionStr = new byte[VERSION_STRING_SIZE];

  @Override
  protected List<String> getFieldOrder() {
    return FIELD_ORDER;
  }
}
