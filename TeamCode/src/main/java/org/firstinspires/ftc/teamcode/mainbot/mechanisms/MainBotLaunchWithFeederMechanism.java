package org.firstinspires.ftc.teamcode.mainbot.mechanisms;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.mainbot.utils.MainBotConstants;

import java.util.List;

public final class MainBotLaunchWithFeederMechanism {

    public final DcMotorEx launcher;

    final double FEED_TIME_SECONDS = 0.20; //The feeder servos run this long when a shot is requested.

    /*
     * When we control our launcher motor, we are using encoders. These allow the control system
     * to read the current speed of the motor and apply more or less power to keep it at a constant
     * velocity. Here we are setting the target, and minimum velocity that the launcher should run
     * at. The minimum velocity is a threshold for determining when to fire.
     */
    final double LAUNCHER_TARGET_VELOCITY = 800;
    final double LAUNCHER_MIN_VELOCITY = 580;
    final double LAUNCHER_REVERSE_VELOCITY = 230;

    final double LAUNCHER_NEAR_ZONE_TARGET_VELOCITY = 600;
    final double LAUNCHER_NEAR_ZONE_MIN_VELOCITY = 400;

    final double LAUNCHER_STOP_VELOCITY = 0.0;

    private String autoLaunchZone = "FAR_ZONE";

    ElapsedTime feederTimer = new ElapsedTime();

    /** Auto related **/

    final double FEED_TIME = 0.20;
    final double TIME_BETWEEN_SHOTS = 0.2;
    final double REVERSE_ROTATION_TIME = 0.1;



    /*
     * TECH TIP: State Machines
     * We use a "state machine" to control our launcher motor and feeder servos in this program.
     * The first step of a state machine is creating an enum that captures the different "states"
     * that our code can be in.
     * The core advantage of a state machine is that it allows us to continue to loop through all
     * of our code while only running specific code when it's necessary. We can continuously check
     * what "State" our machine is in, run the associated code, and when we are done with that step
     * move on to the next state.
     * This enum is called the "LaunchState". It reflects the current condition of the shooter
     * motor and we move through the enum when the user asks our code to fire a shot.
     * It starts at idle, when the user requests a launch, we enter SPIN_UP where we get the
     * motor up to speed, once it meets a minimum speed then it starts and then ends the launch process.
     * We can use higher level code to cycle through these states. But this allows us to write
     * functions and autonomous routines in a way that avoids loops within loops, and "waits".
     */

    private enum LaunchState {
        IDLE,
        SPIN_UP,
        LAUNCH,
        LAUNCHING,
    }

    /*
     * Here we create three timers which we use in different parts of our code. Each of these is an
     * "object," so even though they are all an instance of ElapsedTime(), they count independently
     * from each other.
     */
    private ElapsedTime shotTimer = new ElapsedTime();
    private ElapsedTime autoFeederTimer = new ElapsedTime();
    private ElapsedTime reverseLaunchTimer = new ElapsedTime();

    private enum AutoLaunchState { IDLE, FIND_LAUNCH_ZONE, PREPARE, LAUNCH }

    private LaunchState launchState;

    public LaunchState getLaunchState() {
        return launchState;
    }

    public AutoLaunchState getAutoLaunchState() {
        return autoLaunchState;
    }

    private AutoLaunchState autoLaunchState;

    MainBotLaunchFeederMechanism feederMechanism;

    private double targetVelocity;
    private double minVeliocity;

    private MainBotLimeLightCamera mainBotLimeLightCamera;

    private boolean enableLimelight;

    public MainBotLaunchWithFeederMechanism(HardwareMap hardwareMap, Telemetry telemetry, String alliance) {

        launcher = hardwareMap.get(DcMotorEx.class, MainBotConstants.LAUNCHER_ONE_TO_ONE_RATIO_MOTOR_NAME);

        feederMechanism = new MainBotLaunchFeederMechanism(hardwareMap, telemetry);

        try {

            mainBotLimeLightCamera = new MainBotLimeLightCamera(hardwareMap, telemetry, alliance);

            enableLimelight = true;

        } catch (Exception e) {
            telemetry.addData("Initialization", "No Limelight detected");
            enableLimelight = false;
        }

        launchState = LaunchState.IDLE;

        autoLaunchState = AutoLaunchState.IDLE;

        launcher.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        launcher.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        /*
         * Here we set our launcher to the RUN_USING_ENCODER runmode.
         * If you notice that you have no control over the velocity of the motor, it just jumps
         * right to a number much higher than your set point, make sure that your encoders are plugged
         * into the port right beside the motor itself. And that the motors polarity is consistent
         * through any wiring.
         */
        launcher.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

//        launcher.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));

//        launcher.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(60.1060, 0, 0, 14.3960));


    }

