
package org.sheinbergon.mediainfo.jna;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

@SuppressWarnings("MethodName")
public final class MediaInfoLib {

  @SuppressWarnings("MissingJavadocMethod")
  public static class SizeT extends IntegerType {
    public SizeT() {
      this(0L);
    }

    public SizeT(final long value) {
      super(Native.SIZE_T_SIZE, value, true);
    }
  }

  private static final String MEDIA_INFO = "mediainfo";

  static {
    Native.register(MEDIA_INFO);
  }

  static native Pointer MediaInfo_New();

  static native SizeT MediaInfo_Open(Pointer handle, WString file);

  static native WString MediaInfo_Option(Pointer handle, WString option, WString value);

  static native WString MediaInfo_Inform(Pointer handle, SizeT reserved);

  static native WString MediaInfo_Get(Pointer handle, int streamKind, SizeT streamNumber, WString parameter, int infoKind, int searchKind);

  static native WString MediaInfo_Get(Pointer handle, int streamKind, SizeT streamNumber, SizeT parameterIndex, int infoKind);

  static native int MediaInfo_Count_Get(Pointer handle, int streamKind, SizeT streamNumber);

  static native void MediaInfo_Close(Pointer handle);

  static native void MediaInfo_Delete(Pointer handle);

  private MediaInfoLib() {
  }
}
