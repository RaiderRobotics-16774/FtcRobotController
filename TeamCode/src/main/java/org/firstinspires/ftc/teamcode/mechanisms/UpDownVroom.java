package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class UpDownVroom {

    private DcMotor motor;

    public void init(HardwareMap hwMap) {
        try {
            motor = hwMap.get(DcMotor.class, "UpAndDown");
            if (motor != null) {
                motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        } catch (Exception ignored) {
            motor = null;
        }
    }

    public void setMotorSpeed(double speed) {
        if (motor != null) {
            motor.setPower(speed);
        }
    }

}
