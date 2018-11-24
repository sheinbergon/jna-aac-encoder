package org.sheinbergon.aac.sound;

import lombok.val;
import org.sheinbergon.aac.encoder.AACAudioEncoder;
import org.sheinbergon.aac.encoder.AACAudioOutput;
import org.sheinbergon.aac.encoder.WAVAudioInput;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;
import org.sheinbergon.aac.encoder.util.WAVAudioInputException;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;

import javax.annotation.Nonnull;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.AudioFileWriter;
import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class AACFileWriter extends AudioFileWriter {

    private final static int OUTPUT_BUFFER_SIZE = 16384;
    private final static int INPUT_BUFFER_MULTIPLIER = 8;

    private final static Map<AudioFileFormat.Type, AACEncodingProfile> FILE_TYPES_TO_ENCODING_PROFILES = Map.of(
            AACFileTypes.AAC_LC, AACEncodingProfile.AAC_LC,
            AACFileTypes.AAC_HE, AACEncodingProfile.HE_AAC,
            AACFileTypes.AAC_HE_V2, AACEncodingProfile.HE_AAC_V2);

    @Override
    public AudioFileFormat.Type[] getAudioFileTypes() {
        return Stream.of(AACFileTypes.AAC_LC, AACFileTypes.AAC_HE, AACFileTypes.AAC_HE_V2)
                .toArray(AudioFileFormat.Type[]::new);
    }

    @Override
    public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream stream) {
        AudioFormat format = stream.getFormat();
        if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)
                && WAVAudioSupport.SampleSize.valid(format.getSampleSizeInBits())
                && WAVAudioSupport.SUPPORTED_CHANNELS_RANGE.contains(format.getChannels())
                && !format.isBigEndian()) {
            return getAudioFileTypes();
        } else {
            return new AudioFileFormat.Type[0];
        }
    }

    @Nonnull
    static AACEncodingProfile profileByType(AudioFileFormat.Type type) {
        return Optional.ofNullable(FILE_TYPES_TO_ENCODING_PROFILES.get(type))
                .orElseThrow(() -> new IllegalArgumentException("File type " + type + " is not yet supported"));
    }

    @Nonnull
    // Note that the bitRate is adapted automatically based on the input specification
    private static AACAudioEncoder encoder(AudioFormat format, AudioFileFormat.Type type) {
        return AACAudioEncoder.builder()
                .afterBurner(true)
                .channels(format.getChannels())
                .sampleRate((int) format.getSampleRate())
                .profile(profileByType(type))
                .build();
    }

    @Override
    public int write(AudioInputStream stream, AudioFileFormat.Type fileType, OutputStream out) throws IOException {
        Objects.requireNonNull(stream);
        Objects.requireNonNull(fileType);
        Objects.requireNonNull(out);

        if (!isFileTypeSupported(fileType, stream)) {
            throw new IllegalArgumentException("File type " + fileType + " is not supported.");
        }
        return encodeAndWrite(stream, fileType, out);
    }

    @Override
    public int write(AudioInputStream stream, AudioFileFormat.Type fileType, File out) throws IOException {
        Objects.requireNonNull(stream);
        Objects.requireNonNull(fileType);
        Objects.requireNonNull(out);

        val bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(out), OUTPUT_BUFFER_SIZE);
        try (bufferedOutputStream) {
            return write(stream, fileType, bufferedOutputStream);
        }
    }

    private int readBufferSize(AudioFormat format, AACAudioEncoder encoder) {
        val sampleSize = format.getSampleSizeInBits();
        val bufferSize = encoder.inputBufferSize() * INPUT_BUFFER_MULTIPLIER;
        switch (format.getSampleSizeInBits()) {
            case WAVAudioSupport.SampleSize._16_BITS:
                return bufferSize * WAVAudioSupport.SampleSize._16_BITS / Byte.SIZE;
            case WAVAudioSupport.SampleSize._24_BITS:
                return bufferSize * WAVAudioSupport.SampleSize._24_BITS / Byte.SIZE;
            default:
                throw new WAVAudioInputException(String.format("Unsupported sample size - '%d'", sampleSize));
        }
    }

    @Nonnull
    private WAVAudioInput input(AudioFormat format, byte[] data, int length) {
        val sampleSize = format.getSampleSizeInBits();
        switch (sampleSize) {
            case WAVAudioSupport.SampleSize._16_BITS:
                return WAVAudioInput.pcms16le(data, length);
            case WAVAudioSupport.SampleSize._24_BITS:
                return WAVAudioInput.pcms24le(data, length);
            default:
                throw new WAVAudioInputException(String.format("Unsupported sample size - '%d'", sampleSize));
        }
    }

    private int encodeAndWrite(AudioInputStream input, AudioFileFormat.Type type, OutputStream output) throws IOException {
        boolean concluded = false;
        int read, encoded = 0;
        AudioFormat format = input.getFormat();
        val encoder = encoder(format, type);
        try (encoder) {
            int bufferSize = readBufferSize(format, encoder);
            byte[] buffer = new byte[bufferSize];
            AACAudioOutput audioOutput;
            while (!concluded) {
                read = input.read(buffer);
                if (read == WAVAudioSupport.EOS) {
                    audioOutput = encoder.conclude();
                    concluded = true;
                } else {
                    WAVAudioInput audioInput = input(format, buffer, read);
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
