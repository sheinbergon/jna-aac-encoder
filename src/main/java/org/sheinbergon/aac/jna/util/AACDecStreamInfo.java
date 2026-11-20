package org.sheinbergon.aac.jna.util;

/**
 * The decoded-output related subset of the fdk-aac CStreamInfo struct.
 *
 * @param sampleRate the output (PCM) sample rate, in Hz
 * @param frameSize  the number of output samples per channel in the last decoded frame
 * @param channels   the number of output channels
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.3/libAACdec/include/aacdecoder_lib.h">fdk-aac/libAACdec/include/aacdecoder_lib.h</a>
 */
public record AACDecStreamInfo(int sampleRate, int frameSize, int channels) {
}
