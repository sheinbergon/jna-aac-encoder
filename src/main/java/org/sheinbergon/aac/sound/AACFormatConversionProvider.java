package org.sheinbergon.aac.sound;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sheinbergon.aac.decoder.AACAudioDecoder;
import org.sheinbergon.aac.jna.util.FdkAACLibException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.spi.FormatConversionProvider;
import java.io.IOException;
import java.io.InputStream;

/**
 * Decodes AAC encoded (ADTS) audio streams to 16 bit signed little-endian PCM.
 */
public final class AACFormatConversionProvider extends FormatConversionProvider {

  private static final AudioFormat.Encoding[] NO_ENCODINGS = new AudioFormat.Encoding[0];
  private static final AudioFormat[] NO_FORMATS = new AudioFormat[0];

  private static AudioFormat pcm(final @Nonnull AudioFormat aac) {
    return new AudioFormat(aac.getSampleRate(), Short.SIZE, aac.getChannels(), true, false);
  }

  @Override
  public AudioFormat.Encoding[] getSourceEncodings() {
    return new AudioFormat.Encoding[] {AACFileTypes.AAC_ENCODING};
  }

  @Override
  public AudioFormat.Encoding[] getTargetEncodings() {
    return new AudioFormat.Encoding[] {AudioFormat.Encoding.PCM_SIGNED};
  }

  @Override
  public AudioFormat.Encoding[] getTargetEncodings(final @Nonnull AudioFormat sourceFormat) {
    return AACFileTypes.AAC_ENCODING.equals(sourceFormat.getEncoding()) ? getTargetEncodings() : NO_ENCODINGS;
  }

  @Override
  public AudioFormat[] getTargetFormats(
      final @Nonnull AudioFormat.Encoding targetEncoding,
      final @Nonnull AudioFormat sourceFormat) {
    return AudioFormat.Encoding.PCM_SIGNED.equals(targetEncoding)
        && AACFileTypes.AAC_ENCODING.equals(sourceFormat.getEncoding())
        ? new AudioFormat[] {pcm(sourceFormat)}
        : NO_FORMATS;
  }

  @Override
  public AudioInputStream getAudioInputStream(
      final @Nonnull AudioFormat.Encoding targetEncoding,
      final @Nonnull AudioInputStream sourceStream) {
    if (!isConversionSupported(targetEncoding, sourceStream.getFormat())) {
      throw new IllegalArgumentException("Unsupported conversion: " + sourceStream.getFormat() + " to " + targetEncoding);
    }
    return getAudioInputStream(pcm(sourceStream.getFormat()), sourceStream);
  }

  @Override
  public AudioInputStream getAudioInputStream(
      final @Nonnull AudioFormat targetFormat,
      final @Nonnull AudioInputStream sourceStream) {
    if (!isConversionSupported(targetFormat, sourceStream.getFormat())) {
      throw new IllegalArgumentException("Unsupported conversion: " + sourceStream.getFormat() + " to " + targetFormat);
    }
    val decoding = new DecodingInputStream(new AACAudioDecoder(sourceStream));
    return new AudioInputStream(decoding, pcm(sourceStream.getFormat()), AudioSystem.NOT_SPECIFIED);
  }

  @RequiredArgsConstructor
  static final class DecodingInputStream extends InputStream {

    private static final int BYTE_MASK = 0xFF;

    private final AACAudioDecoder decoder;
    private final byte[] single = new byte[1];

    // The currently drained decoded frame, null once the decoder was exhausted
    private byte[] frame = new byte[0];
    private int position = 0;

    @Override
    public int read() throws IOException {
      return read(single, 0, 1) < 0 ? -1 : single[0] & BYTE_MASK;
    }

    @Override
    public int read(final @Nonnull byte[] bytes, final int offset, final int length) throws IOException {
      if (length == 0) {
        return 0;
      }
      try {
        while (frame != null && position == frame.length) {
          frame = decoder.decode();
          position = 0;
        }
      } catch (FdkAACLibException x) {
        throw new IOException("Could not decode AAC audio", x);
      }
      if (frame == null) {
        return -1;
      }
      val count = Math.min(length, frame.length - position);
      System.arraycopy(frame, position, bytes, offset, count);
      position += count;
      return count;
    }

    @Override
    public void close() throws IOException {
      decoder.close();
    }
  }
}
