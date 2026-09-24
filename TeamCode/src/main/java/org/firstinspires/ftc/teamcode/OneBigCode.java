package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.mechanisms.Bench;
import org.firstinspires.ftc.teamcode.mechanisms.BigWheel;
import org.firstinspires.ftc.teamcode.mechanisms.UpDownVroom;

@TeleOp(name = "One Big Code", group = "Robot")
public class OneBigCode extends OpMode {
    // Drivetrain motors
    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backLeftDrive;
    private DcMotor backRightDrive;

    // IMU for field-centric driving
    private IMU imu;

    // Mechanisms
    private final Bench bench = new Bench();
    private final UpDownVroom upDown = new UpDownVroom();
    private final BigWheel bigWheel = new BigWheel();

    @Override
    public void init() {
        // Initialize drivetrain motors
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeftDrive");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRightDrive");

        // Reverse left motors
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);

        // Set run mode
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Initialize IMU
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.FORWARD;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.RIGHT;

        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        // Initialize mechanisms
        bench.init(hardwareMap);
        upDown.init(hardwareMap);
        bigWheel.init(hardwareMap);
    }

    @Override
    public void loop() {
        // --- Drivetrain Logic (Gamepad 1) ---
        if (gamepad1.a) {
            imu.resetYaw();
        }

        double forward = -gamepad1.left_stick_y;
        double right = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        if (gamepad1.left_bumper) {
            drive(forward, right, rotate);
        } else {
            driveFieldRelative(forward, right, rotate);
        }

        // --- Mechanism Logic (Gamepad 2) ---

        // Bench Motor ("silly")
        if (gamepad2.a) {
            bench.setMotorSpeed(-1.0);
        } else {
            bench.setMotorSpeed(0);
        }

        // Bench Servo ("servo_rot")
        if (gamepad2.left_bumper) {
            bench.setServoRot(-1.0);
        } else {
            bench.setServoRot(0);
        }

        // UpDown Motor ("UpAndDown")
        if (gamepad2.left_trigger > 0.1) {
            upDown.setMotorSpeed(-0.3);
        } else if (gamepad2.right_trigger > 0.1) {
            upDown.setMotorSpeed(0.3);
        } else {
            upDown.setMotorSpeed(0);
        }

        // BigWheel Motor ("massive_wheel")
        if (gamepad2.b) {
            bigWheel.setMotorSpeed(0.67); // Increased power to 0.67
            telemetry.addData("BigWheel", "ON (0.67)");
        } else {
            bigWheel.setMotorSpeed(0);
            telemetry.addData("BigWheel", "OFF");
        }

        // --- Telemetry ---
        telemetry.addData("Status", "Running");
        telemetry.addData("Heading", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
        telemetry.addData("Encoders", "FL:%d FR:%d BL:%d BR:%d",
            frontLeftDrive.getCurrentPosition(), frontRightDrive.getCurrentPosition(),
            backLeftDrive.getCurrentPosition(), backRightDrive.getCurrentPosition());
        telemetry.update();
    }

    private void driveFieldRelative(double forward, double right, double rotate) {
        double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate the joystick vector by -heading to convert field -> robot coordinates
        double rotRight = right * Math.cos(heading) - forward * Math.sin(heading);
        double rotForward = right * Math.sin(heading) + forward * Math.cos(heading);

        drive(rotForward, rotRight, rotate);
    }

    public void drive(double forward, double right, double rotate) {
        double Front_LeftPower = forward + right + rotate;
        double Front_RightPower = forward - right - rotate;
        double Back_RightPower = forward + right - rotate;
        double Back_LeftPower = forward - right + rotate;

        double maxPower = 1.0;
        maxPower = Math.max(maxPower, Math.abs(Front_LeftPower));
        maxPower = Math.max(maxPower, Math.abs(Front_RightPower));
        maxPower = Math.max(maxPower, Math.abs(Back_RightPower));
        maxPower = Math.max(maxPower, Math.abs(Back_LeftPower));

        frontLeftDrive.setPower(Front_LeftPower / maxPower);
        frontRightDrive.setPower(Front_RightPower / maxPower);
        backLeftDrive.setPower(Back_LeftPower / maxPower);
        backRightDrive.setPower(Back_RightPower / maxPower);
    }
}
