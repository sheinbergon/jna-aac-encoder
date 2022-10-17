package org.sheinbergon.aac.jna;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.sheinbergon.aac.jna.structure.AACEncBufDesc;
import org.sheinbergon.aac.jna.structure.AACEncInArgs;
import org.sheinbergon.aac.jna.structure.AACEncInfo;
import org.sheinbergon.aac.jna.structure.AACEncOutArgs;
import org.sheinbergon.aac.jna.structure.AACEncoderHandle;

@SuppressWarnings("MethodName")
public final class FdkAACLib {

  @Getter
  @Accessors(fluent = true)
  @RequiredArgsConstructor
  enum Functions {
    INFO("aacEncInfo"),
    SET_PARAM("aacEncoder_SetParam"),
    OPEN("accEncOpen"),
    CLOSE("accEncClose"),
    ENCODE("aacEncEncode");

    private final String libraryFunctionName;
  }

  private static final String FDK_AAC = "fdk-aac";

  static {
    Native.register(FDK_AAC);
  }

  static native int aacEncOpen(PointerByReference handle, int encModules, int maxChannels);

  static native int aacEncClose(PointerByReference handle);

  static native int aacEncEncode(
      AACEncoderHandle hAacEncoder,
      AACEncBufDesc inBufDesc,
      AACEncBufDesc outBufDesc,
      AACEncInArgs inargs,
      AACEncOutArgs outargs);

  static native int aacEncInfo(AACEncoderHandle hAacEncoder, AACEncInfo pInfo);

  static native int aacEncoder_SetParam(AACEncoderHandle encoder, int param, int value);

  private FdkAACLib() {
  }
}
