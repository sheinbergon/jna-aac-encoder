package org.sheinbergon.aac;

import org.sheinbergon.aac.jna.v015.FdkAAC;
import org.sheinbergon.aac.jna.v015.FdkAACFacade;
import org.sheinbergon.aac.jna.v015.structure.AACEncoder;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;

import java.util.Arrays;
import java.util.Objects;

public class Examples {
    public static void main(String[] args) {

        System.out.println("Library information...");
        System.out.println("########################");
        // Get Library info
        Arrays.stream(FdkAACFacade.getLibInfo())
                .filter(info -> Objects.nonNull(info.title))
                .forEach(info -> System.out.println("Lib Info - " + info));

        // Open encoder handle
        AACEncoder encoder = FdkAACFacade.openEncoder(0, 0);

        // Set some parameters (ChannelMode is mandatory!)
        System.out.println();
        System.out.println("Clean values :");
        System.out.println("##############");
        System.out.println("Sample Rate - " + FdkAACFacade.getEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE));
        System.out.println("Channel Mode - " + FdkAACFacade.getEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE));
        System.out.println("Bit Rate - " + FdkAACFacade.getEncoderParam(encoder, AACEncParam.AACENC_BITRATE));

        FdkAACFacade.setEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE, 44100);
        FdkAACFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE, 2);
        FdkAACFacade.setEncoderParam(encoder, AACEncParam.AACENC_BITRATE, 262144);

        System.out.println();
        System.out.println("Init Flags after initial settings - " + encoder.InitFlags);
        // Initialize encoder. Having the init-flags initiates encoder adaptations;
        FdkAACFacade.initEncoder(encoder);

        // Check parameters set post initialization
        System.out.println();
        System.out.println("After Set & Initialization :");
        System.out.println("###########################");
        System.out.println("Sample Rate - " + FdkAACFacade.getEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE));
        System.out.println("Channel Mode - " + FdkAACFacade.getEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE));
        System.out.println("Bit Rate - " + FdkAACFacade.getEncoderParam(encoder, AACEncParam.AACENC_BITRATE));


        FdkAACFacade.closeEncoder(encoder);
    }
}