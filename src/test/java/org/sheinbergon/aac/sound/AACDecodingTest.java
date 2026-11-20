package org.sheinbergon.aac.sound;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.TestSupport;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.util.stream.Stream;

@SuppressWarnings("MissingJavadocMethod")
@DisplayName("Java AudioSystem AAC decoding support")
public final class AACDecodingTest {

  // Encoder priming/decoder delay pad the decoded audio, never by more than a handful of frames
  private static final long FRAMES_TOLERANCE = 2048 * 6;
  private static final int NOT_AAC_LENGTH = 4096;
  private static final byte[] ADTS_SYNC_WORD = {(byte) 0xFF, (byte) 0xF1};

  private static boolean anyNonZero(final byte[] bytes) {
    for (byte value : bytes) {
      if (value != 0) {
        return true;
      }
    }
    return false;
  }

  @Test
  @DisplayName("Encode/decode round-trip (LC/HE-AAC/HE-AACv2)")
  public void roundTrip() {
    Assertions.assertAll(Stream.of(AACFileTypes.AAC_LC, AACFileTypes.AAC_HE, AACFileTypes.AAC_HE_V2).map(type -> () -> {
      val aac = TestSupport.tempAACOutputFile();
      try (val wav = TestSupport.supportedWAVAudioInputStream()) {
        val source = wav.getFormat();
        AudioSystem.write(wav, type, aac);
        Assertions.assertEquals(AACFileTypes.AAC, AudioSystem.getAudioFileFormat(aac).getType());
        try (val encoded = AudioSystem.getAudioInputStream(aac);
             val decoded = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, encoded)) {
          Assertions.assertEquals(AACFileTypes.AAC_ENCODING, encoded.getFormat().getEncoding());
          Assertions.assertTrue(source.matches(decoded.getFormat()), type + " decoded as " + decoded.getFormat());
          val pcm = decoded.readAllBytes();
          val frames = pcm.length / source.getFrameSize();
          Assertions.assertTrue(Math.abs(frames - wav.getFrameLength()) <= FRAMES_TOLERANCE,
              type + " decoded " + frames + " frames out of " + wav.getFrameLength());
          Assertions.assertTrue(anyNonZero(pcm), type + " decoded to silence");
        }
      } finally {
        Files.deleteIfExists(aac.toPath());
      }
    }));
  }

  @Test
  @DisplayName("Non AAC input is rejected, with the stream left untouched")
  public void unsupportedInput() throws Exception {
    val reader = new AACFileReader();
    val input = new BufferedInputStream(new ByteArrayInputStream(new byte[NOT_AAC_LENGTH]));
    Assertions.assertThrows(UnsupportedAudioFileException.class, () -> reader.getAudioInputStream(input));
    Assertions.assertEquals(NOT_AAC_LENGTH, input.readAllBytes().length);
    // A valid looking ADTS sync-word, followed by garbage
    val garbage = new byte[NOT_AAC_LENGTH];
    System.arraycopy(ADTS_SYNC_WORD, 0, garbage, 0, ADTS_SYNC_WORD.length);
    Assertions.assertThrows(UnsupportedAudioFileException.class,
        () -> reader.getAudioInputStream(new ByteArrayInputStream(garbage)));
  }
}
