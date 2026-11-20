package org.sheinbergon.aac.jna;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import lombok.extern.java.Log;
import lombok.val;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;
import org.sheinbergon.aac.jna.structure.AACDecoder;
import org.sheinbergon.aac.jna.structure.AACEncBufDesc;
import org.sheinbergon.aac.jna.structure.AACEncInArgs;
import org.sheinbergon.aac.jna.structure.AACEncInfo;
import org.sheinbergon.aac.jna.structure.AACEncOutArgs;
import org.sheinbergon.aac.jna.structure.AACEncoder;
import org.sheinbergon.aac.jna.util.AACDecStreamInfo;
import org.sheinbergon.aac.jna.util.AACEncError;
import org.sheinbergon.aac.jna.util.AACEncParam;
import org.sheinbergon.aac.jna.util.FdkAACLibException;
import org.sheinbergon.aac.jna.util.JNASupport;

import jakarta.annotation.Nonnull;
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

  // TRANSPORT_TYPE.TT_MP4_ADTS
  private static final int DECODER_ADTS_TRANSPORT = 2;
  private static final int DECODER_LAYERS = 1;
  // AAC_DECODER_ERROR.AAC_DEC_OK / AAC_DEC_NOT_ENOUGH_BITS
  private static final int DECODER_OK = 0x0000;
  private static final int DECODER_NOT_ENOUGH_BITS = 0x1002;

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
    val pointerRef = new PointerByReference();
    val result = AACEncError.valueOf(FdkAACLib.aacEncOpen(pointerRef, modules, maxChannels));
    verifyResult(result, FdkAACLib.Functions.ENCODER_OPEN);
    return AACEncoder.of(pointerRef);
  }

  /**
   * Close a previously opened {@link AACEncoder}.
   *
   * @param encoder an {@link AACEncoder} instance, previously opened by the fdk-aac library
   */
  public static void closeEncoder(final @Nonnull AACEncoder encoder) {
    val pointerRef = new PointerByReference(encoder.getPointer());
    val result = AACEncError.valueOf(FdkAACLib.aacEncClose(pointerRef));
    verifyResult(result, FdkAACLib.Functions.ENCODER_CLOSE);
  }

  /**
   * Initialize a previously opened {@link AACEncoder}.
   *
   * @param encoder an {@link AACEncoder} instance, previously opened by the fdk-aac library
   */
  public static void initEncoder(final @Nonnull AACEncoder encoder) {
    val result = AACEncError.valueOf(FdkAACLib.aacEncEncode(encoder, null, null, null, null));
    verifyResult(result, FdkAACLib.Functions.ENCODER_ENCODE);
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
          verifyResult(result, FdkAACLib.Functions.ENCODER_ENCODE);
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
    val info = new AACEncInfo();
    val result = AACEncError.valueOf(FdkAACLib.aacEncInfo(encoder, info));
    verifyResult(result, FdkAACLib.Functions.ENCODER_INFO);
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
    val result = AACEncError.valueOf(FdkAACLib.aacEncoder_SetParam(encoder, param.getValue(), value));
    verifyResult(result, FdkAACLib.Functions.ENCODER_SET_PARAM);
  }

  /**
   * Open (create) an {@link AACDecoder} for ADTS framed AAC streams.
   *
   * @return an {@link AACDecoder} handle, allocated by the fdk-aac library.
   * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.3/libAACdec/include/aacdecoder_lib.h">fdk-aac/libAACdec/include/aacdecoder_lib.h</a>
   */
  public static AACDecoder openDecoder() {
    return Optional.ofNullable(FdkAACLib.aacDecoder_Open(DECODER_ADTS_TRANSPORT, DECODER_LAYERS))
        .orElseThrow(() -> new IllegalStateException("Could not open an fdk-aac decoder instance"));
  }

  /**
   * Close a previously opened {@link AACDecoder}.
   *
   * @param decoder an {@link AACDecoder} handle, previously opened by the fdk-aac library
   */
  public static void closeDecoder(final @Nonnull AACDecoder decoder) {
    FdkAACLib.aacDecoder_Close(decoder);
  }

  /**
   * Feed encoded (ADTS) bytes to the decoder's internal bit-buffer.
   *
   * @param decoder an {@link AACDecoder} handle, previously opened by the fdk-aac library
   * @param buffer  native memory holding the encoded bytes
   * @param size    the number of encoded bytes available in the buffer
   * @return the number of trailing bytes that were not consumed, to be fed again once a frame was decoded
   */
  public static int fillDecoder(
      final @Nonnull AACDecoder decoder,
      final @Nonnull Pointer buffer,
      final int size) {
    val valid = new IntByReference(size);
    val result = FdkAACLib.aacDecoder_Fill(decoder, new PointerByReference(buffer), new IntByReference(size), valid);
    verifyDecoderResult(result, FdkAACLib.Functions.DECODER_FILL);
    return valid.getValue();
  }

  /**
   * Decode a single AAC frame out of the bytes previously fed to the decoder.
   *
   * @param decoder an {@link AACDecoder} handle, previously opened by the fdk-aac library
   * @param output  pre-allocated native memory to hold the decoded, interleaved 16 bit PCM samples
   * @return {@code true} if a frame was decoded, {@code false} if the decoder needs more input bytes
   */
  public static boolean decodeFrame(
      final @Nonnull AACDecoder decoder,
      final @Nonnull Memory output) {
    val samples = (int) (output.size() / Short.BYTES);
    val result = FdkAACLib.aacDecoder_DecodeFrame(decoder, output, samples, 0);
    if (result == DECODER_NOT_ENOUGH_BITS) {
      return false;
    } else {
      verifyDecoderResult(result, FdkAACLib.Functions.DECODER_DECODE_FRAME);
      return true;
    }
  }

  /**
   * Get the decoded output properties. Only meaningful after a frame was successfully decoded.
   *
   * @param decoder an {@link AACDecoder} handle, previously opened by the fdk-aac library
   * @return the decoded stream output information
   */
  public static AACDecStreamInfo getDecoderStreamInfo(final @Nonnull AACDecoder decoder) {
    val info = FdkAACLib.aacDecoder_GetStreamInfo(decoder);
    return new AACDecStreamInfo(info.getInt(0), info.getInt(Integer.BYTES), info.getInt(2L * Integer.BYTES));
  }

  private static void verifyDecoderResult(final int result, final @Nonnull FdkAACLib.Functions function) {
    if (result != DECODER_OK) {
      throw new FdkAACLibException(result, function.libraryFunctionName());
    }
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
    val descriptor = new AACEncBufDesc();
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
    val descriptor = new AACEncBufDesc();
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
