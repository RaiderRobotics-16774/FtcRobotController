package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.internal.hardware.android.GpioPin;
import org.firstinspires.ftc.teamcode.mechanisms.ServoCodeThing;

@TeleOp
public class ServoCode extends OpMode {
    ServoCodeThing bench = new ServoCodeThing();
    @Override
    public void init(){
        bench.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.left_bumper) {
            bench.setServoRot(1.0);
        }
        else {
            bench.setServoRot(0);
        }
    }
}