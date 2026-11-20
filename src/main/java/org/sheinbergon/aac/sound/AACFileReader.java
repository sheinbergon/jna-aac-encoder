package org.sheinbergon.aac.sound;

import jakarta.annotation.Nonnull;
import lombok.val;
import org.sheinbergon.aac.decoder.AACAudioDecoder;
import org.sheinbergon.aac.jna.util.FdkAACLibException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Detects ADTS framed AAC audio (.aac files). The returned streams are AAC encoded,
 * use {@link AudioSystem#getAudioInputStream(AudioFormat.Encoding, AudioInputStream)} to decode them to PCM.
 */
public final class AACFileReader extends AudioFileReader {

  // Enough to hold the first few ADTS frames, even for multi-channel audio
  private static final int PROBE_SIZE = 16384;

  // ADTS header - 12 sync bits, followed by the MPEG version bit, 2 (zeroed) layer bits and the protection bit
  private static final int ADTS_HEADER_SIZE = 7;
  private static final int ADTS_SYNC_BYTE = 0xFF;
  private static final int ADTS_SYNC_AND_LAYER_MASK = 0xF6;
  private static final int ADTS_SYNC_AND_LAYER = 0xF0;

  @Override
  public AudioFileFormat getAudioFileFormat(final @Nonnull InputStream stream)
      throws UnsupportedAudioFileException, IOException {
    byte[] probe;
    stream.mark(PROBE_SIZE);
    try {
      probe = stream.readNBytes(PROBE_SIZE);
    } finally {
      stream.reset();
    }
    if (probe.length < ADTS_HEADER_SIZE
        || (probe[0] & ADTS_SYNC_BYTE) != ADTS_SYNC_BYTE
        || (probe[1] & ADTS_SYNC_AND_LAYER_MASK) != ADTS_SYNC_AND_LAYER) {
      throw new UnsupportedAudioFileException("Not an ADTS AAC stream");
    }
    // The ADTS header doesn't signal SBR/PS (HE-AAC), so the actual output format is only known by decoding
    try (val decoder = new AACAudioDecoder(new ByteArrayInputStream(probe))) {
      if (decoder.decode() == null) {
        throw new UnsupportedAudioFileException("No decodable ADTS AAC frames found");
      }
      val format = new AudioFormat(
          AACFileTypes.AAC_ENCODING,
          decoder.sampleRate(),
          AudioSystem.NOT_SPECIFIED,
          decoder.channels(),
          AudioSystem.NOT_SPECIFIED,
          AudioSystem.NOT_SPECIFIED,
          false);
      return new AudioFileFormat(AACFileTypes.AAC, format, AudioSystem.NOT_SPECIFIED);
    } catch (FdkAACLibException x) {
      throw (UnsupportedAudioFileException) new UnsupportedAudioFileException("Invalid ADTS AAC stream").initCause(x);
    }
  }

  @Override
  public AudioFileFormat getAudioFileFormat(final @Nonnull URL url) throws UnsupportedAudioFileException, IOException {
    try (val stream = new BufferedInputStream(url.openStream())) {
      return getAudioFileFormat(stream);
    }
  }

  @Override
  public AudioFileFormat getAudioFileFormat(final @Nonnull File file) throws UnsupportedAudioFileException, IOException {
    return getAudioFileFormat(file.toURI().toURL());
  }

  @Override
  public AudioInputStream getAudioInputStream(final @Nonnull InputStream stream)
      throws UnsupportedAudioFileException, IOException {
    val format = getAudioFileFormat(stream).getFormat();
    return new AudioInputStream(stream, format, AudioSystem.NOT_SPECIFIED);
  }

  @Override
  public AudioInputStream getAudioInputStream(final @Nonnull URL url) throws UnsupportedAudioFileException, IOException {
    val stream = new BufferedInputStream(url.openStream());
    try {
      return getAudioInputStream(stream);
    } catch (UnsupportedAudioFileException | IOException | RuntimeException x) {
      stream.close();
      throw x;
    }
  }

  @Override
  public AudioInputStream getAudioInputStream(final @Nonnull File file) throws UnsupportedAudioFileException, IOException {
    return getAudioInputStream(file.toURI().toURL());
  }
}
