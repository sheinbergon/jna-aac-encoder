package org.sheinbergon.aac.sound;

import org.sheinbergon.aac.encoder.AACAudioEncoder;
import org.sheinbergon.aac.encoder.WAVAudioInput;
import org.sheinbergon.aac.encoder.AACAudioOutput;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;
import org.sheinbergon.aac.encoder.util.WAVAudioSupport;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.spi.AudioFileWriter;
import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class AACFileWriter extends AudioFileWriter {

    private final static int OUTPUT_BUFFER_SIZE = 16384;

    private final static int INPUT_BUFFER_MULTIPLIER = 4;


    private final static Map<AudioFileFormat.Type, AACEncodingProfile> FILE_TYPES_TO_ENCODING_PROFILES = Map.of(
            AACFileTypes.AAC_LC, AACEncodingProfile.AAC_LC);


    @Override
    public AudioFileFormat.Type[] getAudioFileTypes() {
        return Stream.of(AACFileTypes.AAC_LC).toArray(AudioFileFormat.Type[]::new);
    }

    @Override
    public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream stream) {
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

    private static AACEncodingProfile profileByType(AudioFileFormat.Type type) {
        return Optional.ofNullable(FILE_TYPES_TO_ENCODING_PROFILES.get(type))
                .orElseThrow(() -> new IllegalArgumentException("File type " + type + " is not yet supported" ));
    }

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
            throw new IllegalArgumentException("File type " + fileType + " is not supported." );
        }
        return encodeAndWrite(stream, fileType, out);
    }

    @Override
    public int write(AudioInputStream stream, AudioFileFormat.Type fileType, File out) throws IOException {
        Objects.requireNonNull(stream);
        Objects.requireNonNull(fileType);
        Objects.requireNonNull(out);

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(out), OUTPUT_BUFFER_SIZE);
        try (bufferedOutputStream) {
            return write(stream, fileType, bufferedOutputStream);
        }
    }

    private int readBufferSize(AudioFormat format, AACAudioEncoder encoder) {
        return encoder.inputBufferSize(format.getChannels()) * INPUT_BUFFER_MULTIPLIER;
    }

    private int encodeAndWrite(AudioInputStream input, AudioFileFormat.Type type, OutputStream output) throws IOException {
        int read, encoded = 0;
        AudioFormat format = input.getFormat();
        AACAudioEncoder encoder = encoder(format, type);
        try (encoder) {
            int readBufferSize = readBufferSize(format, encoder);
            byte[] readBuffer = new byte[readBufferSize];
            while ((read = input.read(readBuffer)) != WAVAudioSupport.EOS) {
                WAVAudioInput audioInput = WAVAudioInput.pcms16le(readBuffer, read, format.getChannels());
                boolean conclude = read < readBufferSize;
                AACAudioOutput audioOutput = encoder.encode(audioInput, conclude);
                encoded += audioOutput.length();
                output.write(audioOutput.data());
            }
        }
        return encoded;
    }
}
