package org.sheinbergon.aac.jna;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import lombok.Cleanup;
import lombok.extern.java.Log;
import lombok.val;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;
import static org.sheinbergon.aac.jna.FdkAACLib.*;
import org.sheinbergon.aac.jna.structure.*;
import org.sheinbergon.aac.jna.util.*;

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
   * Open (create) an AAC decoder.
   *
   * @param transportType     the transport type to be used.
   * @param numberOfTransportLayers number of transport layers.
   * @return an {@link AACDecoderHandle} handle
   * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACdec/include/aacdecoder_lib.h">fdk-aac/aacdecoder_lib.h</a>
   * @throws FdkAACLibException if decoder cannot be created
   */
  public static @Nonnull AACDecoderHandle openDecoder(@Nonnull TransportType transportType, int numberOfTransportLayers) throws FdkAACLibException {
    var result = FdkAACLib.aacDecoder_Open(transportType.getValue(), numberOfTransportLayers);
    if (result == null || result.equals(Pointer.NULL))
      throw new FdkAACLibException(AACDecoderError.AAC_DEC_UNKNOWN, FdkAACLib.Functions.DECODER_OPEN.libraryFunctionName());
    var self = new AACDecoderHandle();
    self.setPointer(result);
    return self;
  }
  
  /**
   * Close a previously opened {@link AACEncoder}.
   *
   * @param decoder an {@link AACDecoderHandle} instance, previously returned by {@link #openDecoder}
   */
  public static void closeDecoder(final @Nonnull AACDecoderHandle decoder) {
    FdkAACLib.aacDecoder_Close(decoder);
  }
  
  /**
   * Set one single decoder parameter.
   *
   * @param self   AAC decoder handle.
   * @param param  Parameter to be set.
   * @param value  Parameter value.
   * @throws FdkAACLibException if parameter cannot be set
   */
  public static void setDecoderParam(@Nonnull AACDecoderHandle self, @Nonnull AACDecParam param, int value) throws FdkAACLibException {
    int result = FdkAACLib.aacDecoder_SetParam(self, param.getValue(), value);
    verifyResult(AACDecoderError.valueOf(result), FdkAACLib.Functions.DECODER_SETPARAM);
  }
  
  public static @Nonnull CStreamInfo decoderStreamInfo(@Nonnull AACDecoderHandle self) {
    var result = aacDecoder_GetStreamInfo(self);
    if (result == null || result.equals(Pointer.NULL))
      throw new FdkAACLibException(AACDecoderError.AAC_DEC_UNKNOWN, FdkAACLib.Functions.DECODER_STREAMINFO.libraryFunctionName());
    var info = new CStreamInfo();
    info.useMemory(result);
    info.read();
    return info;
  }
  
  /**
   * Fill AAC decoder's internal input buffer with bitstream data from the
   * external input buffer. The function only copies such data as long as the
   * decoder-internal input buffer is not full. So it grabs whatever it can from
   * pBuffer and returns information (bytesValid) so that at a subsequent call of
   * %aacDecoder_Fill(), the right position in pBuffer can be determined to grab
   * the next data.
   *
   * @param self        AAC decoder handle.
   * @param buffer      Input buffer.
   * @throws FdkAACLibException if decoder's internal buffer cannot be filled
   */
  public static void decoderFill(@Nonnull AACDecoderHandle self, @Nonnull ByteBuffer buffer) throws FdkAACLibException {
    var size = buffer.remaining();
    int result;
    if (buffer.isDirect()) {
      var memoryReference = new PointerByReference(Native.getDirectBufferPointer(buffer));
      var bytesValid = new IntByReference(size);
      result = aacDecoder_Fill(self, memoryReference, new IntByReference(size), bytesValid);
      buffer.position(buffer.position() + (size - bytesValid.getValue()));
    }
    else {
      byte[] data = new byte[size];  // JNA Memory can't copy from byte buffers for some reason
      buffer.get(data);
      @Cleanup var memory = new Memory(size);
      memory.write(0, data, 0, size);
      var memoryReference = new PointerByReference(memory.share(0));
      var bytesValid = new IntByReference(size);
      result = aacDecoder_Fill(self, memoryReference, new IntByReference(size), bytesValid);
      buffer.position(buffer.position() - bytesValid.getValue());
    }
    verifyResult(AACDecoderError.valueOf(result), FdkAACLib.Functions.DECODER_SETPARAM);
  }
  
  /**
   * Decode one audio frame
   *
   * @param self        AAC decoder handle
   * @param buffer      output buffer where the decoded PCM samples will be written
   * @param flags       flags for the decoder:
   * <ul>
   *                <li> AACDEC_CONCEAL: Do concealment.
   *                <li> AACDEC_FLUSH: Discard input data. Flush filter banks (output delayed audio).
   *                <li> AACDEC_INTR: Input data is discontinuous. Resynchronize any internals as necessary.
   *                <li> AACDEC_CLRHIS: Clear all signal delay lines and history buffers.
   * </ul>
   * @throws FdkAACLibException if frame cannot be decoded
   */
  public static void decoderDecodeFrame(@Nonnull AACDecoderHandle self, @Nonnull ByteBuffer buffer, @Nonnull AACDecodeFrameFlag... flags) throws FdkAACLibException {
    int flagsInt = 0;
    for (var flag : flags)
        flagsInt |= flag.getValue();
    int result = aacDecoder_DecodeFrame(self, buffer, buffer.remaining() / IN_SAMPLES_DIVISOR, flagsInt);
    if (result == AACDecoderError.AAC_DEC_NOT_ENOUGH_BITS.getValue())
        return;
    verifyResult(AACDecoderError.valueOf(result), FdkAACLib.Functions.DECODER_DECODEFRAME);
    // Setting the buffer position is tricky.
    // Stream info must be called after decode.
    // However, perhaps flags and params can affect it? Not sure. But I see downmixing is reflected correctly.
    var info = decoderStreamInfo(self);
    buffer.position(buffer.position() + info.frameSize * info.numChannels * IN_SAMPLES_DIVISOR);
  }

  /**
   * Utility function to verify a library call's result.
   *
   * @param result   the result descriptor
   * @param function the execution library function
   */
  private static void verifyResult(
      final @Nonnull AACError result,
      final @Nonnull FdkAACLib.Functions function) {
    Optional.of(result)
        .filter(error -> !error.equals(AACEncError.AACENC_OK) && !error.equals(AACDecoderError.AAC_DEC_OK))
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
