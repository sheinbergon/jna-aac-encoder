package org.sheinbergon.aac.jna.v015;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import lombok.RequiredArgsConstructor;
import org.sheinbergon.aac.jna.v015.structure.*;
import org.sheinbergon.aac.jna.v015.util.AACEncError;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;
import org.sheinbergon.aac.jna.v015.util.FdkAACException;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FdkAAC {

    @RequiredArgsConstructor
    enum Methods {
        GET_LIB_INFO("aacEncGetLibInfo"),
        SET_PARAM("aacEncoder_SetParam"),
        OPEN("accEncOpen"),
        CLOSE("accEncClose"),
        ENCODE("aacEncEncode");

        final String method;
    }

    private final static String FDK_AAC = "fdk-aac";

    static {
        Native.register(FDK_AAC);
    }

    static native int aacEncOpen(PointerByReference handle, final int encModules, final int maxChannels);

    static native int aacEncClose(PointerByReference handle);

    static native int aacEncEncode(AACEncoder hAacEncoder, AACEncBufDesc inBufDesc, AACEncBufDesc outBufDesc, AACEncInArgs inargs, AACEncOutArgs outargs);

    static native int aacEncGetLibInfo(LibInfo libInfo);

    static native int aacEncoder_SetParam(AACEncoder encoder, int param, int value);

    static native int aacEncoder_GetParam(AACEncoder encoder, int param);
}