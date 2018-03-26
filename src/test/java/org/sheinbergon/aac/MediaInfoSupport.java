package org.sheinbergon.aac;

import com.abercap.mediainfo.api.MediaInfo;
import org.junit.jupiter.api.Assertions;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;

import javax.sound.sampled.AudioFormat;
import java.io.File;

public class MediaInfoSupport {


    private final static String VALUE_DELIMITER = "/";
    private final static String AAC_MEDIAINFO_FORMAT = "AAC";
    private final static int AUDIO_STREAM_INDEX = 0;
    private final static int EXPECTED_AUDIO_STREAMS_COUNT = 1;

    private final static MediaInfo MEDIA_INFO = new MediaInfo();

    // Mediainfo sometimes provide multiple value delimited by '/'. This extract the first cell only.
    private static String getParam(String param) {
        return MEDIA_INFO.get(MediaInfo.StreamKind.Audio, AUDIO_STREAM_INDEX, param)
                .split(VALUE_DELIMITER)[0]
                .trim();
    }

    // Bitrate is not present as part of AAC's encoding metadata, but is rather deduced from the
    // stream itself. There for, it cannot be parsed by the mediainfo shared library
    // TODO - This should probably changed once metadata is added
    public static void assertAACOutput(File aac, AudioFormat inputFormat, AACEncodingProfile profile) {
        boolean opened = false;
        try {
            if (opened = MEDIA_INFO.open(aac)) {
                Assertions.assertEquals(MEDIA_INFO.streamCount(MediaInfo.StreamKind.Audio), EXPECTED_AUDIO_STREAMS_COUNT);
                Assertions.assertEquals(Float.valueOf(getParam("SamplingRate")).floatValue(), inputFormat.getSampleRate());
                Assertions.assertEquals(Integer.valueOf(getParam("Channel(s)")).intValue(), inputFormat.getChannels());
                Assertions.assertEquals(getParam("Format"), AAC_MEDIAINFO_FORMAT);
                Assertions.assertEquals(getParam("Format_Profile"), profile.getProfile());
            } else {
                throw new IllegalStateException("Could not open AAC file " + aac + " via the mediainfo shared library");
            }
        } finally {
            if (opened) {
                MEDIA_INFO.close();
            }
        }
    }
}
