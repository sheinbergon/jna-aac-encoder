package org.sheinbergon.aac.sound;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.MediaInfoSupport;
import org.sheinbergon.aac.TestSupport;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

// TODO - Consider adding actual byte-level comparison encoding tests
@DisplayName("Java AudioSystem AAC encoding support")
public class AACFileWriterTest {

    private final static AudioFileFormat.Type[] NO_FILE_TYPES = new AudioFileFormat.Type[0];
    private final static AudioFileFormat.Type[] AAC_FILE_TYPES = new AudioFileFormat.Type[]{AACFileTypes.AAC_LC, AACFileTypes.AAC_HE, AACFileTypes.AAC_HE_V2};
    private final static AACEncodingProfile[] AAC_AUDIO_TYPES = new AACEncodingProfile[]{AACEncodingProfile.AAC_LC, AACEncodingProfile.HE_AAC, AACEncodingProfile.HE_AAC_V2};
    private final AACFileWriter writer = new AACFileWriter();

    @Test
    @DisplayName("Supported audio file types")
    void supportedTypes() {
        Assertions.assertArrayEquals(AAC_FILE_TYPES, writer.getAudioFileTypes());
    }

    @Test
    @DisplayName("Unsupported audio input")
    void unsupportedAudioInput() {
        AudioInputStream input = TestSupport.unsupported32bitWAVAudioInputStream();
        Assertions.assertArrayEquals(NO_FILE_TYPES, writer.getAudioFileTypes(input));
    }

    @Test
    @DisplayName("Supported audio inputs")
    void supportedAudioInput() {
        Assertions.assertAll(TestSupport.supportedWAVAudioInputStreams()
                .map(input -> () -> Assertions.assertArrayEquals(AAC_FILE_TYPES, writer.getAudioFileTypes(input))));
    }

    @Test
    @DisplayName("Unsupported audio input encoding")
    public void unsupportedAudioEncoding() {
        File aac = TestSupport.tempAACOutputFile();
        AudioInputStream input = TestSupport.unsupported32bitWAVAudioInputStream();
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
    @DisplayName("Supported audio inputs encoding (LC/HE-AAC/HE-AACv2 types, 16/24 bits inputs)")
    void supportedAudioEncoding() {
        Assertions.assertAll(Stream.of(AAC_FILE_TYPES)
                .flatMap(type -> TestSupport.supportedWAVAudioInputStreams().map(input -> Pair.of(type, input)))
                .map(pair -> () -> {
                    File aac = TestSupport.tempAACOutputFile();
                    FileOutputStream output = new FileOutputStream(aac);
                    AudioFileFormat.Type type = pair.getLeft();
                    AudioInputStream input = pair.getRight();
                    try (input; output) {
                        writer.write(input, type, output);
                        Assertions.assertTrue(aac.length() > 0);
                        output.flush();
                        MediaInfoSupport.assertAACOutput(aac, input.getFormat(), AACFileWriter.profileByType(type));
                    } finally {
                        if (aac.exists()) {
                            Files.delete(aac.toPath());
                        }
                    }
                }));
    }

    @Test
    @DisplayName("Supported audio file encoding")
    void supportedAudioFileEncoding() throws IOException {
        File aac = TestSupport.tempAACOutputFile();
        AudioInputStream input = TestSupport.supported16bitWAVAudioInputStream();
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
}