package org.sheinbergon.aac;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class TestSupport {

    private final static String SUPPORTED_WAV = "supported.wav";


    private final static String UNSUPPORTED_24BIT_WAV = "unsupported-24bit.wav";

    public static AudioInputStream supportedWavAudioInputStream() throws IOException, UnsupportedAudioFileException {
        InputStream input = TestSupport.class.getClassLoader().getResourceAsStream(SUPPORTED_WAV);
        return AudioSystem.getAudioInputStream(input);
    }


    public static AudioInputStream unsupported24bitWavAudioInputStream() throws IOException, UnsupportedAudioFileException {
        InputStream input = TestSupport.class.getClassLoader().getResourceAsStream(UNSUPPORTED_24BIT_WAV);
        return AudioSystem.getAudioInputStream(input);
    }
}
