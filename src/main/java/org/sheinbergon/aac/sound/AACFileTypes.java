package org.sheinbergon.aac.sound;

import javax.sound.sampled.AudioFileFormat;

/**
 * Currently, only AAC-LC is supported
 */
public class AACFileTypes {
    public final static AudioFileFormat.Type AAC_LC = new AudioFileFormat.Type("AAC_LC", "aac");
}