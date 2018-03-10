package org.sheinbergon.aac.jna;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.sheinbergon.aac.jna.structure.*;
import org.sheinbergon.aac.jna.util.AACEncError;
import org.sheinbergon.aac.jna.util.AACEncParam;
import org.sheinbergon.aac.jna.util.FdkAACLibException;

import java.util.Optional;
import java.util.stream.Stream;

public class FdkAACLibFacade {

    private final static int IN_BUFFER_COUNT = 1;
    private final static int IN_BUFFER_IDENTIFIER = 0;
    private final static int IN_BUFFER_ELEMENT_SIZE = 2;

    // This buffer just needs to be big enough to contain the encoded data
    private final static int OUT_BUFFER_SIZE = 20480;
    private final static int OUT_BUFFER_COUNT = 1;
    private final static int OUT_BUFFER_IDENTIFIER = 3;
    private final static int OUT_BUFFER_ELEMENT_SIZE = 1;


    public static AACEncoder openEncoder(int modules, int maxChannels) {
        PointerByReference pointerRef = new PointerByReference();
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncOpen(pointerRef, modules, maxChannels));
        verifyResult(result, FdkAACLib.Methods.OPEN);
        return AACEncoder.of(pointerRef);
    }

    public static void closeEncoder(AACEncoder encoder) {
        Pointer pointer = encoder.getPointer();
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncClose(new PointerByReference(pointer)));
        verifyResult(result, FdkAACLib.Methods.CLOSE);
    }

    public static void initEncoder(AACEncoder encoder) {
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncEncode(encoder, null, null, null, null));
        verifyResult(result, FdkAACLib.Methods.ENCODE);
    }

    public static byte[] encode(AACEncoder encoder, int length, byte[] data) {
        AACEncInArgs inArgs = new AACEncInArgs();
        AACEncOutArgs outArgs = new AACEncOutArgs();
        AACEncBufDesc inBufferDesc, outBufferDesc;
        inArgs.numInSamples = length;
        if (length == -1) {
            inBufferDesc = new AACEncBufDesc();
        } else {
            inBufferDesc = inBufferDescriptor(data, length);
        }
        outBufferDesc = outBufferDescriptor();
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncEncode(encoder, inBufferDesc, outBufferDesc, inArgs, outArgs));
        verifyResult(result, FdkAACLib.Methods.GET_LIB_INFO);
        return outBufferDesc.bufs
                .getValue()
                .getByteArray(0, outArgs.numOutBytes);
    }

    public static AACEncInfo getEncoderInfo(AACEncoder encoder) {
        AACEncInfo info = new AACEncInfo();
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncInfo(encoder, info));
        verifyResult(result, FdkAACLib.Methods.INFO);
        info.read();
        return info;
    }

    public static void setEncoderParam(AACEncoder encoder, AACEncParam param, int value) {
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncoder_SetParam(encoder, param.getValue(), value));
        verifyResult(result, FdkAACLib.Methods.SET_PARAM);
    }

    public static int getEncoderParam(AACEncoder encoder, AACEncParam param) {
        return FdkAACLib.aacEncoder_GetParam(encoder, param.getValue());
    }

    private static void verifyResult(AACEncError result, FdkAACLib.Methods method) {
        Optional.of(result)
                .filter(error -> !error.equals(AACEncError.AACENC_OK))
                .ifPresent(error -> {
                    throw new FdkAACLibException(error, method.method);
                });
    }

    private static AACEncBufDesc outBufferDescriptor() {
        AACEncBufDesc bufDesc = new AACEncBufDesc();
        bufDesc.numBufs = OUT_BUFFER_COUNT;
        bufDesc.bufs = new PointerByReference(new Memory(OUT_BUFFER_SIZE));
        bufDesc.bufSizes = new IntByReference(OUT_BUFFER_SIZE);
        bufDesc.bufferIdentifiers = new IntByReference(OUT_BUFFER_IDENTIFIER);
        bufDesc.bufElSizes = new IntByReference(OUT_BUFFER_ELEMENT_SIZE);
        return bufDesc;
    }

    private static AACEncBufDesc inBufferDescriptor(byte[] data, int length) {
        AACEncBufDesc bufDesc = new AACEncBufDesc();
        bufDesc.numBufs = IN_BUFFER_COUNT;
        Memory memory = new Memory(length);
        bufDesc.bufs = new PointerByReference(memory);
        memory.write(0, data, 0, length);
        bufDesc.bufSizes = new IntByReference(length);
        bufDesc.bufferIdentifiers = new IntByReference(IN_BUFFER_IDENTIFIER);
        bufDesc.bufElSizes = new IntByReference(IN_BUFFER_ELEMENT_SIZE);
        return bufDesc;
    }
}
