package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Bench {
    private DcMotor motor;
    private CRServo servoRot;

    // servoFeed: works whether it is set up in the robot configuration as a
    // regular "Servo" (moves to a position) or a "Continuous Rotation Servo" (spins).
    private Servo servoFeed;
    private CRServo servoFeedCR;

    // ---- servoFeed settings: change these numbers to tune it ----
    // Regular servo: positions run from 0.0 to 1.0
    public static final double FEED_REST_POSITION = 0.0;   // where it sits when X is NOT held
    public static final double FEED_PUSH_POSITION = 0.5;   // where it goes while X IS held
    // Continuous rotation servo: power runs from -1.0 to 1.0
    public static final double FEED_CR_POWER = 1.0;        // spin power while X is held (use -1.0 to reverse)

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

        // Try regular servo first, then continuous rotation servo
        try {
            servoFeed = hwMap.get(Servo.class, "servoFeed");
        } catch (Exception ignored) {
            servoFeed = null;
        }
        if (servoFeed == null) {
            try {
                servoFeedCR = hwMap.get(CRServo.class, "servoFeed");
            } catch (Exception ignored) {
                servoFeedCR = null;
            }
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

    /** true = feed (push position / spin), false = rest (rest position / stop). */
    public void setServoFeed(boolean feeding) {
        if (servoFeed != null) {
            servoFeed.setPosition(feeding ? FEED_PUSH_POSITION : FEED_REST_POSITION);
        } else if (servoFeedCR != null) {
            servoFeedCR.setPower(feeding ? FEED_CR_POWER : 0);
        }
    }

    /** For telemetry: tells you whether servoFeed was found in the configuration. */
    public String servoFeedStatus() {
        if (servoFeed != null) return "Servo";
        if (servoFeedCR != null) return "CR Servo";
        return "NOT FOUND (add servoFeed to config)";
    }
}
