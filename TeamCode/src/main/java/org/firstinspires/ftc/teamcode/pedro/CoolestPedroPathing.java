package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.pedropathing.utils.Timer;

import java.nio.file.Path;


@Autonomous(name = "Coolest Pedro Pathing", group = "Pedro")
public class CoolestPedroPathing extends OpMode {

    private Follower follower;
    private Limelight3A limelight;
    private Timer pathTimer, opModeTimer;
    public enum PathState {
        //START POSITION_END POSITION
        //DRIVE > MOVEMENT STATE
        //SHOOT > ATTEMPT TO SCORE THE ARTIFACT
        DRIVE_STARTPOS_SHOOTPOS,
        SHOOT_PRELOAD
    }
    PathState pathState;

    private final Pose startPose = new Pose(131.6, 33.6, 90);
    private final Pose shootPose = new Pose(85.5,106.3,90);
    private PoseFactory driveStartShoot;

    public void buildPaths() {
        // put in coordinates for starting pose > ending pose

    }


    @Override
    public void init(){

    }

    @Override
    public void loop(){

    }
}
