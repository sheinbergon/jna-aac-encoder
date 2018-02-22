package org.sheinbergon.aac.jna.v015;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.apache.commons.lang3.ArrayUtils;
import org.sheinbergon.aac.jna.v015.structure.*;
import org.sheinbergon.aac.jna.v015.util.AACEncError;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;
import org.sheinbergon.aac.jna.v015.util.FdkAACException;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FdkAACFacade {
    public static AACEncoder openEncoder(int modules, int maxChannels) {
        PointerByReference pointerRef = new PointerByReference();
        AACEncError result = AACEncError.valueOf(FdkAAC.aacEncOpen(pointerRef, modules, maxChannels));
        handleResult(result, FdkAAC.Methods.OPEN);
        return AACEncoder.of(pointerRef);
    }

    public static void closeEncoder(AACEncoder encoder) {
        Pointer pointer = encoder.getPointer();
        AACEncError result = AACEncError.valueOf(FdkAAC.aacEncClose(new PointerByReference(pointer)));
        handleResult(result, FdkAAC.Methods.CLOSE);
    }

    public static void initEncoder(AACEncoder encoder) {
        AACEncError result = AACEncError.valueOf(FdkAAC.aacEncEncode(encoder, null, null, null, null));
        handleResult(result, FdkAAC.Methods.ENCODE);
    }

    public static void encode(AACEncoder encoder, int length, byte[] data) {
        AACEncInArgs inArgs = new AACEncInArgs();
        inArgs.numInSamples = length / 2;
        AACEncOutArgs outArgs = new AACEncOutArgs();
        AACEncBufDesc inBuf = new AACEncBufDesc();
        inBuf.numBufs = 1;
        byte[] converted = ArrayUtils.toPrimitive(IntStream.range(0, length)
                .filter(index -> index % 2 == 0)
                .mapToObj(index -> (byte) (data[index] | (data[index + 1] << 8)))
                .toArray(Byte[]::new));
        Memory in = new Memory(converted.length);
        in.write(0, converted, 0, converted.length);
        inBuf.bufs = new PointerByReference(in);
        inBuf.bufferIdentifiers = new IntByReference(0);
        inBuf.bufSizes = new IntByReference(length);
        inBuf.bufElSizes = new IntByReference(2);
        AACEncBufDesc outBuf = new AACEncBufDesc();
        outBuf.numBufs = 1;
        outBuf.bufs = new PointerByReference(new Memory(20480));
        outBuf.bufferIdentifiers = new IntByReference(3);
        outBuf.bufSizes = new IntByReference(20480);
        outBuf.bufElSizes = new IntByReference(1);

        FdkAAC.aacEncEncode(encoder, inBuf, outBuf, inArgs, outArgs);
    }

    public static LibInfo[] getLibInfo() {
        LibInfo[] infos = LibInfo.allocate();
        AACEncError result = AACEncError.valueOf(FdkAAC.aacEncGetLibInfo(infos[0]));
        Stream.of(infos).forEach(Structure::read);
        handleResult(result, FdkAAC.Methods.GET_LIB_INFO);
        return infos;
    }

    public static void setEncoderParam(AACEncoder encoder, AACEncParam param, int value) {
        AACEncError result = AACEncError.valueOf(FdkAAC.aacEncoder_SetParam(encoder, param.getValue(), value));
        handleResult(result, FdkAAC.Methods.SET_PARAM);
    }

    public static int getEncoderParam(AACEncoder encoder, AACEncParam param) {
        return FdkAAC.aacEncoder_GetParam(encoder, param.getValue());
    }

    private static void handleResult(AACEncError result, FdkAAC.Methods method) {
        Optional.of(result)
                .filter(error -> !error.equals(AACEncError.AACENC_OK))
                .ifPresent(error -> {
                    throw new FdkAACException(error, method.method);
                });
    }
}
