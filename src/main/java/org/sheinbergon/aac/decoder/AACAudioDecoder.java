package org.sheinbergon.aac.decoder;

import com.sun.jna.Memory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.sheinbergon.aac.encoder.util.NotThreadSafe;
import org.sheinbergon.aac.jna.FdkAACLibFacade;
import org.sheinbergon.aac.jna.structure.AACDecoder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Decodes an ADTS framed AAC stream to interleaved, 16 bit signed little-endian PCM audio.
 */
@NotThreadSafe
@Accessors(fluent = true)
public final class AACAudioDecoder implements AutoCloseable {

  private static final int EOS = -1;
  private static final int IN_BUFFER_SIZE = 8192;
  // Up to 2048 samples per frame (HE-AAC), for up to 8 output channels
  private static final int OUT_BUFFER_SIZE = 2048 * 8 * Short.BYTES;

  private final InputStream input;
  private final AACDecoder decoder = FdkAACLibFacade.openDecoder();
  private final byte[] chunk = new byte[IN_BUFFER_SIZE];
  // Hard references are advised for memory buffers
  private final Memory inBuffer = new Memory(IN_BUFFER_SIZE);
  private final Memory outBuffer = new Memory(OUT_BUFFER_SIZE);

  private int offset = 0;
  private int pending = 0;
  private boolean eos = false;
  private boolean closed = false;

  /**
   * The decoded audio sample rate. Only available after the first frame was decoded.
   */
  @Getter
  private int sampleRate;
  /**
   * The decoded audio channel count. Only available after the first frame was decoded.
   */
  @Getter
  private int channels;

  /**
   * @param adts the ADTS framed AAC stream to decode, closed along with this decoder
   */
  public AACAudioDecoder(final @Nonnull InputStream adts) {
    this.input = adts;
  }

  /**
   * Decode the next AAC frame.
   *
   * @return the decoded frame's interleaved PCM (s16le) bytes, or {@code null} if the input stream is exhausted
   * @throws IOException if the underlying stream could not be read
   */
  @Nullable
  public byte[] decode() throws IOException {
    if (closed) {
      throw new IllegalStateException("Decoder instance already closed");
    }
    while (true) {
      readInput();
      feedDecoder();
      if (FdkAACLibFacade.decodeFrame(decoder, outBuffer)) {
        return decodedFrame();
      } else if (eos && pending == 0) {
        return null;
      }
    }
  }

  // Read the next chunk off the input stream, once the previous one was fully consumed
  private void readInput() throws IOException {
    if (pending == 0 && !eos) {
      val read = input.read(chunk);
      eos = read == EOS;
      if (!eos) {
        inBuffer.write(0, chunk, 0, read);
        offset = 0;
        pending = read;
      }
    }
  }

  // The library's internal bit-buffer might not have room for everything, leftovers are kept for the next round
  private void feedDecoder() {
    if (pending > 0) {
      val remaining = FdkAACLibFacade.fillDecoder(decoder, inBuffer.share(offset), pending);
      offset += pending - remaining;
      pending = remaining;
    }
  }

  private byte[] decodedFrame() {
    val info = FdkAACLibFacade.getDecoderStreamInfo(decoder);
    sampleRate = info.sampleRate();
    channels = info.channels();
    // All supported platforms are little-endian, so native 16 bit samples are already s16le
    return outBuffer.getByteArray(0, info.frameSize() * info.channels() * Short.BYTES);
  }

  @Override
  public void close() throws IOException {
    if (!closed) {
      closed = true;
      FdkAACLibFacade.closeDecoder(decoder);
      input.close();
    }
  }
}
