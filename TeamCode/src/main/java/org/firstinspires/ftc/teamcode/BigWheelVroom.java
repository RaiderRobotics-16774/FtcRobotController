package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.BigWheel;

@TeleOp
public class BigWheelVroom extends OpMode {
    BigWheel bench = new BigWheel();

    @Override
    public void init() {
        bench.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad2.b) {
            bench.setMotorSpeed(-0.3);
        }
        else {
            bench.setMotorSpeed(0);
        }
    }
}
