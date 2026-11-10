package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.nio.file.Path;


@Autonomous(name = "Coolest Pedro Pathing", group = "Pedro")
public class CoolestPedroPathing extends OpMode {

    private Follower follower;
    private Limelight3A limelight;

    private static final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose startPose = poseFactory.of(0, 0, 0 );
    private Pose CoolestEverPose = poseFactory.of(67,67,90);
    private final Pose NewCoolPose = poseFactory.of(0,0,0);

    private Path PathPathPath;

    private static final double DESIRED_DISTANCE = 12;


    @Override
    public void init() {

    }

    @Override
    public void loop() {

    }
}
