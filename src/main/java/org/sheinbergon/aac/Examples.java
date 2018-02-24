package org.sheinbergon.aac;

import org.sheinbergon.aac.jna.v015.FdkAACLibFacade;
import org.sheinbergon.aac.jna.v015.structure.AACEncInfo;
import org.sheinbergon.aac.jna.v015.structure.AACEncoder;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Objects;

public class Examples {

    public static void main(String[] args) throws Exception {
        System.out.println("Library information...");
        System.out.println("########################");

        // Get Library info
        Arrays.stream(FdkAACLibFacade.getLibInfo())
                .filter(info -> Objects.nonNull(info.title))
                .forEach(info -> System.out.println("Lib Info - " + info));

        // Open encoder handle
        AACEncoder encoder = FdkAACLibFacade.openEncoder(0, 0);

        // Set some parameters (ChannelMode is mandatory!)
        System.out.println();
        System.out.println("Clean values :");
        System.out.println("##############");
        System.out.println("Sample Rate - " + FdkAACLibFacade.getEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE));
        System.out.println("Channel Mode - " + FdkAACLibFacade.getEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE));
        System.out.println("Bit Rate - " + FdkAACLibFacade.getEncoderParam(encoder, AACEncParam.AACENC_BITRATE));

        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE, 44100);
        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELMODE, 2);
        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_BITRATE, 64000);
        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AFTERBURNER, 1);
        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_CHANNELORDER, 1);
        FdkAACLibFacade.setEncoderParam(encoder, AACEncParam.AACENC_AOT, 2);

        System.out.println();
        System.out.println("Init Flags after initial settings - " + encoder.InitFlags);
        // Initialize encoder. Having the init-flags initiates encoder adaptations;
        FdkAACLibFacade.initEncoder(encoder);


        System.out.println();
        System.out.println("Acquiring encoder info");
        AACEncInfo info = FdkAACLibFacade.getEncoderInfo(encoder);

        AudioInputStream wav = AudioSystem.getAudioInputStream(new File("/home/idans/Downloads/sample.wav"));
        FileOutputStream aac = new FileOutputStream("/tmp/test.aac");
        byte[] buffer = new byte[info.frameLength * 2 * wav.getFormat().getChannels()];

        int read = 0;
        while ((read = wav.read(buffer)) != -1) {
            byte[] encoded = FdkAACLibFacade.encode(encoder, read, buffer, false);
            aac.write(encoded);
        }
        byte[] encoded = FdkAACLibFacade.encode(encoder, read, buffer, true);
        aac.write(encoded);
        aac.flush();

        aac.close();
        wav.close();
        FdkAACLibFacade.closeEncoder(encoder);
    }
}