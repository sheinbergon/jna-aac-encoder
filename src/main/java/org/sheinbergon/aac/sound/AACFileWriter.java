package org.sheinbergon.aac.sound;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.AudioFileWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class AACFileWriter extends AudioFileWriter {

    public final static AudioFileFormat.Type AAC = new AudioFileFormat.Type("AAC", "aac");

    @Override
    public AudioFileFormat.Type[] getAudioFileTypes() {
        return new AudioFileFormat.Type[]{AAC};
    }

    // TODO - This should return a result based on stream format
    @Override
    public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream stream) {
        return getAudioFileTypes();
    }

    @Override
    public int write(AudioInputStream stream, AudioFileFormat.Type fileType, OutputStream out) throws IOException {
        return 0;
    }

    @Override
    public int write(AudioInputStream stream, AudioFileFormat.Type fileType, File out) throws IOException {
        return 0;
    }
}
