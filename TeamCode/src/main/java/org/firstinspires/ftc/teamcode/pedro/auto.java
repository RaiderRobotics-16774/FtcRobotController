package org.firstinspires.ftc.teamcode.pedro;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class auto extends OpMode {
    private Follower follower;
    
    
    @Override
    public void init(){
        follower = Constants.create(hardwareMap);
    }
    
    @Override
    public void start() {
        
    }
    
    @Override
    public void loop() {
        
    }
    private final PoseFactory p = PoseFactory.degrees();

    private final Pose startPose = p.of(24, 24, 0);
    private final Pose park = p.of(48, 48, 90);

}