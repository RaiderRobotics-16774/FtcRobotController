package org.firstinspires.ftc.teamcode.pedro;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Starter Pedro Pathing autonomous for the b-bot.
 *
 * What it does (all distances in inches, angles in degrees):
 *   1. Drives straight forward 24 in.
 *   2. Curves 24 in to the left while turning to face left (90 degrees).
 *   3. Waits 1 second (this is where a mechanism action would go).
 *   4. Drives straight back to where it started, facing forward again.
 *
 * Coordinates: the robot STARTS at (0, 0) facing 0 degrees.
 *   +X = forward (the way the robot faces at the start)
 *   +Y = to the robot's left
 *   Heading 90 = facing left. Pedro wants radians, so we use Math.toRadians().
 *
 * How it works: a "state machine". The robot is always in one step (state).
 * Every loop we call follower.update() so Pedro keeps driving, then check
 * whether the current step is finished. When it is, we start the next path
 * and move to the next state.
 *
 * Before running: Constants.create() must return a real Follower. Give the
 * robot about 4 ft of clear floor ahead and to the left.
 */
@Autonomous(name = "Starter Auto (Pedro)", group = "Pedro")
public class StarterAuto extends OpMode {

    // ---- Poses: where the robot should be at each point. Change these to make new paths. ----
    private final Pose startPose   = new Pose(0, 0, Math.toRadians(0));
    private final Pose forwardPose = new Pose(24, 0, Math.toRadians(0));
    private final Pose curveEndPose = new Pose(48, 24, Math.toRadians(90));

    // A curve also needs a "control point". The robot does not drive through it;
    // it pulls the curve toward itself, like a magnet. Move it to change the shape.
    private final Pose curveControl = new Pose(48, 0);

    // ---- Paths: built from the poses above in buildPaths(). ----
    private Path driveForward;
    private Path curveLeft;
    private Path returnHome;

    // ---- The steps of our autonomous, in order. ----
    private enum State {
        DRIVE_FORWARD,
        CURVE_LEFT,
        WAIT,
        RETURN_HOME,
        DONE
    }

    private State state = State.DRIVE_FORWARD;
    private Follower follower;
    private final ElapsedTime stateTimer = new ElapsedTime();

    private void buildPaths() {
        // Straight line, heading stays at 0 (facing forward the whole time).
        driveForward = line(startPose, forwardPose).constant(startPose.heading());

        // Curve from forwardPose to curveEndPose, bending toward curveControl.
        // linear() turns the robot smoothly from the start heading to the end heading.
        curveLeft = curve(forwardPose, curveControl, curveEndPose)
                .linear(forwardPose.heading(), curveEndPose.heading());

        // Straight line back to the start, turning back to face forward on the way.
        returnHome = line(curveEndPose, startPose)
                .linear(curveEndPose.heading(), startPose.heading());
    }

    /** Switches to a new step and restarts the step timer. */
    private void setState(State newState) {
        state = newState;
        stateTimer.reset();
    }

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        if (follower == null) {
            telemetry.addLine("Constants.create() returned null.");
            telemetry.addLine("Paste the tuner code into pedro/Constants.java first.");
            telemetry.update();
            return;
        }
        follower.setPose(startPose);
        buildPaths();
        telemetry.addLine("Ready. Robot at (0, 0) facing forward with clear floor ahead.");
        telemetry.update();
    }

    @Override
    public void start() {
        if (follower == null) {
            requestOpModeStop();
            return;
        }
        // Tell Pedro where the robot is right now, then start the first path.
        follower.setPose(startPose);
        follower.follow(driveForward);
        setState(State.DRIVE_FORWARD);
    }

    @Override
    public void loop() {
        if (follower == null) return;

        // Must run every loop: reads odometry and sets the drive motor powers.
        follower.update();

        switch (state) {
            case DRIVE_FORWARD:
                // atParametricEnd() = the robot has reached the end of the current path.
                if (follower.atParametricEnd()) {
                    follower.follow(curveLeft);
                    setState(State.CURVE_LEFT);
                }
                break;

            case CURVE_LEFT:
                if (follower.atParametricEnd()) {
                    // Pedro holds the robot at the end of a path on its own while we wait.
                    setState(State.WAIT);
                }
                break;

            case WAIT:
                // Later: run the intake or shooter here instead of just waiting.
                if (stateTimer.seconds() > 1.0) {
                    follower.follow(returnHome);
                    setState(State.RETURN_HOME);
                }
                break;

            case RETURN_HOME:
                if (follower.atParametricEnd()) {
                    setState(State.DONE);
                }
                break;

            case DONE:
                // Nothing left to do. Pedro keeps holding the final position until Stop.
                break;
        }

        Pose pose = follower.pose();
        telemetry.addData("State", state);
        telemetry.addData("X (in)", "%.1f", pose.x());
        telemetry.addData("Y (in)", "%.1f", pose.y());
        telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(pose.heading()));
        telemetry.update();
    }

    @Override
    public void stop() {
        if (follower != null) follower.stop();
    }
}
