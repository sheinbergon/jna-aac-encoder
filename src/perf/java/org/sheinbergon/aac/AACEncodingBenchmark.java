package org.sheinbergon.aac;

import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.*;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;
import org.sheinbergon.aac.sound.AACFileTypes;

import javax.sound.sampled.*;
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
public class AACEncodingBenchmark {

    private final static String AAC_ENC_BIN = System.getProperty("perf.aac.enc.bin");
    private final static String AAC_ENC_COMMAND_TEMPLATE = AAC_ENC_BIN + " -r %d -t %d -a 1 %s %s";

    private final static Map<AACEncodingProfile, Float> AAC_ENCODING_PROFILE_BITRATE_FACTOR = Map.of(
            AACEncodingProfile.AAC_LC, 1.5f,
            AACEncodingProfile.HE_AAC, 0.625f);

    private final static String WAV_EXT = "." + AudioFileFormat.Type.WAVE.getExtension();
    private final static String AAC_EXT = "." + AACFileTypes.AAC_LC.getExtension();
    private final static String PREFIX = "perf";

    private final static int FORKS = 3;
    private final static int ITERATIONS = 15;
    private final static int WARMUPS = 5;

    private final static String INPUT_RESOURCE_NAME = "africa-toto.wav";

    private final static Map<AACEncodingProfile, AudioFileFormat.Type> ENCODING_PROFILES_TO_FILE_TYPES = Map.of(
            AACEncodingProfile.AAC_LC, AACFileTypes.AAC_LC,
            AACEncodingProfile.HE_AAC, AACFileTypes.AAC_HE,
            AACEncodingProfile.HE_AAC_V2, AACFileTypes.AAC_HE_V2);

    /* This is manually set by each benchmark iteration. @Param annotations are not used
     * in order to allow better visualization via JMH visualizer.
     */
    private volatile static AACEncodingProfile encodingProfile;

    public enum Mode {
        BINARY, JNA
    }

    @Param({"BINARY", "JNA"})
    private Mode mode;

    @Param({"1000000", "2000000", "5000000", "10000000"})
    private int inputBytes;

    private File tmpAudioInputFile;
    private AudioFormat tmpAudioInputFormat;
    private URL audioInput;
    private File audioOutput;

    @Setup
    public void setup() throws IOException, UnsupportedAudioFileException {
        tmpAudioInputFile = truncatedInput();
        tmpAudioInputFormat = AudioSystem.getAudioFileFormat(tmpAudioInputFile).getFormat();
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
        URL fullAudioInput = AACEncodingBenchmark.class.getClassLoader().getResource(INPUT_RESOURCE_NAME);
        AudioInputStream fullAudioInputStream = AudioSystem.getAudioInputStream(fullAudioInput);
        byte[] bytes = new byte[inputBytes];
        fullAudioInputStream.read(bytes);
        ByteArrayInputStream bytesInputStream = new ByteArrayInputStream(bytes);
        AudioInputStream truncatedAudioInputStream = new AudioInputStream(bytesInputStream, fullAudioInputStream.getFormat(), AudioSystem.NOT_SPECIFIED);
        File tmpFile = File.createTempFile(PREFIX, WAV_EXT);
        AudioSystem.write(truncatedAudioInputStream, AudioFileFormat.Type.WAVE, tmpFile);
        return tmpFile;
    }

    private int bitRate() {
        return (int) (tmpAudioInputFormat.getChannels() * tmpAudioInputFormat.getSampleRate() * AAC_ENCODING_PROFILE_BITRATE_FACTOR.get(encodingProfile));
    }

    private String[] composeAACEncCommand() {
        return String.format(AAC_ENC_COMMAND_TEMPLATE,
                bitRate(),
                encodingProfile.getAot(),
                tmpAudioInputFile.getAbsolutePath(),
                audioOutput.getAbsolutePath()
        ).split(StringUtils.SPACE);
    }

    /* Grouping by 'mode' @Param and then switching upon the mode gives visually comparable results,
     * where results BINARY(aac-enc binary) and JNA(this library) benchmarks are displayed side-by-side.
     */
    private void handleEncoding() throws IOException, UnsupportedAudioFileException, InterruptedException {
        switch (mode) {
            case BINARY: {
                new ProcessBuilder(composeAACEncCommand())
                        .inheritIO() // Redirect stdout/err to those of the JVM
                        .start()
                        .waitFor();
                break;
            }
            case JNA: {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioInput);
                AudioSystem.write(audioInputStream, ENCODING_PROFILES_TO_FILE_TYPES.get(AACEncodingProfile.AAC_LC), audioOutput);
                break;
            }
        }
    }

    /* Splitting benchmarks per encoding profile allows for better group visualization
     * via JMH visualizer ( when comparing to using an additional @Param annotation or
     * a benchmark per execution-mode instead.
     */

    @Benchmark
    @Fork(FORKS)
    @Warmup(iterations = WARMUPS)
    @Measurement(iterations = ITERATIONS)
    public void AAC_LC() throws IOException, UnsupportedAudioFileException, InterruptedException {
        encodingProfile = AACEncodingProfile.AAC_LC;
        handleEncoding();
    }

    @Benchmark
    @Fork(FORKS)
    @Warmup(iterations = WARMUPS)
    @Measurement(iterations = ITERATIONS)
    public void HE_AAC() throws IOException, UnsupportedAudioFileException, InterruptedException {
        encodingProfile = AACEncodingProfile.HE_AAC;
        handleEncoding();
    }
}