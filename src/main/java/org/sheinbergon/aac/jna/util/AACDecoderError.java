package org.sheinbergon.aac.jna.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Maps to AAC_DECODER_ERROR.
 *
 * @see <a href="https://github.com/mstorsjo/fdk-aac/blob/v2.0.2/libAACdec/include/aacdecoder_lib.h">fdk-aac/aacdecoder_lib.h</a>
 */
@Getter
@RequiredArgsConstructor
public enum AACDecoderError implements AACError {
  
  /** No error occurred. Output buffer is valid and error free. */
  AAC_DEC_OK(0x0000),
  /** Heap returned NULL pointer. Output buffer is invalid. */
  AAC_DEC_OUT_OF_MEMORY(0x0002),
  /** Error condition is of unknown reason, or from a another module. Output buffer is invalid. */
  AAC_DEC_UNKNOWN(0x0005),
  
  
  // Synchronization errors. Output buffer is invalid.
  
  /** The transport decoder had synchronization problems. Do not exit decoding. Just feed new bitstream data. */
  AAC_DEC_TRANSPORT_SYNC_ERROR(0x1001),
  /**  The input buffer ran out of bits.  */
  AAC_DEC_NOT_ENOUGH_BITS(0x1002),

  
  // Initialization errors. Output buffer is invalid.
  
  /**  The handle passed to the function call was invalid (NULL).  */
  AAC_DEC_INVALID_HANDLE(0x2001),
  /**  The AOT found in the configuration is not supported.  */
  AAC_DEC_UNSUPPORTED_AOT(0x2002),
  /**  The bitstream format is not supported.   */
  AAC_DEC_UNSUPPORTED_FORMAT(0x2003),
  /**  The error resilience tool format is not supported.  */
  AAC_DEC_UNSUPPORTED_ER_FORMAT(0x2004),
  /**  The error protection format is not supported.  */
  AAC_DEC_UNSUPPORTED_EPCONFIG(0x2005),
  /**  More than one layer for AAC scalable is not supported.  */
  AAC_DEC_UNSUPPORTED_MULTILAYER(0x2006),
  /**  The channel configuration (either number or arrangement) is not supported.  */
  AAC_DEC_UNSUPPORTED_CHANNELCONFIG(0x2007),
  /**  The sample rate specified in the configuration is not supported.  */
  AAC_DEC_UNSUPPORTED_SAMPLINGRATE(0x2008),
  /**  The SBR configuration is not supported.  */
  AAC_DEC_INVALID_SBR_CONFIG(0x2009),
  /**  The parameter could not be set. Either the value was out of range or the parameter does not exist.  */
  AAC_DEC_SET_PARAM_FAIL(0x200A),
  /**  The decoder needs to be restarted, since the required configuration change cannot be performed.  */
  AAC_DEC_NEED_TO_RESTART(0x200B),
  /**  The provided output buffer is too small.  */
  AAC_DEC_OUTPUT_BUFFER_TOO_SMALL(0x200C),

  
  // Decode errors. Output buffer is valid but concealed.
  
  /**  The transport decoder encountered an unexpected error.  */
  AAC_DEC_TRANSPORT_ERROR(0x4001),
  /**  Error while parsing the bitstream. Most probably it is corrupted, or the system crashed.  */
  AAC_DEC_PARSE_ERROR(0x4002),
  /**  Error while parsing the extension payload of the bitstream. The extension payload type found is not supported.  */
  AAC_DEC_UNSUPPORTED_EXTENSION_PAYLOAD(0x4003),
  /**  The parsed bitstream value is out of range. Most probably the bitstream is corrupt, or the system crashed.  */
  AAC_DEC_DECODE_FRAME_ERROR(0x4004),
  /**  The embedded CRC did not match.  */
  AAC_DEC_CRC_ERROR(0x4005),
  /**  An invalid codebook was signaled. Most probably the bitstream is corrupt, or the system  crashed.  */
  AAC_DEC_INVALID_CODE_BOOK(0x4006),
  /**  Predictor found, but not supported in the AAC Low Complexity profile. Most probably the bitstream is corrupt, or has a wrong format.  */
  AAC_DEC_UNSUPPORTED_PREDICTION(0x4007),
  /**  A CCE element was found which is not supported. Most probably the bitstream is corrupt, or has a wrong format.  */
  AAC_DEC_UNSUPPORTED_CCE(0x4008),
  /**  A LFE element was found which is not supported. Most probably the bitstream is corrupt, or has a wrong format.  */
  AAC_DEC_UNSUPPORTED_LFE(0x4009),
  /**  Gain control data found but not supported. Most probably the bitstream is corrupt, or has a wrong format.  */
  AAC_DEC_UNSUPPORTED_GAIN_CONTROL_DATA(0x400A),
  /**  SBA found, but currently not supported in the BSAC profile.  */
  AAC_DEC_UNSUPPORTED_SBA(0x400B),
  /**  Error while reading TNS data. Most probably the bitstream is corrupt or the system crashed.  */
  AAC_DEC_TNS_READ_ERROR(0x400C),
  /**  Error while decoding error resilient data.  */
  AAC_DEC_RVLC_ERROR(0x400D),
  
  
  // Ancillary data errors. Output buffer is valid.
  
  /**  Non severe error concerning the ancillary data handling.  */
  AAC_DEC_ANC_DATA_ERROR(0x8001),
  /**  The registered ancillary data buffer is too small to receive the parsed data.  */
  AAC_DEC_TOO_SMALL_ANC_BUFFER(0x8002),
  /**  More than the allowed number of ancillary data elements should be written to buffer.  */
  AAC_DEC_TOO_MANY_ANC_ELEMENTS(0x8003);
  

  private static final Map<Integer, AACDecoderError> BY_CODE = Stream.of(values())
      .collect(Collectors.toMap(AACDecoderError::getValue, err -> err));

  /**
   * Match a {@link AACEncError} from a given numeric error code.
   *
   * @param value numeric library error code
   * @return the matched library error descriptor
   */
  public static AACDecoderError valueOf(final int value) {
    return BY_CODE.getOrDefault(value, AAC_DEC_UNKNOWN);
  }

  private final int value;
}
