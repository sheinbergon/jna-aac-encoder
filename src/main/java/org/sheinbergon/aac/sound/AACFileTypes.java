package org.sheinbergon.aac.sound;

import javax.sound.sampled.AudioFileFormat;

public final class AACFileTypes {
    /**
     * Low-Complexity AAC profile file type.
     */
    public static final AudioFileFormat.Type AAC_LC = new AudioFileFormat.Type("AAC_LC", "aac");
    /**
     * High-Efficiency AAC profile file type.
     */
    public static final AudioFileFormat.Type AAC_HE = new AudioFileFormat.Type("AAC_HE", "aac");
    /**
     * High-Efficiency AAC (v2) profile file type.
     */
    public static final AudioFileFormat.Type AAC_HE_V2 = new AudioFileFormat.Type("AAC_HE_V2", "aac");

    private AACFileTypes() {
    }
}
