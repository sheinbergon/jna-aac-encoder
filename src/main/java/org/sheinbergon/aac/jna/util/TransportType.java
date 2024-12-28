package org.sheinbergon.aac.jna.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * Maps to TRANSPORT_TYPE.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACdec/include/aacdecoder_lib.h">fdk-aac/aacdecoder_lib.h</a>
 */
@Getter
@RequiredArgsConstructor
public enum TransportType {
  
  /**  Unknown format.             */
  TT_UNKNOWN(-1),
  /**  "as is" access units (packet based since there is obviously no sync layer)  */
  TT_MP4_RAW(0),
  /**  ADIF bitstream format.      */
  TT_MP4_ADIF(1),
  /**  ADTS bitstream format.      */
  TT_MP4_ADTS(2),

  /**  Audio Mux Elements with muxConfigPresent(1)  */
  TT_MP4_LATM_MCP1(6),
  /**  Audio Mux Elements with muxConfigPresent(0), out of band StreamMuxConfig  */
  TT_MP4_LATM_MCP0(7),

  /**  Audio Sync Stream.          */
  TT_MP4_LOAS(10),

  /**  Digital Radio Mondial (DRM30/DRM+) bitstream format.  */
  TT_DRM(12);
  

  private final int value;
}
