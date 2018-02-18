package org.sheinbergon.aac.jna.v015;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import lombok.RequiredArgsConstructor;
import org.sheinbergon.aac.jna.v015.structure.*;
import org.sheinbergon.aac.jna.v015.util.AACEncError;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;
import org.sheinbergon.aac.jna.v015.util.FdkAACException;

import java.util.Optional;
import java.util.stream.Stream;

public class FdkAAC {


    @RequiredArgsConstructor
    private enum Methods {
        GET_LIB_INFO("aacEncGetLibInfo"),
        SET_PARAM("aacEncoder_SetParam"),
        OPEN("accEncOpen"),
        CLOSE("accEncClose");

        private final String method;
    }

    private final static String FDK_AAC = "fdk-aac";

    static {
        Native.register(FDK_AAC);
    }

    private static native int aacEncOpen(PointerByReference handle, final int encModules, final int maxChannels);

    private static native int aacEncClose(PointerByReference handle);

    public static native int aacEncEncode(AACEncoder hAacEncoder, AACEncBufDesc inBufDesc, AACEncBufDesc outBufDesc, AACEncInArgs inargs, AACEncOutArgs outargs);

    private static native int aacEncGetLibInfo(LibInfo libInfo);

    private static native int aacEncoder_SetParam(AACEncoder encoder, int param, int value);

    private static native int aacEncoder_GetParam(AACEncoder encoder, int param);

    public static AACEncoder openEncoder(int modules, int maxChannels) {
        PointerByReference pointerRef = new PointerByReference();
        AACEncError result = AACEncError.valueOf(aacEncOpen(pointerRef, modules, maxChannels));
        handlerResult(result, Methods.OPEN.method);
        AACEncoder encoder = AACEncoder.of(pointerRef);
        encoder.read(); // TODO - Read additional fields here...
        return encoder;
    }

    public static void closeEncoder(AACEncoder encoder) {
        Pointer pointer = encoder.getPointer();
        AACEncError result = AACEncError.valueOf(aacEncClose(new PointerByReference(pointer)));
        handlerResult(result, Methods.CLOSE.method);
    }

    public static LibInfo[] getLibInfo() {
        LibInfo[] infos = LibInfo.allocate();
        AACEncError result = AACEncError.valueOf(aacEncGetLibInfo(infos[0]));
        Stream.of(infos).forEach(Structure::read);
        handlerResult(result, Methods.GET_LIB_INFO.method);
        return infos;
    }

    public static void setEncoderParam(AACEncoder encoder, AACEncParam param, int value) {
        AACEncError result = AACEncError.valueOf(aacEncoder_SetParam(encoder, param.getValue(), value));
        handlerResult(result, Methods.SET_PARAM.method);
    }

    public static int getEncoderParam(AACEncoder encoder, AACEncParam param) {
        return aacEncoder_GetParam(encoder, param.getValue());
    }

    private final static void handlerResult(AACEncError result, String method) {
        Optional.of(result)
                .filter(error -> !error.equals(AACEncError.AACENC_OK))
                .ifPresent(error -> {
                    throw new FdkAACException(error, method);
                });
    }
}