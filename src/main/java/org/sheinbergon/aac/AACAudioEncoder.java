package org.sheinbergon.aac;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.sheinbergon.aac.jna.v015.FdkAACLibFacade;
import org.sheinbergon.aac.jna.v015.structure.AACEncInfo;
import org.sheinbergon.aac.jna.v015.structure.AACEncoder;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;
import org.sheinbergon.aac.util.AACAudioEncoderException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AACAudioEncoder implements AutoCloseable {

    private final static int WAV_INPUT_CHANNEL_ORDER = 1;
    private final static int MAX_ENCODER_CHANNELS = 0;
    private final static int ENCODER_MODULES_MASK = 0;

    private AACEncoder encoder;
    private AACEncInfo info;

    private final AACAudioEncoderParams params;

    // TODO - Move this to the builder and merge with AACAudioEncoderParams
    public synchronized void init() {
        encoder = FdkAACLibFacade.openEncoder(ENCODER_MODULES_MASK, MAX_ENCODER_CHANNELS);
        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AFTERBURNER, params.afterBurner());
        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE, params.sampleRate());
        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_BITRATE, params.bitRate());
        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AOT, params.aot());
        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELORDER, WAV_INPUT_CHANNEL_ORDER);
        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE, params.channelMode());
        info = FdkAACLibFacade.getEncoderInfo(encoder);
    }

    // TODO - implement this ;
    public AACAudioOutput encode(WAVAudioInput input) throws AACAudioEncoderException {
        return null ;
    }

    @Override
    public void close() throws Exception {
        FdkAACLibFacade.closeEncoder(encoder);
        encoder = null;
        info = null;
    }
}