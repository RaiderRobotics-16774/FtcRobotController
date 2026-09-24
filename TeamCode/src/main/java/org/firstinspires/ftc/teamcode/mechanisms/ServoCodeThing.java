package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;



public class ServoCodeThing {
    private CRServo servoRot;



    public void init(HardwareMap hwMap) {
        servoRot = hwMap.get(CRServo.class, "servo_rot");
    }


    public void setServoRot(double power){
        servoRot.setPower(power);

    }
}
