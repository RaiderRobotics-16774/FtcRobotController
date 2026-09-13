package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class VroomVroom {
    private DcMotor motor; // motor

    public void init(HardwareMap hwMap) {
        //touch sensor code

        //DC motor
        motor = hwMap.get(DcMotor.class, "silly");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setMotorSpeed(double speed) {
        // accepts values from -1.0 - 1.0
        motor.setPower(speed);
    }

}
