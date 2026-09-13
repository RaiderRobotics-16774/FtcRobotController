package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

public class PossibleScannerForInfoPoints {
}
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Teleop(name="Limelight Test", group = "Sensor")
public class LimelightTest extends OpMode {
}

private Limelight3A limelight;

@Override
public void init() {

    limelight = hardwareMap.get(Limelight3A.class), "limelight"
    limelight.setPollRateHz(100);
    limelight.start();
    limelight.pipelineSwitch(0)
    telemetry.addData("Status", "Limelight Initialized");
}

@Override
public void loop() {

    LLResult result = limelight.getLatestResult();
}
if (result != null && result.isValid)){
//Read tag coords and data
double tx = result.getTargetX(); //Horizontal offset from crosshair
double ty = result.getTargetY();//Vertical offset from crosshair
double ta = result.getTargetA();//Target area (0% to 100% of image)
telemetry.

addData("Target Found","True");
telemetry.

addData("Target X (tx)",tx);
telemetry.

addData("Target Y (ty)",ty);
telemetry.

addData("Target Area (ta)",ta);
} else {
    telemetry.addData("Target Found", "False");
        }

        telemetry.update();
    }
}