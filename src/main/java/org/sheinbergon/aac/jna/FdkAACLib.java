package org.sheinbergon.aac.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
import java.nio.ByteBuffer;
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
        ENCODER_ENCODE("aacEncEncode"),
        
        DECODER_OPEN("aacDecoder_Open"),
        DECODER_CLOSE("aacDecoder_Close"),
        DECODER_SETPARAM("aacDecoder_SetParam"),
        DECODER_STREAMINFO("aacDecoder_GetStreamInfo"),
        DECODER_FILL("aacDecoder_Fill"),
        DECODER_DECODEFRAME("aacDecoder_DecodeFrame"),
        ;

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

    static native void aacDecoder_Close(AACDecoderHandle self);
    
    static native int aacDecoder_SetParam(AACDecoderHandle self, int param, int value);
    
    static native Pointer aacDecoder_GetStreamInfo(AACDecoderHandle self);

    static native int aacDecoder_Fill(
            AACDecoderHandle self,
            PointerByReference buffer,
            IntByReference bufferSize,
            IntByReference bytesValid);

    static native int aacDecoder_DecodeFrame(
            AACDecoderHandle self,
            ByteBuffer pTimeData,
            int timeDataSize,
            int flags);

    private FdkAACLib() {
    }
}
