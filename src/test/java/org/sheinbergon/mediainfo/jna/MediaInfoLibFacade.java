
package org.sheinbergon.mediainfo.jna;


import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import lombok.extern.java.Log;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Log
@SuppressWarnings("MissingJavadocMethod")
public final class MediaInfoLibFacade implements Closeable {


  private static final MediaInfoLib.SizeT RESERVED = new MediaInfoLib.SizeT(0);

  private static final String ZEN = "zen";

  static {
    if (Platform.isLinux()) {
      try {
        NativeLibrary.getInstance(ZEN);
      } catch (LinkageError e) {
        log.throwing(NativeLibrary.class.getName(), "getInstance", e);
      }
    }
  }

  private final Pointer handle = MediaInfoLib.MediaInfo_New();

  public synchronized boolean open(final File file) {
    val path = new WString(file.getAbsolutePath());
    return file.isFile() && MediaInfoLib
        .MediaInfo_Open(handle, path)
        .intValue() > 0;
  }

  public synchronized String inform() {
    return MediaInfoLib.MediaInfo_Inform(handle, RESERVED).toString();
  }


  public String option(final String option) {
    return option(option, StringUtils.EMPTY);
  }


  public synchronized String option(final String option, final String value) {
    val k = new WString(option);
    val v = new WString(value);
    return MediaInfoLib.MediaInfo_Option(handle, k, v).toString();
  }

  public String get(
      final StreamKind streamKind,
      final int streamNumber,
      final String parameter) {
    return get(streamKind, streamNumber, parameter, InfoKind.Text, InfoKind.Name);
  }

  public String get(
      final StreamKind streamKind,
      final int streamNumber,
      final String parameter,
      final InfoKind infoKind) {
    return get(streamKind, streamNumber, parameter, infoKind, InfoKind.Name);
  }

  public synchronized String get(
      final StreamKind streamKind,
      final int streamNumber,
      final String parameter,
      final InfoKind infoKind,
      final InfoKind searchKind) {
    return MediaInfoLib.MediaInfo_Get(
        handle,
        streamKind.ordinal(),
        new MediaInfoLib.SizeT(streamNumber),
        new WString(parameter),
        infoKind.ordinal(),
        searchKind.ordinal()
    ).toString();
  }


  public String get(
      final StreamKind streamKind,
      final int streamNumber,
      final int parameterIndex) {
    return get(streamKind, streamNumber, parameterIndex, InfoKind.Text);
  }


  public synchronized String get(
      final StreamKind streamKind,
      final int streamNumber,
      final int parameterIndex,
      final InfoKind infoKind) {
    return MediaInfoLib.MediaInfo_Get(
        handle,
        streamKind.ordinal(),
        new MediaInfoLib.SizeT(streamNumber),
        new MediaInfoLib.SizeT(parameterIndex),
        infoKind.ordinal()
    ).toString();
  }

  public synchronized int streamCount(
      final StreamKind streamKind) {
    return MediaInfoLib.MediaInfo_Count_Get(
        handle,
        streamKind.ordinal(),
        new MediaInfoLib.SizeT(-1));
  }

  public synchronized int parameterCount(
      final StreamKind streamKind,
      final int streamNumber) {
    return MediaInfoLib.MediaInfo_Count_Get(
        handle,
        streamKind.ordinal(),
        new MediaInfoLib.SizeT(streamNumber));
  }

  public Map<StreamKind, List<Map<String, String>>> snapshot() {
    val snapshot = new EnumMap<StreamKind, List<Map<String, String>>>(StreamKind.class);
    for (StreamKind streamKind : StreamKind.values()) {
      int streamCount = streamCount(streamKind);
      if (streamCount > 0) {
        val streams = new ArrayList<Map<String, String>>(streamCount);
        for (int i = 0; i < streamCount; i++) {
          streams.add(snapshot(streamKind, i));
        }
        snapshot.put(streamKind, streams);
      }
    }
    return snapshot;
  }

  public Map<String, String> snapshot(final StreamKind streamKind, final int streamNumber) {
    val streamInfo = new LinkedHashMap<String, String>();
    for (int i = 0, count = parameterCount(streamKind, streamNumber); i < count; i++) {
      String value = get(streamKind, streamNumber, i, InfoKind.Text);
      if (value.length() > 0) {
        streamInfo.put(get(streamKind, streamNumber, i, InfoKind.Name), value);
      }
    }
    return streamInfo;
  }

  public synchronized void close() {
    MediaInfoLib.MediaInfo_Close(handle);
  }

  public synchronized void dispose() {
    if (handle != null) {
      MediaInfoLib.MediaInfo_Delete(handle);
    }
  }

  public enum StreamKind {
    General,
    Video,
    Audio,
    Text,
    Chapters,
    Image,
    Menu
  }

  public enum InfoKind {
    Name, Text
  }
}
