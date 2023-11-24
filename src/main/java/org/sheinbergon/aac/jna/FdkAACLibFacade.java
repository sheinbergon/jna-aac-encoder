package org.sheinbergon.aac.jna;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import lombok.extern.java.Log;
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


@Log
public final class FdkAACLibFacade {

  private static final int IN_BUFFER_COUNT = 1;
  private static final int IN_BUFFER_IDENTIFIER = 0;
  private static final int IN_BUFFER_ELEMENT_SIZE = 2;

  // In samples division is required due to input bytes sample bit-shifting
  private static final int IN_SAMPLES_DIVISOR = 2;

  private static final int OUT_BUFFER_COUNT = 1;
  private static final int OUT_BUFFER_IDENTIFIER = 3;
  private static final int OUT_BUFFER_ELEMENT_SIZE = 1;

  /**
   * Open (create) an {@link AACEncoder}.
   *
   * @param modules     fdk-aac encoder modules bitmask
   * @param maxChannels the maximum number of expected audio channels
   * @return an {@link AACEncoder} instance, populated by fdk-aac library.
   * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libAACenc/src/aacenc_lib.cpp">fdk-aac/libAACenc/src/aacenc_lib.cpp</a>
   */
  public static AACEncoder openEncoder(
      final int modules,
      final int maxChannels) {
    PointerByReference pointerRef = new PointerByReference();
    AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncOpen(pointerRef, modules, maxChannels));
    verifyResult(result, FdkAACLib.Functions.OPEN);
    return AACEncoder.of(pointerRef);
  }

  /**
   * Close a previously opened {@link AACEncoder}.
   *
   * @param encoder an {@link AACEncoder} instance, previously opened by the fdk-aac library
   */
  public static void closeEncoder(final @Nonnull AACEncoder encoder) {
    PointerByReference pointerRef = new PointerByReference(encoder.getPointer());
    AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncClose(pointerRef));
    verifyResult(result, FdkAACLib.Functions.CLOSE);
  }

  /**
   * Initialize a previously opened {@link AACEncoder}.
   *
   * @param encoder an {@link AACEncoder} instance, previously opened by the fdk-aac library
   */
  public static void initEncoder(final @Nonnull AACEncoder encoder) {
    AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncEncode(encoder, null, null, null, null));
    verifyResult(result, FdkAACLib.Functions.ENCODE);
  }

  /**
   * Encode raw (WAV) bytes to AAC.
   * <p>
   * Most of the passed arguments are reused/pre-allocated outside of this function
   * to mitigate memory-pressure effects.
   *
   * @param encoder             an {@link AACEncoder} instance, previously opened and initialized by the fdk-aac library
   * @param inBufferDescriptor  pre instantiated in-buffer descriptor used to hold input raw bytes
   * @param outBufferDescriptor pre instantiated out-buffer descriptor used to hold output encoded bytes
   * @param inArgs              input encoder data structure
   * @param outArgs             output encoder data structure
   * @param size                input data size indicator
   * @return the encoded AAC bytes, if any are present
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
          verifyResult(result, FdkAACLib.Functions.ENCODE);
          return outBufferDescriptor.bufs
              .getValue().getByteArray(0, outArgs.numOutBytes);
        });
  }

  /**
   * Get library information on a previously opened {@link AACEncoder}.
   *
   * @param encoder an {@link AACEncoder} instance, previously opened by the fdk-aac library
   * @return the give encoder's information payload
   */
  public static AACEncInfo getEncoderInfo(final @Nonnull AACEncoder encoder) {
    AACEncInfo info = new AACEncInfo();
    AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncInfo(encoder, info));
    verifyResult(result, FdkAACLib.Functions.INFO);
    info.read();
    return info;
  }

  /**
   * Set an {@link AACEncParam} parameter value on previously opened {@link AACEncoder}.
   *
   * @param encoder an {@link AACEncoder} instance, previously opened by the fdk-aac library
   * @param param   the parameter descriptor.
   * @param value   the parameter value
   * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v0.1.6/libAACenc/include/aacenc_lib.h">fdk-aac/libAACenc/include/aacenc_lib.h</a>
   */
  public static void setEncoderParam(
      final @Nonnull AACEncoder encoder,
      final @Nonnull AACEncParam param,
      final int value) {
    AACEncError result = AACEncError.valueOf(FdkAACLib.aacEncoder_SetParam(encoder, param.getValue(), value));
    verifyResult(result, FdkAACLib.Functions.SET_PARAM);
  }

  /**
   * Utility function to verify a library call's result.
   *
   * @param result   the result descriptor
   * @param function the execution library function
   */
  private static void verifyResult(
      final @Nonnull AACEncError result,
      final @Nonnull FdkAACLib.Functions function) {
    Optional.of(result)
        .filter(error -> !error.equals(AACEncError.AACENC_OK))
        .ifPresent(error -> {
          throw new FdkAACLibException(error, function.libraryFunctionName());
        });
  }

  /**
   * A utility function construct an out-buffer descriptor according to the fdk-aac library specifications.
   *
   * @param buffer a pre-allocated native memory region to be used by this descriptor
   * @return an out-buffer descriptor structure
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
   * A utility function construct an in-buffer descriptor according to the fdk-aac library specifications.
   *
   * @param buffer a pre-allocated native memory region to be used by this descriptor
   * @return an in-buffer descriptor structure
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
