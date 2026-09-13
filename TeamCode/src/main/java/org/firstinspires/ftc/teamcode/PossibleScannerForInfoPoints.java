package org.firstinspires.ftc.teamcode;

import static com.qualcomm.hardware.limelightvision.LLResultTypes.*;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

/*
 * This OpMode illustrates using a Limelight 3A to locate and drive towards an AprilTag.
 * The code assumes a Holonomic (Mecanum or X Drive) Robot.
 */
@TeleOp(name="Limelight Omni Drive", group = "Concept")
public class PossibleScannerForInfoPoints extends LinearOpMode {

    // Adjust these numbers to suit your robot.
    final double DESIRED_DISTANCE = 12.0; //  this is how close the camera should get to the target (inches)

    //  Set the GAIN constants to control the relationship between the measured position error, and how much power is
    //  applied to the drive motors to correct the error.
    final double SPEED_GAIN  =  0.02  ;   //  Forward Speed Control "Gain".
    final double STRAFE_GAIN =  0.015 ;   //  Strafe Speed Control "Gain".
    final double TURN_GAIN   =  0.01  ;   //  Turn Control "Gain".

    final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value
    final double MAX_AUTO_STRAFE= 0.5;   //  Clip the strafing speed to this max value
    final double MAX_AUTO_TURN  = 0.3;   //  Clip the turn speed to this max value

    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;

    private Limelight3A limelight;
    private static final int DESIRED_TAG_ID = -1;     // Choose the tag you want to approach or set to -1 for ANY tag.
    @Override public void runOpMode() {

        // Initialize the hardware variables.
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeftDrive");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRightDrive");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.start();
        limelight.pipelineSwitch(0);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            boolean targetFound = false;
            double drive = 0;
            double strafe = 0;
            double turn = 0;
            FiducialResult desiredTag = null;

            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                List<FiducialResult> fiducialResults = result.getFiducialResults();
                for (FiducialResult fr : fiducialResults) {
                    if (DESIRED_TAG_ID < 0 || fr.getFiducialId() == DESIRED_TAG_ID) {
                        targetFound = true;
                        desiredTag = fr;
                        break;
                    }
                }
            }

            if (targetFound) {
                telemetry.addData("\n>", "HOLD Left-Bumper to Drive to Target\n");
                telemetry.addData("Found", "ID %d", desiredTag.getFiducialId());
                
                // Get Pose in Camera Space
                Pose3D pose = desiredTag.getTargetPoseCameraSpace();
                // Limelight usually reports Pose3D in meters.
                double rangeMeters = Math.sqrt(Math.pow(pose.getPosition().x, 2) + Math.pow(pose.getPosition().y, 2) + Math.pow(pose.getPosition().z, 2));
                double range = DistanceUnit.METER.toInches(rangeMeters);
                
                double bearing = desiredTag.getTargetXDegrees();
                double yaw = pose.getOrientation().getYaw(AngleUnit.DEGREES);

                telemetry.addData("Range",  "%5.1f inches", range);
                telemetry.addData("Bearing","%3.0f degrees", bearing);
                telemetry.addData("Yaw","%3.0f degrees", yaw);

                if (gamepad1.left_bumper) {
                    double rangeError = (range - DESIRED_DISTANCE);
                    double headingError = bearing;
                    double yawError = yaw;

                    drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                    turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                    strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);
                }
            } else {
                telemetry.addData("\n>", "Drive using joysticks to find valid target\n");
            }

            if (!gamepad1.left_bumper || !targetFound) {
                drive  = -gamepad1.left_stick_y  / 2.0;
                strafe = -gamepad1.left_stick_x  / 2.0;
                turn   = -gamepad1.right_stick_x / 3.0;
            }

            moveRobot(drive, strafe, turn);
            telemetry.update();
        }
    }

    public void moveRobot(double x, double y, double yaw) {
        double frontLeftPower    =  x - y - yaw;
        double frontRightPower   =  x + y + yaw;
        double backLeftPower     =  x + y - yaw;
        double backRightPower    =  x - y + yaw;

        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
    }
}
