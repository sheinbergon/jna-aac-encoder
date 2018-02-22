package org.sheinbergon.aac;

import com.sun.jna.Native;
import org.sheinbergon.aac.jna.v015.FdkAAC;
import org.sheinbergon.aac.jna.v015.FdkAACFacade;
import org.sheinbergon.aac.jna.v015.structure.AACEncoder;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

public class Examples {

    public static void main(String[] args) throws Exception {
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
        FdkAACFacade.setEncoderParam(encoder, AACEncParam.AACENC_BITRATE, 131072);

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


        // Check parameters set post initialization
        System.out.println();
        System.out.println("Reading WAV file");
        System.out.println("###########################");
        System.out.flush();
        System.out.flush();
        // TODO - these functionality should be abstracted
        byte[] buffer = new byte[4096];
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File("/home/idans/Downloads/sample.wav"));
        int read = stream.read(buffer);
        System.out.println("Read " + read + " bytes, lets encode");
        FdkAACFacade.encode(encoder, read, buffer);
        System.out.println("Encoded");
        stream.close();
        FdkAACFacade.closeEncoder(encoder);
    }
}