package org.sheinbergon.aac.sound;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.MediaInfoSupport;
import org.sheinbergon.aac.TestSupport;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

// TODO - Consider adding actual byte-level comparison encoding tests
@SuppressWarnings("MissingJavadocMethod")
@DisplayName("Java AudioSystem AAC encoding support")
public final class AACFileWriterTest {

  private static final AudioFileFormat.Type[] NO_FILE_TYPES = new AudioFileFormat.Type[0];
  private static final AudioFileFormat.Type[] AAC_FILE_TYPES = new AudioFileFormat.Type[] {
      AACFileTypes.AAC_LC,
      AACFileTypes.AAC_HE,
      AACFileTypes.AAC_HE_V2};
  private static final AACEncodingProfile[] AAC_AUDIO_TYPES = new AACEncodingProfile[] {
      AACEncodingProfile.AAC_LC,
      AACEncodingProfile.HE_AAC,
      AACEncodingProfile.HE_AAC_V2};
  private static final float SAMPLE_RATE = 44100f;
  private static final int SAMPLE_SIZE = 16;
  private static final int CHANNELS = 2;
  private static final int UNSUPPORTED_CHANNELS = 8;
  private static final int BYTES_PER_SAMPLE = SAMPLE_SIZE / Byte.SIZE;

  private final AACFileWriter writer = new AACFileWriter();

  private static AudioInputStream silentAudioInputStream(
      final AudioFormat.Encoding encoding,
      final int channels,
      final boolean bigEndian,
      final int frames) {
    val frameSize = channels * BYTES_PER_SAMPLE;
    val format = new AudioFormat(encoding, SAMPLE_RATE, SAMPLE_SIZE, channels, frameSize, SAMPLE_RATE, bigEndian);
    return new AudioInputStream(new ByteArrayInputStream(new byte[frames * frameSize]), format, frames);
  }

  private static AudioInputStream emptyAudioInputStream(
      final AudioFormat.Encoding encoding,
      final int channels,
      final boolean bigEndian) {
    return silentAudioInputStream(encoding, channels, bigEndian, 0);
  }

  @Test
  @DisplayName("Supported audio file types")
  public void supportedTypes() {
    Assertions.assertArrayEquals(AAC_FILE_TYPES, writer.getAudioFileTypes());
  }

  @Test
  @DisplayName("Supported audio input")
  public void supportedAudioInput() throws UnsupportedAudioFileException, IOException {
    val input = TestSupport.supportedWAVAudioInputStream();
    Assertions.assertArrayEquals(AAC_FILE_TYPES, writer.getAudioFileTypes(input));
  }

  @Test
  @DisplayName("Unsupported audio input encoding")
  public void unsupportedAudioInputEncoding() {
    val input = emptyAudioInputStream(AudioFormat.Encoding.PCM_UNSIGNED, CHANNELS, false);
    Assertions.assertArrayEquals(NO_FILE_TYPES, writer.getAudioFileTypes(input));
  }

  @Test
  @DisplayName("Unsupported audio input channel count")
  public void unsupportedAudioInputChannels() {
    val input = emptyAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, UNSUPPORTED_CHANNELS, false);
    Assertions.assertArrayEquals(NO_FILE_TYPES, writer.getAudioFileTypes(input));
  }

  @Test
  @DisplayName("Unsupported audio input byte order")
  public void unsupportedAudioInputEndianness() {
    val input = emptyAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, CHANNELS, true);
    Assertions.assertArrayEquals(NO_FILE_TYPES, writer.getAudioFileTypes(input));
  }

  @Test
  @DisplayName("Empty audio input encoding")
  public void emptyAudioInputEncoding() throws IOException {
    val output = new ByteArrayOutputStream();
    try (val input = emptyAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, CHANNELS, false)) {
      val written = writer.write(input, AACFileTypes.AAC_LC, output);
      Assertions.assertEquals(output.size(), written);
    }
  }

  // A single frame is too short for the encoder to emit anything before the stream is concluded
  @Test
  @DisplayName("Single frame audio input encoding")
  public void singleFrameAudioInputEncoding() throws IOException {
    val output = new ByteArrayOutputStream();
    try (val input = silentAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, CHANNELS, false, 1)) {
      val written = writer.write(input, AACFileTypes.AAC_LC, output);
      Assertions.assertEquals(output.size(), written);
    }
  }

  @Test
  @DisplayName("Unsupported audio input")
  public void unsupportedAudioEncoding() throws IOException {
    val aac = TestSupport.tempAACOutputFile();
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      try (val input = TestSupport.unsupported24bitWAVAudioInputStream()) {
        writer.write(input, AACFileTypes.AAC_LC, aac);
      } finally {
        if (aac.exists()) {
          Files.delete(aac.toPath());
        }
      }
    });
  }

  @Test
  @DisplayName("Supported audio types encoding (LC/HE-AAC/HE-AACv2)")
  public void supportedAudioOutputStreamEncoding() {
    Assertions.assertAll(Stream.of(AAC_FILE_TYPES).map(audioType -> () -> {
      val aac = TestSupport.tempAACOutputFile();
      try (val input = TestSupport.supportedWAVAudioInputStream();
           val output = new FileOutputStream(aac)) {
        writer.write(input, audioType, output);
        Assertions.assertTrue(aac.length() > 0);
        output.flush();
        MediaInfoSupport.assertAACOutput(aac, input.getFormat(), AACFileWriter.profileByType(audioType));
      } finally {
        if (aac.exists()) {
          Files.delete(aac.toPath());
        }
      }
    }));
  }

  @Test
  @DisplayName("Supported audio file encoding")
  public void supportedAudioFileEncoding() throws UnsupportedAudioFileException, IOException {
    val aac = TestSupport.tempAACOutputFile();
    try (val input = TestSupport.supportedWAVAudioInputStream()) {
      writer.write(input, AACFileTypes.AAC_LC, aac);
      Assertions.assertTrue(aac.length() > 0);
      MediaInfoSupport.assertAACOutput(aac, input.getFormat(), AACEncodingProfile.AAC_LC);
    } finally {
      if (aac.exists()) {
        Files.delete(aac.toPath());
      }
    }
  }
}
