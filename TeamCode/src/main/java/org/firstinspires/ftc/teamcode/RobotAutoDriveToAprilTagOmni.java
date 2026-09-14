/* Copyright (c) 2023 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

@TeleOp(name="Omni Drive To AprilTag Limelight", group = "Concept")
public class RobotAutoDriveToAprilTagOmni extends LinearOpMode
{
    // Adjust these numbers to suit your robot.
    final double DESIRED_DISTANCE = 12.0; 

    // Gains: Positive means "Proportional" correction.
    final double SPEED_GAIN  = -0.02;   
    final double STRAFE_GAIN = -0.015;  
    final double TURN_GAIN   = -0.01;   

    final double MAX_AUTO_SPEED = 0.5;   
    final double MAX_AUTO_STRAFE= 0.5;   
    final double MAX_AUTO_TURN  = 0.3;   

    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;

    private static final int DESIRED_TAG_ID = -1;     
    private Limelight3A limelight;

    // Class members to avoid local shadowing
    private double drive  = 0;
    private double strafe = 0;
    private double turn   = 0;

    @Override public void runOpMode()
    {
        limelight = hardwareMap.get(Limelight3A.class, "Limelight3a");
        limelight.setPollRateHz(100);
        limelight.start();
        limelight.pipelineSwitch(0);

        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeftDrive");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRightDrive");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData(">", "Touch START to start OpMode");
        telemetry.update();
        waitForStart();

        while (opModeIsActive())
        {
            boolean targetFound = false;
            LLResultTypes.FiducialResult desiredTag = null;
            double rangeError = 0;
            double headingError = 0;
            double yawError = 0;

            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                for (LLResultTypes.FiducialResult fr : fiducialResults) {
                    if ((DESIRED_TAG_ID < 0) || (fr.getFiducialId() == DESIRED_TAG_ID)) {
                        targetFound = true;
                        desiredTag = fr;
                        break;
                    }
                }
            }

            if (targetFound) {
                Pose3D pose = desiredTag.getTargetPoseCameraSpace();
                double ta = desiredTag.getTargetArea();
                double tx = desiredTag.getTargetXDegrees();
                
                boolean has3D = (pose != null && (Math.abs(pose.getPosition().x) > 0.001 || Math.abs(pose.getPosition().z) > 0.001));
                
                double range, bearing, yaw;
                if (has3D) {
                    range = Math.sqrt(Math.pow(pose.getPosition().x, 2) + Math.pow(pose.getPosition().y, 2) + Math.pow(pose.getPosition().z, 2)) * 39.37;
                    bearing = tx;
                    yaw = pose.getOrientation().getYaw();
                } else {
                    range = 25.0 / Math.sqrt(ta + 0.001); 
                    bearing = tx;
                    yaw = 0; 
                }

                rangeError = (range - DESIRED_DISTANCE);
                headingError = bearing;
                yawError = yaw;

                telemetry.addData("Vision", "ID:%d %s", desiredTag.getFiducialId(), has3D ? "(3D)" : "(2D Fallback)");
                telemetry.addData("Range", "%5.1f / Err:%5.1f", range, rangeError);
                telemetry.addData("Bearing", "%5.1f / Err:%5.1f", bearing, headingError);
                telemetry.addData("Yaw", "%5.1f / Err:%5.1f", yaw, yawError);

                if (gamepad1.left_bumper) {
                    // Use standard sign: Error * Gain
                    drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                    turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                    strafe = Range.clip(yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);
                } else {
                    driveManual();
                }
            } else {
                telemetry.addData("Vision", "No Target Found");
                driveManual();
            }

            telemetry.addData("Final Pwr", "D:%5.2f, S:%5.2f, T:%5.2f", drive, strafe, turn);
            moveRobot(drive, strafe, turn);
            telemetry.update();
            sleep(10);
        }
        limelight.stop();
    }

    private void driveManual() {
        drive  = -gamepad1.left_stick_y  / 2.0;
        strafe = -gamepad1.left_stick_x  / 2.0;
        turn   = -gamepad1.right_stick_x / 3.0;
    }

    public void moveRobot(double x, double y, double yaw) {
        // Standard Omni-Drive mapping (y = strafe left)
        double fl = x - y - yaw;
        double fr = x + y + yaw;
        double bl = x + y - yaw;
        double br = x - y + yaw;

        double max = Math.max(Math.abs(fl), Math.abs(fr));
        max = Math.max(max, Math.abs(bl));
        max = Math.max(max, Math.abs(br));

        if (max > 1.0) {
            fl /= max; fr /= max; bl /= max; br /= max;
        }

        frontLeftDrive.setPower(fl);
        frontRightDrive.setPower(fr);
        backLeftDrive.setPower(bl);
        backRightDrive.setPower(br);

        telemetry.addData("Motors", "FL:%4.2f FR:%4.2f", fl, fr);
        telemetry.addData("Motors", "BL:%4.2f BR:%4.2f", bl, br);
    }
}
