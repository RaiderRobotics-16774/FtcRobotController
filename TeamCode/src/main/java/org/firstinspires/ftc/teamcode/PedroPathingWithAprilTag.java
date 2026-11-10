package org.firstinspires.ftc.teamcode;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.List;

/**


 * Autonomous OpMode using Pedro Pathing to navigate to an AprilTag target
 * and execute a reaction to move 10 inches backward after a 1-second interval
 *
 *
 *
 */
@Autonomous(name = "Pedro Pathing With AprilTag", group = "Pedro")
public class PedroPathingWithAprilTag extends OpMode {

    private Follower follower;
    private Limelight3A limelight;

    private static final PoseFactory poseFactory = PoseFactory.degrees();

    // Default target poses (in inches and degrees)
    private final Pose startPose = poseFactory.of(0, 0, 0);
    private Pose aprilTagPose = poseFactory.of(72, 0, 0);
    private final Pose controlPose = poseFactory.of(0, 0, 0);

    private Path pathToAprilTag;

    // Desired stopping distance from AprilTag in inches
    private static final double DESIRED_DISTANCE = 12.0;

    // State Machine for Autonomous Routine & AprilTag Reaction
    private enum State {
        DRIVE_TO_TAG,
        WAIT_INTERVAL,
        BACKWARD_REACTION,
        DONE
    }

    private State state = State.DRIVE_TO_TAG;
    private final ElapsedTime waitTimer = new ElapsedTime();
    private boolean hasSeenAprilTag = false;

    @Override
    public void init() {
        // Initialize Pedro Pathing Follower using Constants configuration
        follower = Constants.create(hardwareMap);
        if (follower != null) {
            follower.setPose(startPose);
        } else {
            telemetry.addLine("Error: Follower initialization failed.");
        }

        // Build default trajectory to target AprilTag pose
        pathToAprilTag = getPathToAprilTag(startPose, aprilTagPose);

        // Try initializing Limelight camera if present
        try {
            limelight = hardwareMap.get(Limelight3A.class, "limelight");
            limelight.pipelineSwitch(0);
            limelight.start();
            telemetry.addLine("Limelight camera initialized.");
        } catch (Exception e) {
            limelight = null;
            telemetry.addLine("Limelight not found - using default preset AprilTag pose.");
        }

        telemetry.addLine("Init Complete. Ready to start.");
        telemetry.update();
    }

    @Override
    public void start() {
        if (follower == null) return;

        follower.setPose(startPose);
        state = State.DRIVE_TO_TAG;

        // Check if Limelight detects an AprilTag to update AprilTag target pose dynamically
        Pose detectedPose = detectAprilTagTargetPose();
        if (detectedPose != null) {
            hasSeenAprilTag = true;
            aprilTagPose = detectedPose;
            pathToAprilTag = getPathToAprilTag(startPose, aprilTagPose);
            telemetry.addData("AprilTag Detected at", "X: %.1f, Y: %.1f", aprilTagPose.x(), aprilTagPose.y());
        } else {
            telemetry.addLine("No AprilTag detected at start, using preset AprilTag pose.");
        }

        // Start following the generated path to the AprilTag
        follower.follow(pathToAprilTag);
    }

