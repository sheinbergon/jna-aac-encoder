package org.sheinbergon.aac.sound;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.MediaInfoSupport;
import org.sheinbergon.aac.TestSupport;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;
import org.sheinbergon.aac.encoder.util.WAVInputException;
import org.sheinbergon.aac.encoder.util.WAVSupport;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

// TODO - Consider adding actual byte-level comparison encoding tests
@SuppressWarnings("MissingJavadocMethod")
@DisplayName("Java AudioSystem AAC encoding support")
public final class AACFileWriterTest {

  private static final int VALID_CHANNEL_COUNT = 2;
  private static final int VALID_SAMPLE_RATE = 44100;
  private static final int VALID_SAMPLE_SIZE = WAVSupport.SUPPORTED_SAMPLE_SIZE;

  private static final int INVALID_CHANNEL_COUNT = 100;
  private static final int INVALID_SAMPLE_SIZE = 24;

  private static final AudioFileFormat.Type[] NO_FILE_TYPES = new AudioFileFormat.Type[0];

  private static final AudioFileFormat.Type[] AAC_FILE_TYPES = new AudioFileFormat.Type[] {
      AACFileTypes.AAC_LC,
      AACFileTypes.AAC_HE,
      AACFileTypes.AAC_HE_V2};

  private static final AACEncodingProfile[] AAC_AUDIO_TYPES = new AACEncodingProfile[] {
      AACEncodingProfile.AAC_LC,
      AACEncodingProfile.HE_AAC,
      AACEncodingProfile.HE_AAC_V2};

  private final AACFileWriter writer = new AACFileWriter();

  @Test
  @DisplayName("Supported audio file types")
  public void supportedTypes() {
    Assertions.assertArrayEquals(AAC_FILE_TYPES, writer.getAudioFileTypes());
  }

  @Test
  @DisplayName("Unsupported audio input")
  public void unsupportedAudioInput() throws UnsupportedAudioFileException, IOException {
    AudioInputStream input = TestSupport.unsupported24bitWAVAudioInputStream();
    Assertions.assertArrayEquals(NO_FILE_TYPES, writer.getAudioFileTypes(input));
  }

  @Test
  @DisplayName("Supported audio input")
  public void supportedAudioInput() throws UnsupportedAudioFileException, IOException {
    AudioInputStream input = TestSupport.supportedWAVAudioInputStream();
    Assertions.assertArrayEquals(AAC_FILE_TYPES, writer.getAudioFileTypes(input));
  }

  @Test
  @DisplayName("Unsupported audio encoding")
  public void unsupportedAudioEncoding() throws UnsupportedAudioFileException, IOException {
    File aac = TestSupport.tempAACOutputFile();
    AudioInputStream input = TestSupport.unsupported24bitWAVAudioInputStream();
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      try (input) {
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
      File aac = TestSupport.tempAACOutputFile();
      FileOutputStream output = new FileOutputStream(aac);
      AudioInputStream input = TestSupport.supportedWAVAudioInputStream();
      try (input; output) {
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
    File aac = TestSupport.tempAACOutputFile();
    AudioInputStream input = TestSupport.supportedWAVAudioInputStream();
    try (input) {
      writer.write(input, AACFileTypes.AAC_LC, aac);
      Assertions.assertTrue(aac.length() > 0);
      MediaInfoSupport.assertAACOutput(aac, input.getFormat(), AACEncodingProfile.AAC_LC);
    } finally {
      if (aac.exists()) {
        Files.delete(aac.toPath());
      }
    }
  }

  @Test
  @DisplayName("Invalid sample size")
  public void invalidSampleSize() {
    Assertions.assertThrows(
        WAVInputException.class,
        () -> writer.extractAudioFormat(
            new AudioInputStream(
                AudioInputStream.nullInputStream(),
                new AudioFormat(
                    VALID_SAMPLE_RATE,
                    INVALID_SAMPLE_SIZE,
                    VALID_CHANNEL_COUNT,
                    true,
                    true
                ),
                0L
            )));
  }

  @Test
  @DisplayName("Invalid channel count")
  public void invalidChannelCount() {
    Assertions.assertThrows(
        WAVInputException.class,
        () -> writer.extractAudioFormat(
            new AudioInputStream(
                AudioInputStream.nullInputStream(),
                new AudioFormat(
                    VALID_SAMPLE_RATE,
                    VALID_SAMPLE_SIZE,
                    INVALID_CHANNEL_COUNT,
                    true,
                    true
                ),
                0L
            )));
  }


  @Test
  @DisplayName("Invalid WAV format")
  public void invalidWAVFormat() {
    Assertions.assertThrows(
        WAVInputException.class,
        () -> writer.extractAudioFormat(
            new AudioInputStream(
                AudioInputStream.nullInputStream(),
                new AudioFormat(
                    VALID_SAMPLE_RATE,
                    VALID_SAMPLE_SIZE,
                    VALID_CHANNEL_COUNT,
                    false,
                    false
                ),
                0L
            )));
  }

  @Test
  @DisplayName("Invalid byte order")
  public void invalidEndianness() {
    Assertions.assertThrows(
        WAVInputException.class,
        () -> writer.extractAudioFormat(
            new AudioInputStream(
                AudioInputStream.nullInputStream(),
                new AudioFormat(
                    VALID_SAMPLE_RATE,
                    VALID_SAMPLE_SIZE,
                    VALID_CHANNEL_COUNT,
                    true,
                    true
                ),
                0L
            )));
  }
}
