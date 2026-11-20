package org.sheinbergon.aac.sound;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

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
  /**
   * Generic AAC (ADTS) file type, as reported when reading AAC files.
   */
  public static final AudioFileFormat.Type AAC = new AudioFileFormat.Type("AAC", "aac");
  /**
   * AAC audio encoding, as reported for (not yet decoded) AAC audio streams.
   */
  public static final AudioFormat.Encoding AAC_ENCODING = new AudioFormat.Encoding("AAC");

  private AACFileTypes() {
  }
}
