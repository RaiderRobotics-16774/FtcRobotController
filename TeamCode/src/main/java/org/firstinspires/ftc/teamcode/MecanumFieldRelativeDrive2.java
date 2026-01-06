/* Copyright (c) 2025 FIRST. All rights reserved.
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
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/*
 * This OpMode illustrates how to program your robot to drive field relative.  This means
 * that the robot drives the direction you push the joystick regardless of the current orientation
 * of the robot.
 *
 * This OpMode assumes that you have four mecanum wheels each on its own motor named:
 *   front_left_motor, front_right_motor, back_left_motor, back_right_motor
 *
 *   and that the left motors are flipped such that when they turn clockwise the wheel moves backwards
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 *
 */
@TeleOp(name = "Robot: Field Relative Mecanum Drive", group = "Robot")
//@Disabled
public class MecanumFieldRelativeDrive2 extends OpMode {
    // This declares the four motors needed
    DcMotor Front_Left;
    DcMotor Front_Right;
    DcMotor Back_Left;
    DcMotor Back_Right;

    // This declares the IMU needed to get the current direction the robot is facing
    IMU imu;

    @Override
    public void init() {
        Front_Left = hardwareMap.get(DcMotor.class, "Front_Left");
        Front_Right = hardwareMap.get(DcMotor.class, "Front_Right");
        Back_Left = hardwareMap.get(DcMotor.class, "Back_Left");
        Back_Right = hardwareMap.get(DcMotor.class, "Back_Right");

        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
        Back_Left.setDirection(DcMotor.Direction.REVERSE);
        Front_Left.setDirection(DcMotor.Direction.REVERSE);

        // This uses RUN_USING_ENCODER to be more accurate.   If you don't have the encoder
        // wires, you should remove these
//        Front_Left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        Front_Right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        Back_Left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        Back_Right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        Front_Left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Front_Right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Back_Left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Back_Right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        imu = hardwareMap.get(IMU.class, "imu");
        // This needs to be changed to match the orientation on your robot
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT;

        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
    }

    @Override
    public void loop() {
        telemetry.addLine("Press A to reset Yaw");
        telemetry.addLine("Hold left bumper to drive in robot relative");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");

        // If you press the A button, then you reset the Yaw to be zero from the way
        // the robot is currently pointing
        if (gamepad1.a) {
            imu.resetYaw();
        }
        // If you press the left bumper, you get a drive from the point of view of the robot
        // (much like driving an RC vehicle)
        if (gamepad1.left_bumper) {
            drive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        } else {
            driveFieldRelative(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        }
    }

//    //This routine drives the robot field relative
//    private void driveFieldRelative(double forward, double strafe, double rotate) {
//        // First, convert direction being asked to drive to polar coordinates
//        double theta = Math.atan2(forward, strafe);
//        double r = Math.hypot(strafe, forward);
//
//        // Second, rotate angle by the angle the robot is pointing
//        theta = AngleUnit.normalizeRadians(theta -
//                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
//
//        // Third, convert back to cartesian
//        double newForward = r * Math.sin(theta);
//        double newStrafe = r * Math.cos(theta);
//
//        // Finally, call the drive method with robot relative forward and strafe amounts
//        this.drive(newForward, newStrafe, rotate);
//    }

    // Field-centric transform (standard)
    private void driveFieldRelative(double forward, double right, double rotate) {
        double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate the joystick vector by -heading to convert field -> robot coordinates
        double rotRight   = right   * Math.cos(heading) - forward * Math.sin(heading);
        double rotForward = right   * Math.sin(heading) + forward * Math.cos(heading);

        drive(rotForward, rotRight, rotate);
    }


    // Thanks to FTC16072 for sharing this code!!
    public void drive(double forward, double right, double rotate) {
        // This calculates the power needed for each wheel based on the amount of forward,
        // strafe right, and rotate
        double Front_LeftPower = forward + right + rotate;
        double Front_RightPower = forward - right - rotate;
        double Back_RightPower = forward + right - rotate;
        double Back_LeftPower = forward - right + rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;  // make this slower for outreaches

        // This is needed to make sure we don't pass > 1.0 to any wheel
        // It allows us to keep all of the motors in proportion to what they should
        // be and not get clipped
        maxPower = Math.max(maxPower, Math.abs(Front_LeftPower));
        maxPower = Math.max(maxPower, Math.abs(Front_RightPower));
        maxPower = Math.max(maxPower, Math.abs(Back_RightPower));
        maxPower = Math.max(maxPower, Math.abs(Back_LeftPower));

        // We multiply by maxSpeed so that it can be set lower for outreaches
        // When a young child is driving the robot, we may not want to allow full
        // speed.
        Front_Left.setPower(maxSpeed * (Front_LeftPower / maxPower));
        Front_Right.setPower(maxSpeed * (Front_RightPower / maxPower));
        Back_Left.setPower(maxSpeed * (Back_LeftPower / maxPower));
        Back_Right.setPower(maxSpeed * (Back_RightPower / maxPower));

        // Put these telemetry lines inside loop() (near the end is fine)
        telemetry.addData("Enc FL", Front_Left.getCurrentPosition());
        telemetry.addData("Enc FR", Front_Right.getCurrentPosition());
        telemetry.addData("Enc BL", Back_Left.getCurrentPosition());
        telemetry.addData("Enc BR", Back_Right.getCurrentPosition());

// Optional: show the current run modes too (helps confirm encoder vs no-encoder)
        telemetry.addData("Mode FL", Front_Left.getMode());
        telemetry.addData("Mode FR", Front_Right.getMode());
        telemetry.addData("Mode BL", Back_Left.getMode());
        telemetry.addData("Mode BR", Back_Right.getMode());

        telemetry.update();

       // Telemetry.Item yawDeg = telemetry.addData("Yaw deg", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));


    }
}
