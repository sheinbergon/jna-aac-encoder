package org.sheinbergon.aac.jna.v015;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.sheinbergon.aac.jna.v015.structure.*;
import org.sheinbergon.aac.jna.v015.util.AACEncError;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;
import org.sheinbergon.aac.jna.v015.util.FdkAACException;

import java.util.Optional;
import java.util.stream.Stream;

public class FdkAACFacade {
    public static AACEncoder openEncoder(int modules, int maxChannels) {
        PointerByReference pointerRef = new PointerByReference();
        AACEncError result = AACEncError.valueOf(FdkAAC.aacEncOpen(pointerRef, modules, maxChannels));
        verifyResult(result, FdkAAC.Methods.OPEN);
        return AACEncoder.of(pointerRef);
    }

    public static void closeEncoder(AACEncoder encoder) {
        Pointer pointer = encoder.getPointer();
        AACEncError result = AACEncError.valueOf(FdkAAC.aacEncClose(new PointerByReference(pointer)));
        verifyResult(result, FdkAAC.Methods.CLOSE);
    }

    public static void initEncoder(AACEncoder encoder) {
        AACEncError result = AACEncError.valueOf(FdkAAC.aacEncEncode(encoder, null, null, null, null));
        verifyResult(result, FdkAAC.Methods.ENCODE);
    }

    public static byte[] encode(AACEncoder encoder, int length, byte[] data, boolean terminal) {
        AACEncInArgs inArgs = new AACEncInArgs();
        AACEncOutArgs outArgs = new AACEncOutArgs();
        AACEncBufDesc outBuf = new AACEncBufDesc(), inBuf = new AACEncBufDesc();
        if (terminal) {
            inArgs.numInSamples = -1;
        } else {
            inArgs.numInSamples = length;
            inBuf.numBufs = 1;
            Memory in = new Memory(data.length);
            in.write(0, data, 0, data.length);
            inBuf.bufs = new PointerByReference(in);
            inBuf.bufSizes = new IntByReference(length);
            inBuf.bufferIdentifiers = new IntByReference(0);
            inBuf.bufElSizes = new IntByReference(2);
        }

        outBuf.numBufs = 1;
        // Contained buffer just needs to be big enough to contain the encoded data
        outBuf.bufs = new PointerByReference(new Memory(20480));
        outBuf.bufSizes = new IntByReference(20480);
        outBuf.bufferIdentifiers = new IntByReference(3);
        outBuf.bufElSizes = new IntByReference(1);
        AACEncError result = AACEncError.valueOf(FdkAAC.aacEncEncode(encoder, inBuf, outBuf, inArgs, outArgs));
        verifyResult(result, FdkAAC.Methods.GET_LIB_INFO);
        return outBuf.bufs.getValue().getByteArray(0, outArgs.numOutBytes);
    }

    public static LibInfo[] getLibInfo() {
        LibInfo[] infos = LibInfo.allocate();
        AACEncError result = AACEncError.valueOf(FdkAAC.aacEncGetLibInfo(infos[0]));
        Stream.of(infos).forEach(Structure::read);
        verifyResult(result, FdkAAC.Methods.GET_LIB_INFO);
        return infos;
    }

    public static void setEncoderParam(AACEncoder encoder, AACEncParam param, int value) {
        AACEncError result = AACEncError.valueOf(FdkAAC.aacEncoder_SetParam(encoder, param.getValue(), value));
        verifyResult(result, FdkAAC.Methods.SET_PARAM);
    }

    public static int getEncoderParam(AACEncoder encoder, AACEncParam param) {
        return FdkAAC.aacEncoder_GetParam(encoder, param.getValue());
    }

    private static void verifyResult(AACEncError result, FdkAAC.Methods method) {
        Optional.of(result)
                .filter(error -> !error.equals(AACEncError.AACENC_OK))
                .ifPresent(error -> {
                    throw new FdkAACException(error, method.method);
                });
    }
}
