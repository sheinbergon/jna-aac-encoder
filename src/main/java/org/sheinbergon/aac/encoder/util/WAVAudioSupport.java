package org.sheinbergon.aac.encoder.util;

import org.apache.commons.lang3.Range;

public final class WAVAudioSupport {

    /**
     * The only supported sample size for fdk-aac for wav audio input is 16 bits.
     */
    public static final int SUPPORTED_SAMPLE_SIZE = 16;

    /**
     * The maximum number of channels support by this bridge.
     */
    public static final Range<Integer> SUPPORTED_CHANNELS_RANGE = Range.between(1, 6);

    /**
     * EOS character (\u0001) is used to indicate end-of-input for WAV audio input.
     */
    public static final int EOS = -1;

    private WAVAudioSupport() {
    }
}
