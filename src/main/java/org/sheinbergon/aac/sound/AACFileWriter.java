package org.sheinbergon.aac.sound;

import lombok.val;
import org.sheinbergon.aac.encoder.AACAudioEncoder;
import org.sheinbergon.aac.encoder.AACAudioOutput;
import org.sheinbergon.aac.encoder.WAVAudioInput;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;

import javax.annotation.Nonnull;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.AudioFileWriter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class AACFileWriter extends AudioFileWriter {

  private static final int OUTPUT_BUFFER_SIZE = 16384;

  private static final int INPUT_BUFFER_MULTIPLIER = 16;

  private static final Map<AudioFileFormat.Type, AACEncodingProfile> FILE_TYPES_TO_ENCODING_PROFILES = Map.of(
      AACFileTypes.AAC_LC, AACEncodingProfile.AAC_LC,
      AACFileTypes.AAC_HE, AACEncodingProfile.HE_AAC,
      AACFileTypes.AAC_HE_V2, AACEncodingProfile.HE_AAC_V2
  );

  @Override
  public AudioFileFormat.Type[] getAudioFileTypes() {
    return Stream.of(AACFileTypes.AAC_LC, AACFileTypes.AAC_HE, AACFileTypes.AAC_HE_V2)
        .toArray(AudioFileFormat.Type[]::new);
  }

  @Override
  public AudioFileFormat.Type[] getAudioFileTypes(final @Nonnull AudioInputStream stream) {
    AudioFormat format = stream.getFormat();
    if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)
        && format.getSampleSizeInBits() == WAVAudioSupport.SUPPORTED_SAMPLE_SIZE
        && WAVAudioSupport.SUPPORTED_CHANNELS_RANGE.contains(format.getChannels())
        && !format.isBigEndian()) {
      return getAudioFileTypes();
    } else {
      return new AudioFileFormat.Type[0];
    }
  }

  static AACEncodingProfile profileByType(final @Nonnull AudioFileFormat.Type type) {
    return Optional.ofNullable(FILE_TYPES_TO_ENCODING_PROFILES.get(type))
        .orElseThrow(() -> new IllegalArgumentException("File type " + type + " is not yet supported"));
  }

  // Note that the bitRate is adapted automatically based on the input specification
  private static AACAudioEncoder encoder(
      final @Nonnull AudioFormat format,
      final @Nonnull AudioFileFormat.Type type) {
    return AACAudioEncoder.builder()
        .afterBurner(true)
        .channels(format.getChannels())
        .sampleRate((int) format.getSampleRate())
        .profile(profileByType(type))
        .build();
  }

  @Override
  public int write(
      final @Nonnull AudioInputStream stream,
      final @Nonnull AudioFileFormat.Type fileType,
      final @Nonnull OutputStream out) throws IOException {
    Objects.requireNonNull(stream);
    Objects.requireNonNull(fileType);
    Objects.requireNonNull(out);

    if (!isFileTypeSupported(fileType, stream)) {
      throw new IllegalArgumentException("File type " + fileType + " is not supported.");
    }
    return encodeAndWrite(stream, fileType, out);
  }

  @Override
  public int write(
      final @Nonnull AudioInputStream stream,
      final @Nonnull AudioFileFormat.Type fileType,
      final @Nonnull File out) throws IOException {
    Objects.requireNonNull(stream);
    Objects.requireNonNull(fileType);
    Objects.requireNonNull(out);
    try (val bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(out), OUTPUT_BUFFER_SIZE)) {
      return write(stream, fileType, bufferedOutputStream);
    }
  }

  private int readBufferSize(
      final @Nonnull AudioFormat format,
      final @Nonnull AACAudioEncoder encoder) {
    return encoder.inputBufferSize() * INPUT_BUFFER_MULTIPLIER;
  }

  private int encodeAndWrite(
      final @Nonnull AudioInputStream input,
      final @Nonnull AudioFileFormat.Type type,
      final @Nonnull OutputStream output) throws IOException {
    boolean concluded = false;
    int read, encoded = 0;
    AudioFormat format = input.getFormat();
    try (val encoder = encoder(format, type)) {
      int readBufferSize = readBufferSize(format, encoder);
      byte[] readBuffer = new byte[readBufferSize];
      AACAudioOutput audioOutput;
      while (!concluded) {
        read = input.read(readBuffer);
        if (read == WAVAudioSupport.EOS) {
          audioOutput = encoder.conclude();
          concluded = true;
        } else {
          WAVAudioInput audioInput = WAVAudioInput.pcms16le(readBuffer, read);
          audioOutput = encoder.encode(audioInput);
        }
        if (audioOutput.length() > 0) {
          encoded += audioOutput.length();
          output.write(audioOutput.data());
        }
      }
    }
    return encoded;
  }
}
