package org.sheinbergon.aac;

import com.sun.jna.Native;
import org.sheinbergon.aac.jna.v015.FdkAAC;
import org.sheinbergon.aac.jna.v015.FdkAACFacade;
import org.sheinbergon.aac.jna.v015.structure.AACEncoder;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileOutputStream;
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
        FdkAACFacade.setEncoderParam(encoder, AACEncParam.AACENC_BITRATE, 64000);
        FdkAACFacade.setEncoderParam(encoder, AACEncParam.AACENC_AFTERBURNER, 1);
        FdkAACFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELORDER, 1);
        FdkAACFacade.setEncoderParam(encoder, AACEncParam.AACENC_AOT, 2);

        System.out.println();
        System.out.println("Init Flags after initial settings - " + encoder.InitFlags);
        // Initialize encoder. Having the init-flags initiates encoder adaptations;
        FdkAACFacade.initEncoder(encoder);

        // This needs to be sized according to the number of channels
        byte[] buffer = new byte[4096];
        AudioInputStream wav = AudioSystem.getAudioInputStream(new File("/home/idans/Downloads/sample.wav"));
        FileOutputStream aac = new FileOutputStream("/tmp/test.aac");
        int read = 0;
        while ((read = wav.read(buffer)) != -1) {
            byte[] encoded = FdkAACFacade.encode(encoder, read, buffer, false);
            aac.write(encoded);
        }
        byte[] encoded = FdkAACFacade.encode(encoder, read, buffer, true);
        aac.write(encoded);
        aac.flush();
        aac.close();
        wav.close();
        FdkAACFacade.closeEncoder(encoder);
    }
}