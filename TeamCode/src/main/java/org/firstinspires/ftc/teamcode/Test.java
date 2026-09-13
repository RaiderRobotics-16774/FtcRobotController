package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Bench;

@TeleOp
public class Test extends OpMode {
    Bench bench = new Bench();

    @Override
    public void init() {
        bench.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad2.a) {
            bench.setMotorSpeed(0.5);
        }
        else {
            bench.setMotorSpeed(0);
        }

        if (gamepad2.left_bumper) {
            bench.setServoRot(1.0);
        }
        else {
            bench.setServoRot(0);
        }
    }
}
