package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class VariablePractice extends OpMode {
    @Override
    public void init() {
        int teamNumber = 16774;
        double motorSpeed = 0.75;
        boolean clawClosed = true;
        String teamName = "Raider Robotics";
        int motorAngle = 90;

        telemetry.addData("Team Number", teamNumber);
        telemetry.addData("motor speed", motorSpeed);
        telemetry.addData("claw closed", clawClosed);
        telemetry.addData("teamName", teamName);
        telemetry.addData("motor angle", motorAngle);
    }

    @Override
    public void loop() {
        /*
        1. change the String variable name to team name
         */

    }
}
