package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Bench {
    private DcMotor motor;
    private CRServo servoRot;

    public void init(HardwareMap hwMap) {
        motor = hwMap.get(DcMotor.class, "silly");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        servoRot = hwMap.get(CRServo.class, "servo_rot");
    }

    public void setMotorSpeed(double speed) {
        if (motor != null) {
            motor.setPower(speed);
        }
    }

    public void setServoRot(double power) {
        if (servoRot != null) {
            servoRot.setPower(power);
        }
    }
}
