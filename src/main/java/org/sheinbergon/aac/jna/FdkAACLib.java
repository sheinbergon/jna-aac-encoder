package org.sheinbergon.aac.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.sheinbergon.aac.jna.structure.*;

@SuppressWarnings("MethodName")
public final class FdkAACLib {

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    enum Functions {
        ENCODER_INFO("aacEncInfo"),
        ENCODER_SET_PARAM("aacEncoder_SetParam"),
        ENCODER_OPEN("accEncOpen"),
        ENCODER_CLOSE("accEncClose"),
        ENCODER_ENCODE("aacEncEncode");

        private final String libraryFunctionName;
    }

    private static final String FDK_AAC = "fdk-aac";

    static {
        Native.register(FDK_AAC);
    }

    static native int aacEncOpen(PointerByReference handle, int encModules, int maxChannels);

    static native int aacEncClose(PointerByReference handle);

    static native int aacEncEncode(
            AACEncoder hAacEncoder,
            AACEncBufDesc inBufDesc,
            AACEncBufDesc outBufDesc,
            AACEncInArgs inargs,
            AACEncOutArgs outargs);

    static native int aacEncInfo(AACEncoder hAacEncoder, AACEncInfo pInfo);

    static native int aacEncoder_SetParam(AACEncoder encoder, int param, int value);

    static native Pointer aacDecoder_Open(int transportFmt, int nrOfLayers);

    static native void aacDecoder_Close(AACDecoder self);

    static native int aacDecoder_Fill(
            AACDecoder self,
            Pointer[] Buffer,
            int[] bufferSize,
            IntByReference bytesValid);

    static native int aacDecoder_DecodeFrame(
            AACDecoder self,
            ShortByReference pTimeData,
            int timeDataSize,
            int flags);

    private FdkAACLib() {
    }
}
