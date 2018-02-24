package org.sheinbergon.aac;

import org.sheinbergon.aac.jna.v015.FdkAACLibFacade;
import org.sheinbergon.aac.jna.v015.structure.AACEncoder;

import java.io.Closeable;
import java.io.IOException;

public class FdkAACEncoder implements Closeable {

    private final static int MAX_ENCODER_CHANNELS = 0;
    private final static int ENCODER_MODULES_MASK = 0;

    private AACEncoder encoder;

    public synchronized void init() {
        encoder = FdkAACLibFacade.openEncoder(ENCODER_MODULES_MASK, MAX_ENCODER_CHANNELS);
    }

    public synchronized void destroy() {
        FdkAACLibFacade.closeEncoder(encoder);
        encoder = null;
    }


    @Override
    public void close() throws IOException {
        destroy();
    }
}
