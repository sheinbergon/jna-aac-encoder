package org.sheinbergon.aac;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public class TestSupport {

    private final static String TEMP_FILE_PREFIX = "test";
    private final static String AAC_FILE_SUFFIX = ".aac";

    private final static String SUPPORTED_16BIT_WAV = "supported-16bit.wav";
    private final static String SUPPORTED_24BIT_WAV = "supported-24bit.wav";
    private final static String UNSUPPORTED_32BIT_WAV = "unsupported-32bit.wav";

    public static AudioInputStream supported16bitWAVAudioInputStream() {
        try {
            InputStream input = TestSupport.class.getClassLoader().getResourceAsStream(SUPPORTED_16BIT_WAV);
            return AudioSystem.getAudioInputStream(input);
        } catch (IOException | UnsupportedAudioFileException x) {
            throw new RuntimeException(x);
        }
    }

    public static AudioInputStream supported24bitWAVAudioInputStream() {
        try {
            InputStream input = TestSupport.class.getClassLoader().getResourceAsStream(SUPPORTED_24BIT_WAV);
            return AudioSystem.getAudioInputStream(input);
        } catch (IOException | UnsupportedAudioFileException x) {
            throw new RuntimeException(x);
        }
    }

    public static Stream<AudioInputStream> supportedWAVAudioInputStreams() {
        return Stream.of(supported16bitWAVAudioInputStream(), supported24bitWAVAudioInputStream());
    }


    public static AudioInputStream unsupported32bitWAVAudioInputStream() {
        try {
            InputStream input = TestSupport.class.getClassLoader().getResourceAsStream(UNSUPPORTED_32BIT_WAV);
            return AudioSystem.getAudioInputStream(input);
        } catch (IOException | UnsupportedAudioFileException x) {
            throw new RuntimeException(x);
        }
    }

    public static File tempAACOutputFile() {
        try {
            return File.createTempFile(TEMP_FILE_PREFIX, AAC_FILE_SUFFIX);
        } catch (IOException x) {
            throw new RuntimeException(x);
        }
    }
}
