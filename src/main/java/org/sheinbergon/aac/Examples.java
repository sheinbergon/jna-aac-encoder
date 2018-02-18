package org.sheinbergon.aac;

import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import org.sheinbergon.aac.jna.v015.FdkAAC;
import org.sheinbergon.aac.jna.v015.structure.AACEncoder;
import org.sheinbergon.aac.jna.v015.structure.LibInfo;
import org.sheinbergon.aac.jna.v015.util.AACEncError;
import org.sheinbergon.aac.jna.v015.util.AACEncParam;

import java.util.Arrays;
import java.util.Objects;

public class Examples {
    public static void main(String[] args) {

        // Get Library info
        Arrays.stream(FdkAAC.getLibInfo())
                .filter(info -> Objects.nonNull(info.title))
                .forEach(info -> System.out.println("Lib Info - " + info));

        // Open encoder handle
        AACEncoder encoder = FdkAAC.openEncoder(0, 0);

        // Test Set parameter functionality
        System.out.println("Before set - " + encoder.extParam.userSamplerate);
        FdkAAC.setEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE, 44100);
        int paramAfter = FdkAAC.getEncoderParam(encoder, AACEncParam.AACENC_SAMPLERATE);
        System.out.println("After set - " + encoder.extParam.userSamplerate);

        // Close encoder handle
        FdkAAC.closeEncoder(encoder);
    }
}