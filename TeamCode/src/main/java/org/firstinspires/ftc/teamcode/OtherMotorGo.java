package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.UpDownVroom;

@TeleOp
public class OtherMotorGo extends OpMode {

    UpDownVroom bench = new UpDownVroom();

    @Override
    public void init() {
        bench.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad2.left_trigger > 0.1) {
            bench.setMotorSpeed(-0.3);
        } else if (gamepad2.right_trigger > 0.1) {
            bench.setMotorSpeed(0.3);
        } else {
            bench.setMotorSpeed(0);
        }
    }
}