    @Override
    public void loop() {
        if (follower == null) return;

        // Must run every loop to update localizer and drive motor powers
        follower.update();

        switch (state) {
            case DRIVE_TO_TAG:
                // Check if Limelight detects AprilTag while driving if not detected at start
                Pose detected = detectAprilTagTargetPose();
                if (detected != null && !hasSeenAprilTag) {
                    hasSeenAprilTag = true;
                    aprilTagPose = detected;
                    follower.follow(getPathToAprilTag(follower.pose(), aprilTagPose));
                }

                if (follower.atParametricEnd()) {
                    telemetry.addLine("AprilTag Reached! Waiting 1 second interval...");
                    waitTimer.reset();
                    state = State.WAIT_INTERVAL;
                } else {
                    telemetry.addLine("Pathing to AprilTag...");
                }
                break;

            case WAIT_INTERVAL:
                telemetry.addData("Waiting interval...", "%.2f / 1.00s", waitTimer.seconds());
                if (waitTimer.seconds() >= 1.0) {
                    telemetry.addLine("1s interval elapsed! Executing 10-inch backward reaction...");
                    startBackwardReaction(10.0);
                    state = State.BACKWARD_REACTION;
                }
                break;

            case BACKWARD_REACTION:
                if (follower.atParametricEnd()) {
                    state = State.DONE;
                } else {
                    telemetry.addLine("Moving 12 inches forward...");
                }
                break;

            case DONE:
                telemetry.addLine("Reaction Complete! Robot moved 10 inches backward.");
                break;
        }

        Pose currentPose = follower.pose();
        telemetry.addData("State", state);
        telemetry.addData("Current X (in)", "%.1f", currentPose.x());
        telemetry.addData("Current Y (in)", "%.1f", currentPose.y());
        telemetry.addData("Current Heading (deg)", "%.1f", Math.toDegrees(currentPose.heading()));
        telemetry.update();
    }

    @Override
    public void stop() {
        if (follower != null) follower.stop();
        if (limelight != null) limelight.stop();
    }

    /**
     * Generates a straight line Pedro Pathing trajectory to an AprilTag target.
     */
    public Path getPathToAprilTag(Pose start, Pose target) {
        return line(start, target).linear(start.heading(), target.heading());
    }

    /**
     * Generates a curved Pedro Pathing trajectory using a control point to an AprilTag target.
     */
    public Path getCurvedPathToAprilTag(Pose start, Pose control, Pose target) {
        return curve(start, control, target).linear(start.heading(), target.heading());
    }

    /**
     * Generates a curved trajectory using default control pose.
     */
    public Path getCurvedPathToAprilTag(Pose target) {
        return getCurvedPathToAprilTag(startPose, controlPose, target);
    }

    /**
     * Reaction method: Computes a pose 10 inches backward from current position
     * along the robot's current heading and commands Pedro Pathing to follow it.
     *
     * @param distanceInches Distance to move backwards in inches (e.g. 10.0)
     */
    public void startBackwardReaction(double distanceInches) {
        Pose currentPose = follower.pose();
        double headingRad = currentPose.heading();

        // Calculate 10-inch backward offset in field coordinates based on current heading
        double backX = currentPose.x() - distanceInches * Math.cos(headingRad);
        double backY = currentPose.y() - distanceInches * Math.sin(headingRad);
        Pose backPose = new Pose(backX, backY, headingRad);

        Path backPath = line(currentPose, backPose).constant(headingRad);
        follower.follow(backPath);
    }

    /**
     * Detects AprilTag via Limelight and computes target pose in field coordinates.
     * Returns null if no tag is currently detected.
     */
    private Pose detectAprilTagTargetPose() {
        if (limelight == null) return null;

        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            if (!fiducials.isEmpty()) {
                LLResultTypes.FiducialResult tag = fiducials.get(0);
                Pose3D cameraSpacePose = tag.getTargetPoseCameraSpace();

                double rangeInches = cameraSpacePose.getPosition().toUnit(DistanceUnit.INCH).z - DESIRED_DISTANCE;
                double offsetInches = cameraSpacePose.getPosition().toUnit(DistanceUnit.INCH).x;
                double yawDegrees = cameraSpacePose.getOrientation().getYaw(AngleUnit.DEGREES);

                // Compute relative target pose offset from current start pose
                double targetX = startPose.x() + rangeInches;
                double targetY = startPose.y() - offsetInches;
                double targetHeadingDeg = Math.toDegrees(startPose.heading()) + yawDegrees;

                return poseFactory.of(targetX, targetY, targetHeadingDeg);
            }
        }
        return null;
    }
}
