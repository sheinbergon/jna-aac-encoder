package org.sheinbergon.aac;

import org.junit.jupiter.api.Assertions;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;
import org.sheinbergon.mediainfo.jna.MediaInfoLibFacade;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

@SuppressWarnings("MissingJavadocMethod")
public final class MediaInfoSupport {

  private static final String VALUE_DELIMITER = "/";
  private static final String AAC_MEDIAINFO_FORMAT = "AAC";
  private static final int AUDIO_STREAM_INDEX = 0;
  private static final int EXPECTED_AUDIO_STREAMS_COUNT = 1;

  private static final MediaInfoLibFacade MEDIA_INFO = new MediaInfoLibFacade();

  // Mediainfo sometimes provide multiple value delimited by '/'. This extract the first cell only.
  private static String getParam(final String param) {
    return MEDIA_INFO.get(MediaInfoLibFacade.StreamKind.Audio, AUDIO_STREAM_INDEX, param)
        .split(VALUE_DELIMITER)[0]
        .trim();
  }

  // Bitrate is not present as part of AAC's encoding metadata, but is rather deduced from the
  // stream itself. There for, it cannot be parsed by the mediainfo shared library
  // TODO - This should probably changed once metadata is added
  public static void assertAACOutput(
      final File aac,
      final AudioFormat inputFormat,
      final AACEncodingProfile profile) {
    boolean opened = false;
    try {
      if (opened = MEDIA_INFO.open(aac)) {
        Assertions
            .assertEquals(EXPECTED_AUDIO_STREAMS_COUNT, MEDIA_INFO.streamCount(MediaInfoLibFacade.StreamKind.Audio));
        Assertions.assertEquals(inputFormat.getSampleRate(),
            Float.valueOf(getParam("SamplingRate")).floatValue());
        Assertions.assertEquals(inputFormat.getChannels(), Integer.valueOf(getParam("Channel(s)")).intValue());
        Assertions.assertEquals(AAC_MEDIAINFO_FORMAT, getParam("Format"));
        Assertions.assertTrue(new HashSet<>(Arrays.asList(
            getParam("Format_AdditionalFeatures"),
            getParam("Format_Commercial")
        )).contains(profile.code()));
      } else {
        throw new IllegalStateException("Could not open AAC file " + aac + " via the mediainfo shared library");
      }
    } finally {
      if (opened) {
        MEDIA_INFO.close();
      }
    }
  }

  private MediaInfoSupport() {
  }
}
