package org.sheinbergon.aac.encoder.util;

import org.apache.commons.lang3.Range;

public class WAVAudioSupport {

    public final static int SUPPORTED_SAMPLE_SIZE = 16;

    public final static Range<Integer> SUPPORTED_CHANNELS_RANGE = Range.between(1, 6);

    public final static int EOS = -1;
}
