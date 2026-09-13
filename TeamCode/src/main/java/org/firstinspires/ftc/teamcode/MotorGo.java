package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.VroomVroom;

@TeleOp
public class MotorGo extends OpMode {
    VroomVroom bench = new VroomVroom();

    @Override
    public void init() {
        bench.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            bench.setMotorSpeed(-1.0);
        }
        else {
            bench.setMotorSpeed(0);
        }
    }
}