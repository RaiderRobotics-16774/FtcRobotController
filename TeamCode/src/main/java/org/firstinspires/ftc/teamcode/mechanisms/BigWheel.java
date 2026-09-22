package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class BigWheel {

    private DcMotor motor;

    public void init(HardwareMap hwMap) {
        // Find the motor in the hardware configuration
        motor = hwMap.get(DcMotor.class, "massive_wheel");
        
        // Use RUN_WITHOUT_ENCODER in case the encoder cable is not plugged in
        if (motor != null) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    public void setMotorSpeed(double speed) {
        if (motor != null) {
            motor.setPower(speed);
        }
    }
}
