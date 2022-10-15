
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

  /**
   * Create a new handle.
   *
   * @return handle
   */
  static native Pointer MediaInfo_New();

  /**
   * Open a file and collect information about it (technical information and tags).
   *
   * @param handle
   * @param file   full name of the file to open
   * @return 1 if file was opened, 0 if not
   */
  static native SizeT MediaInfo_Open(Pointer handle, WString file);


  /**
   * Configure or get information about MediaInfo.
   *
   * @param handle
   * @param option The name of option
   * @param value  The value of option
   * @return Depends on the option: by default "" (nothing) means No, other means Yes
   */
  static native WString MediaInfo_Option(Pointer handle, WString option, WString value);


  /**
   * Get all details about a file.
   *
   * @param handle
   * @param reserved
   * @return All details about a file in one string
   */
  static native WString MediaInfo_Inform(Pointer handle, SizeT reserved);


  /**
   * Get a piece of information about a file (parameter is a string).
   *
   * @param handle
   * @param streamKind   Kind of stream (general, video, audio...)
   * @param streamNumber Stream number in Kind of stream (first, second...)
   * @param parameter    Parameter you are looking for in the stream (Codec, width, bitrate...),
   *                     in string format ("Codec", "Width"...)
   * @param infoKind     Kind of information you want about the parameter (the text, the measure,
   *                     the help...)
   * @param searchKind   Where to look for the parameter
   * @return a string about information you search, an empty string if there is a problem
   */
  static native WString MediaInfo_Get(Pointer handle, int streamKind, SizeT streamNumber, WString parameter, int infoKind, int searchKind);


  /**
   * Get a piece of information about a file (parameter is an integer).
   *
   * @param handle
   * @param streamKind     Kind of stream (general, video, audio...)
   * @param streamNumber   Stream number in Kind of stream (first, second...)
   * @param parameterIndex Parameter you are looking for in the stream (Codec, width, bitrate...),
   *                       in integer format (first parameter, second parameter...)
   * @param infoKind       Kind of information you want about the parameter (the text, the measure,
   *                       the help...)
   * @return a string about information you search, an empty string if there is a problem
   */
  static native WString MediaInfo_Get(Pointer handle, int streamKind, SizeT streamNumber, SizeT parameterIndex, int infoKind);


  /**
   * Count of streams of a stream kind (StreamNumber not filled), or count of piece of
   * information in this stream.
   *
   * @param handle
   * @param streamKind   Kind of stream (general, video, audio...)
   * @param streamNumber Stream number in this kind of stream (first, second...)
   * @return number of streams of the given stream kind
   */
  static native int MediaInfo_Count_Get(Pointer handle, int streamKind, SizeT streamNumber);


  /**
   * Close a file opened before with Open().
   *
   * @param handle
   */
  static native void MediaInfo_Close(Pointer handle);


  /**
   * Dispose of a handle created with New().
   *
   * @param handle
   */
  static native void MediaInfo_Delete(Pointer handle);

  private MediaInfoLib() {
  }
}