    public void launch(boolean shotRequested, String launchZone) {
        switch (launchState) {
            case IDLE:
                if (shotRequested) {
                    launchState = LaunchState.SPIN_UP;
                }
                break;
            case SPIN_UP:
                if("FAR_ZONE".equals(launchZone)) {
                    targetVelocity = LAUNCHER_TARGET_VELOCITY;
                    minVeliocity = LAUNCHER_MIN_VELOCITY;
                } else if("NEAR_ZONE".equals(launchZone)) {
                    targetVelocity = LAUNCHER_NEAR_ZONE_TARGET_VELOCITY;
                    minVeliocity = LAUNCHER_NEAR_ZONE_MIN_VELOCITY;
                }
                launcher.setVelocity(targetVelocity);
                if (launcher.getVelocity() > minVeliocity) {
                    launchState = LaunchState.LAUNCHING;
//                    feederMechanism.allowArtifact();
                }
                break;
            case LAUNCH:
                launchState = LaunchState.LAUNCHING;
                break;
            case LAUNCHING:
//                if (feederTimer.seconds() > FEED_TIME_SECONDS) {
                    launchState = LaunchState.IDLE;
//                    feederMechanism.blockArtifact();
//                }
                break;
        }
    }

    /**
     * Launches one ball, when a shot is requested spins up the motor and once it is above a minimum
     * velocity, runs the feeder servos for the right amount of time to feed the next ball.
     * @param shotRequested "true" if the user would like to fire a new shot, and "false" if a shot
     *                      has already been requested and we need to continue to move through the
     *                      state machine and launch the ball.
     * @return "true" for one cycle after a ball has been successfully launched, "false" otherwise.
     */
    public boolean launchForAuto(boolean shotRequested, YawPitchRollAngles orientation, Telemetry telemetry){
        switch (autoLaunchState) {
            case IDLE:
                if (shotRequested) {
                    autoLaunchState = AutoLaunchState.PREPARE;
                    shotTimer.reset();
                }
                break;
            case FIND_LAUNCH_ZONE:

                if(enableLimelight) {

                    mainBotLimeLightCamera.limelight.updateRobotOrientation(orientation.getYaw());

                    LLResult llResult = mainBotLimeLightCamera.limelight.getLatestResult();

                    // Access fiducial results
                    List<LLResultTypes.FiducialResult> fiducialResults = llResult.getFiducialResults();

                    for (LLResultTypes.FiducialResult fiducial : fiducialResults) {
                        int id = fiducial.getFiducialId(); // The ID number of the fiducial
                        double x = fiducial.getTargetXDegrees(); // Where it is (left-right)
                        double y = fiducial.getTargetYDegrees(); // Where it is (up-down)
                        double StrafeDistance_3D = fiducial.getRobotPoseTargetSpace().getPosition().y;;
                        telemetry.addData("Fiducial " + id, "is " + StrafeDistance_3D + " meters away");

//            fiducial.getRobotPoseTargetSpace(); // Robot pose relative it the AprilTag Coordinate System (Most Useful)
//            fiducial.getCameraPoseTargetSpace(); // Camera pose relative to the AprilTag (useful)
//            fiducial.getRobotPoseFieldSpace(); // Robot pose in the field coordinate system based on this tag alone (useful)
//            fiducial.getTargetPoseCameraSpace(); // AprilTag pose in the camera's coordinate system (not very useful)
//            fiducial.getTargetPoseRobotSpace(); // AprilTag pose in the robot's coordinate system (not very useful)

//                        double currentPositionX = fiducial.getRobotPoseFieldSpace().getPosition().x;
//                        double currentPositionY = fiducial.getRobotPoseFieldSpace().getPosition().y;
//                        double currentPositionAngleInDegrees = fiducial.getRobotPoseFieldSpace().getOrientation().getYaw(AngleUnit.DEGREES);
//
//                        Pose2d currRobotPose = new Pose2d(currentPositionX, currentPositionY, Math.toRadians(currentPositionAngleInDegrees));
//
//                        telemetry.addData("Current Position X : ", currentPositionX);
//                        telemetry.addData("Current Position Y : ", currentPositionY);
//                        telemetry.addData("Current Position Angle In Degrees : ", currentPositionAngleInDegrees);
//                        telemetry.update();

                        double robotFromTargetPosePosX = fiducial.getRobotPoseTargetSpace().getPosition().x;
                        double robotFromTargetPosePosY = fiducial.getRobotPoseTargetSpace().getPosition().y;
                        double robotFromTargetPosePosAngleInDegrees = fiducial.getRobotPoseTargetSpace().getOrientation().getYaw(AngleUnit.DEGREES);

                        Pose2d robotFromTargetPose = new Pose2d(robotFromTargetPosePosX, robotFromTargetPosePosY, Math.toRadians(robotFromTargetPosePosAngleInDegrees));

                        telemetry.addData("Robot Position From Target X : ", robotFromTargetPosePosX);
                        telemetry.addData("Robot Position From Target Y : ", robotFromTargetPosePosY);
                        telemetry.addData("Robot Position From Target Angle In Degrees : ", robotFromTargetPosePosAngleInDegrees);
                        telemetry.update();

                        if(robotFromTargetPosePosX > 10000) {
                            autoLaunchZone = "FAR_ZONE";
                        } else {
                            autoLaunchZone = "NEAR_ZONE";
                        }
                    }

                    autoLaunchState = AutoLaunchState.PREPARE;
                } else {
                    autoLaunchState = AutoLaunchState.PREPARE;
                }
                break;
            case PREPARE:
                if("FAR_ZONE".equals(autoLaunchZone)) {
                    targetVelocity = LAUNCHER_TARGET_VELOCITY;
                    minVeliocity = LAUNCHER_MIN_VELOCITY;
                } else if("NEAR_ZONE".equals(autoLaunchZone)) {
                    targetVelocity = LAUNCHER_NEAR_ZONE_TARGET_VELOCITY;
                    minVeliocity = LAUNCHER_NEAR_ZONE_MIN_VELOCITY;
                }
                launcher.setVelocity(targetVelocity);
                if (launcher.getVelocity() > minVeliocity) {
                    autoLaunchState = AutoLaunchState.LAUNCH;
                    feederMechanism.allowArtifact();
                }
                break;
            case LAUNCH:
                if (autoFeederTimer.seconds() > FEED_TIME) {
                    blockArtifact();
                    if(shotTimer.seconds() > TIME_BETWEEN_SHOTS){
                        stopLauncher();
                        autoLaunchState = AutoLaunchState.IDLE;
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    public void startLauncher() {

//        launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
//        launcher.setPower(.7);

        launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        feederMechanism.allowArtifact();

    }

    public void startLauncherNearZone() {

        launcher.setVelocity(LAUNCHER_MIN_VELOCITY);
//        launcher.setPower(.7);
    }

    public void stopLauncher() {

        launcher.setVelocity(LAUNCHER_STOP_VELOCITY);
//        launcher.setPower(0);
    }

    public void reverseLauncher() {

        launcher.setVelocity(-1 * LAUNCHER_REVERSE_VELOCITY);
//        launcher.setPower(-1 * 0.1);
    }

    public void blockArtifact() {
        feederMechanism.blockArtifact();
    }

    public void allowArtifact() {
        feederMechanism.allowArtifact();
    }

    public void startLimeLightCamera() {
        if(enableLimelight) {
            mainBotLimeLightCamera.startLimeLightCamera();
        }
    }

    public void stopLimeLightCamera() {
        if(enableLimelight) {
            mainBotLimeLightCamera.stopLimeLightCamera();
        }
    }

    public boolean isArtifactAllowedToFlow(Telemetry telemetry) {
        return feederMechanism.isArtifactAllowedToFlow(telemetry);
    }
}
