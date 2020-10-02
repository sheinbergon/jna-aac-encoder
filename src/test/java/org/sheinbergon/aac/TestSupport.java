package org.sheinbergon.aac;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class TestSupport {

    private static final String TEMP_FILE_PREFIX = "test";
    private static final String AAC_FILE_SUFFIX = ".aac";

    private static final String SUPPORTED_WAV = "supported.wav";

    private static final String UNSUPPORTED_24BIT_WAV = "unsupported-24bit.wav";

    public static AudioInputStream supportedWAVAudioInputStream() throws IOException, UnsupportedAudioFileException {
        InputStream input = TestSupport.class.getClassLoader().getResourceAsStream(SUPPORTED_WAV);
        return AudioSystem.getAudioInputStream(input);
    }

    public static AudioInputStream unsupported24bitWAVAudioInputStream()
            throws IOException, UnsupportedAudioFileException {
        InputStream input = TestSupport.class.getClassLoader().getResourceAsStream(UNSUPPORTED_24BIT_WAV);
        return AudioSystem.getAudioInputStream(input);
    }

    public static File tempAACOutputFile() throws IOException {
        return File.createTempFile(TEMP_FILE_PREFIX, AAC_FILE_SUFFIX);
    }

    private TestSupport() {
    }
}
