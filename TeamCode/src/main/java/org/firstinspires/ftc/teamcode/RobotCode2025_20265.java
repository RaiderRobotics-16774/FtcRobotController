package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "RobotCode2025_20265 (Blocks to Java)")
public class RobotCode2025_20265 extends LinearOpMode {

    private DcMotor Front_Left;
    private DcMotor Back_Left;
    private DcMotor Right_Launcher_Motor;
    private DcMotor Front_Right;
    private DcMotor Back_Right;
    private Servo feedRamp;
    private CRServo feedServo;
    private DcMotor Left_Launcher_Motor;

    /**
     * This sample contains the bare minimum Blocks for any regular OpMode. The 3 blue
     * Comment Blocks show where to place Initialization code (runs once, after touching the
     * DS INIT button, and before touching the DS Start arrow), Run code (runs once, after
     * touching Start), and Loop code (runs repeatedly while the OpMode is active, namely not
     * Stopped).
     */
    @Override
    public void runOpMode() {
        float Vertical;
        float Horizontal;
        float Pivot;
        float Left_Trigger;
        float Right_Trigger;

        Front_Left = hardwareMap.get(DcMotor.class, "Front_Left");
        Back_Left = hardwareMap.get(DcMotor.class, "Back_Left");
        Right_Launcher_Motor = hardwareMap.get(DcMotor.class, "Right_Launcher_Motor");
        Front_Right = hardwareMap.get(DcMotor.class, "Front_Right");
        Back_Right = hardwareMap.get(DcMotor.class, "Back_Right");
        feedRamp = hardwareMap.get(Servo.class, "feedRamp");
        feedServo = hardwareMap.get(CRServo.class, "feedServo");
        Left_Launcher_Motor = hardwareMap.get(DcMotor.class, "Left_Launcher_Motor");

        // Put initialization blocks here.
        Front_Left.setDirection(DcMotor.Direction.REVERSE);
        Back_Left.setDirection(DcMotor.Direction.REVERSE);
        Right_Launcher_Motor.setDirection(DcMotor.Direction.REVERSE);
        waitForStart();
        if (opModeIsActive()) {
            // Put run blocks here.
            while (opModeIsActive()) {
                // Put loop blocks here.
                Vertical = gamepad1.left_stick_y;
                Horizontal = gamepad1.left_stick_x;
                Pivot = gamepad1.right_stick_x;
                Left_Trigger = gamepad2.left_trigger;
                Right_Trigger = gamepad2.right_trigger;
                Front_Right.setPower(-Pivot + (Vertical - Horizontal));
                Back_Right.setPower(-Pivot + Vertical + Horizontal);
                Front_Left.setPower(Pivot + Vertical + Horizontal);
                Back_Left.setPower(Pivot + (Vertical - Horizontal));
                Front_Left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                Front_Right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                Back_Left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                Back_Right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                if (gamepad2.a) {
                    feedRamp.setPosition(0.6);
                } else {
                    feedRamp.setPosition(0.25);
                }
                if (gamepad2.left_bumper) {
                    feedServo.setPower(1);
                    sleep(500);
                    feedServo.setPower(0);
                    sleep(1000);
                } else if (gamepad2.right_bumper) {
                    feedServo.setPower(-1);
                } else {
                    feedServo.setPower(0);
                }
                // Check if the Left_Trigger is pressed more than a tiny amount
                if (Left_Trigger > 0.1) {
                    // Your code to run when the trigger is pressed{
                    Left_Launcher_Motor.setPower(0.36);
                    Right_Launcher_Motor.setPower(0.36);
                } else if (Right_Trigger > 0.1) {
                    Left_Launcher_Motor.setPower(0.46);
                    Right_Launcher_Motor.setPower(0.46);
                } else {
                    Left_Launcher_Motor.setPower(0);
                    Right_Launcher_Motor.setPower(0);
                }
                telemetry.update();
            }
        }
    }
}