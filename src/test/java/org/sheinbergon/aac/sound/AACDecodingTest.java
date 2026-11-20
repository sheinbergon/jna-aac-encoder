package org.sheinbergon.aac.sound;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.TestSupport;
import org.sheinbergon.aac.decoder.AACAudioDecoder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Stream;

@SuppressWarnings("MissingJavadocMethod")
@DisplayName("Java AudioSystem AAC decoding support")
public final class AACDecodingTest {

  // Encoder priming/decoder delay pad the decoded audio, never by more than a handful of frames
  private static final long FRAMES_TOLERANCE = 2048 * 6;
  private static final int NOT_AAC_LENGTH = 4096;
  private static final byte[] ADTS_SYNC_WORD = {(byte) 0xFF, (byte) 0xF1};

  private static final int ADTS_HEADER_LENGTH = 7;
  private static final int CORRUPTION_LENGTH = 3000;
  private static final int FEW_FRAMES_LENGTH = 2000;
  private static final float OTHER_SAMPLE_RATE = 8000f;
  private static final byte CORRUPTION = (byte) 0xFF;

  private final AACFileReader reader = new AACFileReader();
  private final AACFormatConversionProvider provider = new AACFormatConversionProvider();

  private static byte[] aac() throws Exception {
    val output = new ByteArrayOutputStream();
    try (val wav = TestSupport.supportedWAVAudioInputStream()) {
      AudioSystem.write(wav, AACFileTypes.AAC_LC, output);
    }
    return output.toByteArray();
  }

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
    val input = new BufferedInputStream(new ByteArrayInputStream(new byte[NOT_AAC_LENGTH]));
    Assertions.assertThrows(UnsupportedAudioFileException.class, () -> reader.getAudioInputStream(input));
    Assertions.assertEquals(NOT_AAC_LENGTH, input.readAllBytes().length);
    // A valid looking ADTS sync-word, followed by garbage
    val garbage = new byte[NOT_AAC_LENGTH];
    System.arraycopy(ADTS_SYNC_WORD, 0, garbage, 0, ADTS_SYNC_WORD.length);
    Assertions.assertThrows(UnsupportedAudioFileException.class,
        () -> reader.getAudioInputStream(new ByteArrayInputStream(garbage)));
  }

  @Test
  @DisplayName("Too short or mismatching ADTS headers are rejected")
  public void invalidHeaders() {
    val tooShort = new byte[] {CORRUPTION, CORRUPTION};
    val wrongLayer = new byte[NOT_AAC_LENGTH];
    wrongLayer[0] = CORRUPTION;
    Assertions.assertThrows(UnsupportedAudioFileException.class,
        () -> reader.getAudioFileFormat(new ByteArrayInputStream(tooShort)));
    Assertions.assertThrows(UnsupportedAudioFileException.class,
        () -> reader.getAudioFileFormat(new ByteArrayInputStream(wrongLayer)));
  }

  @Test
  @DisplayName("Non AAC files are rejected")
  public void unsupportedFile() throws Exception {
    val file = TestSupport.tempAACOutputFile();
    try {
      Files.write(file.toPath(), new byte[NOT_AAC_LENGTH]);
      Assertions.assertThrows(UnsupportedAudioFileException.class, () -> reader.getAudioFileFormat(file));
      Assertions.assertThrows(UnsupportedAudioFileException.class, () -> reader.getAudioInputStream(file));
    } finally {
      Files.deleteIfExists(file.toPath());
    }
  }

  @Test
  @DisplayName("Corrupt leading AAC frame is rejected")
  public void corruptLeadingFrame() throws Exception {
    val corrupt = aac();
    Arrays.fill(corrupt, ADTS_HEADER_LENGTH, 2 * ADTS_HEADER_LENGTH, CORRUPTION);
    Assertions.assertThrows(UnsupportedAudioFileException.class,
        () -> reader.getAudioInputStream(new ByteArrayInputStream(corrupt)));
  }

  @Test
  @DisplayName("Mid-stream corruption fails decoding")
  public void corruptMidStream() throws Exception {
    val corrupt = aac();
    Arrays.fill(corrupt, corrupt.length / 2, corrupt.length / 2 + CORRUPTION_LENGTH, CORRUPTION);
    try (val encoded = reader.getAudioInputStream(new ByteArrayInputStream(corrupt));
         val decoded = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, encoded)) {
      Assertions.assertThrows(IOException.class, decoded::readAllBytes);
    }
  }

  @Test
  @DisplayName("Closed decoder")
  public void closedDecoder() throws Exception {
    val decoder = new AACAudioDecoder(new ByteArrayInputStream(aac()));
    decoder.close();
    decoder.close();
    Assertions.assertThrows(IllegalStateException.class, decoder::decode);
  }

  @Test
  @DisplayName("Byte by byte decoded stream reads")
  public void singleByteReads() throws Exception {
    val fewFrames = Arrays.copyOf(aac(), FEW_FRAMES_LENGTH);
    val decoder = new AACAudioDecoder(new ByteArrayInputStream(fewFrames));
    try (val stream = new AACFormatConversionProvider.DecodingInputStream(decoder)) {
      int count = 0;
      Assertions.assertEquals(0, stream.read(new byte[0], 0, 0));
      while (stream.read() >= 0) {
        count++;
      }
      Assertions.assertTrue(count > 0 && count % (decoder.channels() * Short.BYTES) == 0, "Decoded " + count + " bytes");
      Assertions.assertEquals(-1, stream.read());
    }
  }

  @Test
  @DisplayName("Supported conversions")
  public void conversions() throws Exception {
    try (val encoded = reader.getAudioInputStream(new ByteArrayInputStream(aac()))) {
      val aacFormat = encoded.getFormat();
      val pcmFormat = provider.getTargetFormats(AudioFormat.Encoding.PCM_SIGNED, aacFormat)[0];
      val otherFormat = new AudioFormat(OTHER_SAMPLE_RATE, Short.SIZE, 1, true, false);
      Assertions.assertArrayEquals(new AudioFormat.Encoding[] {AACFileTypes.AAC_ENCODING}, provider.getSourceEncodings());
      Assertions.assertArrayEquals(
          new AudioFormat.Encoding[] {AudioFormat.Encoding.PCM_SIGNED}, provider.getTargetEncodings(aacFormat));
      Assertions.assertEquals(0, provider.getTargetEncodings(pcmFormat).length);
      Assertions.assertEquals(0, provider.getTargetFormats(AudioFormat.Encoding.ULAW, aacFormat).length);
      Assertions.assertEquals(0, provider.getTargetFormats(AudioFormat.Encoding.PCM_SIGNED, pcmFormat).length);
      Assertions.assertThrows(IllegalArgumentException.class,
          () -> provider.getAudioInputStream(AudioFormat.Encoding.ULAW, encoded));
      Assertions.assertThrows(IllegalArgumentException.class, () -> provider.getAudioInputStream(otherFormat, encoded));
    }
  }
}
