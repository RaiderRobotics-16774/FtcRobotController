package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Bench {
    private DcMotor motor;
    private CRServo servoRot;

    public void init(HardwareMap hwMap) {
        try {
            motor = hwMap.get(DcMotor.class, "silly");
            if (motor != null) {
                motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        } catch (Exception ignored) {
            motor = null;
        }

        try {
            servoRot = hwMap.get(CRServo.class, "servo_rot");
        } catch (Exception ignored) {
            servoRot = null;
        }
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
