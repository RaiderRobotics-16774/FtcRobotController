package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Bench {
    private DcMotor motor;
    private CRServo servoRot;

    // servoFeed (config name "servo_feed"): works whether it is set up in the robot configuration as a
    // regular "Servo" (moves to a position) or a "Continuous Rotation Servo" (spins).
    private Servo servoFeed;
    private CRServo servoFeedCR;

    // ---- servoFeed settings: change these numbers to tune it ----
    // servo_feed is a continuous-rotation servo set up in the config as a regular "Servo".
    // For that setup, 0.5 = STOP, 1.0 = full speed one way, 0.0 = full speed the other way.
    // (It used to rest at 0.0, which made it spin all the time and stop only while X was held.)
    public static final double FEED_REST_POSITION = 0.5;   // X NOT held: stopped
    public static final double FEED_PUSH_POSITION = 1.0;   // X held: spin (use 0.0 to reverse)
    // Continuous rotation servo: power runs from -1.0 to 1.0
    public static final double FEED_CR_POWER = 1.0;        // spin power while X is held (use -1.0 to reverse)

    // Names to look for in the robot configuration (first match wins)
    private static final String[] FEED_NAMES = {"servo_feed", "servoFeed"};

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

        // Try regular servo first, then continuous rotation servo.
        // The config name is "servo_feed"; "servoFeed" is still accepted as a backup.
        for (String name : FEED_NAMES) {
            try {
                servoFeed = hwMap.get(Servo.class, name);
                if (servoFeed != null) break;
            } catch (Exception ignored) {
                servoFeed = null;
            }
        }
        if (servoFeed == null) {
            for (String name : FEED_NAMES) {
                try {
                    servoFeedCR = hwMap.get(CRServo.class, name);
                    if (servoFeedCR != null) break;
                } catch (Exception ignored) {
                    servoFeedCR = null;
                }
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
        return "NOT FOUND (add servo_feed to config)";
    }
}
