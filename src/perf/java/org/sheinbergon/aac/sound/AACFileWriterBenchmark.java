package org.sheinbergon.aac.sound;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class AACFileWriterBenchmark {

    private final static String WAV_EXT = "." + AudioFileFormat.Type.WAVE.getExtension();
    private final static String AAC_EXT = "." + AACFileTypes.AAC_LC.getExtension();
    private final static String PREFIX = "perf";

    private final static int FORKS = 5;
    private final static int ITERATIONS = 15;
    private final static int WARMUPS = 5;

    private final static String INPUT_RESOURCE_NAME = "africa-toto.wav";

    private final static Map<AACEncodingProfile, AudioFileFormat.Type> ENCODING_PROFILES_TO_FILE_TYPES = Map.of(
            AACEncodingProfile.AAC_LC, AACFileTypes.AAC_LC,
            AACEncodingProfile.HE_AAC, AACFileTypes.AAC_HE,
            AACEncodingProfile.HE_AAC_V2, AACFileTypes.AAC_HE_V2);

    @Param({"1000000", "2000000", "5000000", "10000000"})
    private int inputBytes;

    @Param({"AAC_LC", "HE_AAC"})
    private AACEncodingProfile encodingProfile;

    private File tmpAudioInputFile;
    private URL audioInput;
    private File audioOutput;

    @Setup
    public void setup() throws IOException, UnsupportedAudioFileException {
        tmpAudioInputFile = truncatedInput();
        audioInput = tmpAudioInputFile.toURI().toURL();
        audioOutput = File.createTempFile(PREFIX, AAC_EXT);
    }

    @TearDown
    public void tearDown() throws IOException {
        Stream.of(audioOutput, tmpAudioInputFile)
                .filter(Objects::nonNull)
                .filter(File::exists)
                .forEach(File::delete);
    }

    private File truncatedInput() throws IOException, UnsupportedAudioFileException {
        URL fullAudioInput = AACFileWriterBenchmark.class.getClassLoader().getResource(INPUT_RESOURCE_NAME);
        AudioInputStream fullAudioInputStream = AudioSystem.getAudioInputStream(fullAudioInput);
        byte[] bytes = new byte[inputBytes];
        fullAudioInputStream.read(bytes);
        ByteArrayInputStream bytesInputStream = new ByteArrayInputStream(bytes);
        AudioInputStream truncatedAudioInputStream = new AudioInputStream(bytesInputStream, fullAudioInputStream.getFormat(), AudioSystem.NOT_SPECIFIED);
        File tmpFile = File.createTempFile(PREFIX, WAV_EXT);
        AudioSystem.write(truncatedAudioInputStream, AudioFileFormat.Type.WAVE, tmpFile);
        return tmpFile;
    }

    @Benchmark
    @Fork(FORKS)
    @Warmup(iterations = WARMUPS)
    @Measurement(iterations = ITERATIONS)
    public void aacFileWriter() throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioInput);
        AudioSystem.write(audioInputStream, ENCODING_PROFILES_TO_FILE_TYPES.get(encodingProfile), audioOutput);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().build();
        new Runner(options).run();
    }
}