package org.sheinbergon.aac;

import org.sheinbergon.aac.jna.v015.FdkAAC;
import org.sheinbergon.aac.jna.v015.structure.AACEncoder;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;

import java.util.Arrays;
import java.util.Objects;

public class Examples {
    public static void main(String[] args) {

        System.out.println("Library information...");
        System.out.println("########################");
        // Get Library info
        Arrays.stream(FdkAAC.getLibInfo())
                .filter(info -> Objects.nonNull(info.title))
                .forEach(info -> System.out.println("Lib Info - " + info));

        // Open encoder handle
        AACEncoder encoder = FdkAAC.openEncoder(0, 0);

        // Set some parameters (ChannelMode is mandatory!)
        System.out.println();
        System.out.println("Clean values :");
        System.out.println("##############");
        System.out.println("Sample Rate - " + FdkAAC.getEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE));
        System.out.println("Channel Mode - " + FdkAAC.getEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE));
        System.out.println("Bit Rate - " + FdkAAC.getEncoderParam(encoder, AACEncParam.AACENC_BITRATE));

        FdkAAC.setEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE, 44100);
        FdkAAC.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE, 2);
        FdkAAC.setEncoderParam(encoder, AACEncParam.AACENC_BITRATE, 262144);

        System.out.println();
        System.out.println("Init Flags after initial settings - " + encoder.InitFlags);
        // Initialize encoder. Having the init-flags initiates encoder adaptations;
        FdkAAC.initEncoder(encoder);

        // Check parameters set post initialization
        System.out.println();
        System.out.println("After Set & Initialization :");
        System.out.println("###########################");
        System.out.println("Sample Rate - " + FdkAAC.getEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE));
        System.out.println("Channel Mode - " + FdkAAC.getEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE));
        System.out.println("Bit Rate - " + FdkAAC.getEncoderParam(encoder, AACEncParam.AACENC_BITRATE));


        FdkAAC.closeEncoder(encoder);
    }
}