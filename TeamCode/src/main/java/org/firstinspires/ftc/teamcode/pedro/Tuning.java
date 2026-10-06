package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.PinpointTuner;

/**
 * Registers Pedro Pathing tuners with the web tuner.
 * Open http://192.168.43.1:10158 while connected to the Control Hub's Wi-Fi.
 *
 * Step 1: Mecanum Tuner  -> finds which drive motors need reversing
 * Step 2: Pinpoint Tuner -> finds pod directions and offsets for the goBILDA Pinpoint
 * Paste the code each tuner gives you into Constants.java.
 */
public class Tuning {
    @Tuner(name = "1. Mecanum Tuner")
    public static Procedure mecanum() {
        return new MecanumTuner();
    }

    @Tuner(name = "2. Pinpoint Tuner")
    public static Procedure pinpoint() {
        return new PinpointTuner();
    }
}