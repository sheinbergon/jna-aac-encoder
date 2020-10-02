package org.sheinbergon.aac.jna;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;
import org.sheinbergon.aac.jna.structure.AACEncBufDesc;
import org.sheinbergon.aac.jna.structure.AACEncInArgs;
import org.sheinbergon.aac.jna.structure.AACEncInfo;
import org.sheinbergon.aac.jna.structure.AACEncOutArgs;
import org.sheinbergon.aac.jna.structure.AACEncoder;
import org.sheinbergon.aac.jna.util.AACEncError;
import org.sheinbergon.aac.jna.util.AACEncParam;
import org.sheinbergon.aac.jna.util.FdkAACLibException;
import org.sheinbergon.aac.jna.util.JNASupport;

import javax.annotation.Nonnull;
import java.util.Optional;

public final class FdkAACLibFacade {

    private static final int IN_BUFFER_COUNT = 1;
    private static final int IN_BUFFER_IDENTIFIER = 0;
    private static final int IN_BUFFER_ELEMENT_SIZE = 2;

    // In samples division is required due to input bytes sample bitshifting
    private static final int IN_SAMPLES_DIVISOR = 2;

    private static final int OUT_BUFFER_COUNT = 1;
    private static final int OUT_BUFFER_IDENTIFIER = 3;
    private static final int OUT_BUFFER_ELEMENT_SIZE = 1;

    /**
     * @param modules
     * @param maxChannels
     * @return
     */
    public static AACEncoder openEncoder(
            final int modules,
            final int maxChannels) {
        PointerByReference pointerRef = new PointerByReference();
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncOpen(pointerRef, modules, maxChannels));
        verifyResult(result, FdkAACLib.Methods.OPEN);
        return AACEncoder.of(pointerRef);
    }

    /**
     * @param encoder
     */
    public static void closeEncoder(final @Nonnull AACEncoder encoder) {
        PointerByReference pointerRef = new PointerByReference(encoder.getPointer());
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncClose(pointerRef));
        verifyResult(result, FdkAACLib.Methods.CLOSE);
    }

    /**
     * @param encoder
     */
    public static void initEncoder(final @Nonnull AACEncoder encoder) {
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncEncode(encoder, null, null, null, null));
        verifyResult(result, FdkAACLib.Methods.ENCODE);
    }

    /**
     * @param encoder
     * @param inBufferDescriptor
     * @param outBufferDescriptor
     * @param inArgs
     * @param outArgs
     * @param size
     * @return
     */
    public static Optional<byte[]> encode(
            final @Nonnull AACEncoder encoder,
            final @Nonnull AACEncBufDesc inBufferDescriptor,
            final @Nonnull AACEncBufDesc outBufferDescriptor,
            final @Nonnull AACEncInArgs inArgs,
            final @Nonnull AACEncOutArgs outArgs,
            final int size) {
        JNASupport.clearStructureMemory(inArgs, outArgs);
        inArgs.numInSamples = (size == WAVAudioSupport.EOS) ? size : size / IN_SAMPLES_DIVISOR;
        inArgs.writeField("numInSamples");
        return Optional.ofNullable(AACEncError
                .valueOf(FdkAACLib.aacEncEncode(encoder, inBufferDescriptor, outBufferDescriptor, inArgs, outArgs)))
                .filter(result -> result != AACEncError.AACENC_ENCODE_EOF)
                .map(result -> {
                    outArgs.readField("numOutBytes");
                    verifyResult(result, FdkAACLib.Methods.ENCODE);
                    return outBufferDescriptor.bufs
                            .getValue().getByteArray(0, outArgs.numOutBytes);
                });
    }

    /**
     * @param encoder
     * @return
     */
    public static AACEncInfo getEncoderInfo(final @Nonnull AACEncoder encoder) {
        AACEncInfo info = new AACEncInfo();
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncInfo(encoder, info));
        verifyResult(result, FdkAACLib.Methods.INFO);
        info.read();
        return info;
    }

    /**
     * @param encoder
     * @param param
     * @param value
     */
    public static void setEncoderParam(
            final @Nonnull AACEncoder encoder,
            final @Nonnull AACEncParam param,
            final int value) {
        AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncoder_SetParam(encoder, param.getValue(), value));
        verifyResult(result, FdkAACLib.Methods.SET_PARAM);
    }

    /**
     * @param result
     * @param method
     */
    private static void verifyResult(
            final @Nonnull AACEncError result,
            final @Nonnull FdkAACLib.Methods method) {
        Optional.of(result)
                .filter(error -> !error.equals(AACEncError.AACENC_OK))
                .ifPresent(error -> {
                    throw new FdkAACLibException(error, method.method());
                });
    }

    /**
     * @param buffer
     * @return
     */
    public static AACEncBufDesc outBufferDescriptor(final @Nonnull Memory buffer) {
        AACEncBufDesc descriptor = new AACEncBufDesc();
        descriptor.numBufs = OUT_BUFFER_COUNT;
        descriptor.bufs = new PointerByReference(buffer);
        descriptor.bufSizes = new IntByReference((int) buffer.size());
        descriptor.bufferIdentifiers = new IntByReference(OUT_BUFFER_IDENTIFIER);
        descriptor.bufElSizes = new IntByReference(OUT_BUFFER_ELEMENT_SIZE);
        descriptor.write();
        return descriptor;
    }

    /**
     * @param buffer
     * @return
     */
    public static AACEncBufDesc inBufferDescriptor(final @Nonnull Memory buffer) {
        AACEncBufDesc descriptor = new AACEncBufDesc();
        descriptor.numBufs = IN_BUFFER_COUNT;
        descriptor.bufs = new PointerByReference(buffer);
        descriptor.bufSizes = new IntByReference((int) buffer.size());
        descriptor.bufferIdentifiers = new IntByReference(IN_BUFFER_IDENTIFIER);
        descriptor.bufElSizes = new IntByReference(IN_BUFFER_ELEMENT_SIZE);
        descriptor.write();
        return descriptor;
    }

    private FdkAACLibFacade() {
    }
}
