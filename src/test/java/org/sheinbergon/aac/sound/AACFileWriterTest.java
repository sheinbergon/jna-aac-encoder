package org.sheinbergon.aac.sound;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sheinbergon.aac.TestSupport;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

@DisplayName("Java AudioSystem AAC encoding support")
public class AACFileWriterTest {

    private final static AudioFileFormat.Type[] NO_FILE_TYPES = new AudioFileFormat.Type[0];
    private final static AudioFileFormat.Type[] AAC_FILE_TYPES = new AudioFileFormat.Type[]{AACFileTypes.AAC_LC};

    private final AACFileWriter writer = new AACFileWriter();

    @Test
    @DisplayName("Supported audio file types")
    public void supportedTypes() {
        Assertions.assertArrayEquals(AAC_FILE_TYPES, writer.getAudioFileTypes());
    }

    @Test
    @DisplayName("Unsupported audio input")
    public void unsupportedAudionInput() throws UnsupportedAudioFileException, IOException {
        AudioInputStream input = TestSupport.unsupported24bitWavAudioInputStream();
        Assertions.assertArrayEquals(NO_FILE_TYPES, writer.getAudioFileTypes(input));
    }

    @Test
    @DisplayName("Supported audio input")
    public void supportedAudionInput() throws UnsupportedAudioFileException, IOException {
        AudioInputStream input = TestSupport.supportedWavAudioInputStream();
        Assertions.assertArrayEquals(AAC_FILE_TYPES, writer.getAudioFileTypes(input));
    }

}
