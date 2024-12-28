package org.sheinbergon.aac.jna.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Flags for aacDecoder_DecodeFrame().
 */
@Getter
@RequiredArgsConstructor
public enum AACDecodeFrameFlag {
    /**
     * Trigger the built-in error concealment module to generate a substitute signal for one lost frame.
     * New input data will not be considered.
     */
    AACDEC_CONCEAL(1),
    /**
     * Flush all filterbanks to get all delayed audio without having new input data.
     * Thus new input data will not be considered.
     */
    AACDEC_FLUSH(2),
    /**
     * Signal an input bit stream data discontinuity. 
     * Resync any internals as necessary.
     */
    AACDEC_INTR(4),
    /**
     * Clear all signal delay lines and history buffers.
     * CAUTION: This can cause discontinuities in the output signal.
     */
    AACDEC_CLRHIST(8);
    
    final int value;
}
