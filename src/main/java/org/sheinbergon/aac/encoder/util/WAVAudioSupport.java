package org.sheinbergon.aac.encoder.util;

import org.apache.commons.lang3.Range;

public final class WAVAudioSupport {

    /**
     * .
     */
    public static final int SUPPORTED_SAMPLE_SIZE = 16;

    /**
     * .
     */
    public static final Range<Integer> SUPPORTED_CHANNELS_RANGE = Range.between(1, 6);

    /**
     * .
     */
    public static final int EOS = -1;

    private WAVAudioSupport() {
    }
}
