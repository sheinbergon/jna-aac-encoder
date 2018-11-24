package org.sheinbergon.aac.jna;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import lombok.val;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;
import org.sheinbergon.aac.jna.structure.*;
import org.sheinbergon.aac.jna.util.AACEncError;
import org.sheinbergon.aac.jna.util.AACEncParam;
import org.sheinbergon.aac.jna.util.FdkAACLibException;
import org.sheinbergon.aac.jna.util.JNAUtil;

import java.util.Optional;

public class FdkAACLibFacade {

    private final static int IN_BUFFER_COUNT = 1;
    private final static int IN_BUFFER_IDENTIFIER = 0;
    private final static int IN_BUFFER_ELEMENT_SIZE = 2;

    // In samples division is required due to input bytes samples bit-shifting
    private final static int IN_SAMPLES_DIVISOR = 2;

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
        PointerByReference pointerRef = new PointerByReference(encoder.getPointer());
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncClose(pointerRef));
        verifyResult(result, FdkAACLib.Methods.CLOSE);
    }

    public static void initEncoder(AACEncoder encoder) {
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncEncode(encoder, null, null, null, null));
        verifyResult(result, FdkAACLib.Methods.ENCODE);
    }

    public static Optional<byte[]> encode(AACEncoder encoder, AACEncBufDesc inBufferDescriptor,
                                          AACEncBufDesc outBufferDescriptor, AACEncInArgs inArgs,
                                          AACEncOutArgs outArgs, int size) {
        JNAUtil.clearStructureMemory(inArgs, outArgs);
        inArgs.numInSamples = (size == WAVAudioSupport.EOS) ? size : size / IN_SAMPLES_DIVISOR;
        inArgs.writeField("numInSamples");
        val code = FdkAACLib.aacEncEncode(encoder, inBufferDescriptor, outBufferDescriptor, inArgs, outArgs);
        return Optional.ofNullable(AACEncError.valueOf(code))
                .filter(result -> result != AACEncError.AACENC_ENCODE_EOF)
                .map(result -> {
                    outArgs.readField("numOutBytes");
                    verifyResult(result, FdkAACLib.Methods.ENCODE);
                    return outBufferDescriptor.bufs.getValue().getByteArray(0, outArgs.numOutBytes);
                });
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

    private static void verifyResult(AACEncError result, FdkAACLib.Methods method) {
        Optional.of(result)
                .filter(error -> !error.equals(AACEncError.AACENC_OK))
                .ifPresent(error -> {
                    throw new FdkAACLibException(error, method.method);
                });
    }

    public static AACEncBufDesc outBufferDescriptor(Memory buffer) {
        AACEncBufDesc descriptor = new AACEncBufDesc();
        descriptor.numBufs = OUT_BUFFER_COUNT;
        descriptor.bufs = new PointerByReference(buffer);
        descriptor.bufSizes = new IntByReference((int) buffer.size());
        descriptor.bufferIdentifiers = new IntByReference(OUT_BUFFER_IDENTIFIER);
        descriptor.bufElSizes = new IntByReference(OUT_BUFFER_ELEMENT_SIZE);
        descriptor.write();
        return descriptor;
    }

    public static AACEncBufDesc inBufferDescriptor(Memory buffer) {
        AACEncBufDesc descriptor = new AACEncBufDesc();
        descriptor.numBufs = IN_BUFFER_COUNT;
        descriptor.bufs = new PointerByReference(buffer);
        descriptor.bufSizes = new IntByReference((int) buffer.size());
        descriptor.bufferIdentifiers = new IntByReference(IN_BUFFER_IDENTIFIER);
        descriptor.bufElSizes = new IntByReference(IN_BUFFER_ELEMENT_SIZE);
        descriptor.write();
        return descriptor;
    }
}
