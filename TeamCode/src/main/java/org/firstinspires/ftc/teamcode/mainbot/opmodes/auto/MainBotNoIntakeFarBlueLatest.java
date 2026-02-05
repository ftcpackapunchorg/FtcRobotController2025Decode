package org.firstinspires.ftc.teamcode.mainbot.opmodes.auto;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotMecanumDrive;

@Autonomous(name = "MainBotAutoNoIntakeFarBlueLatest", group = "MainBot")
public class MainBotNoIntakeFarBlueLatest extends LinearOpMode {

    MainBotMecanumDrive drive;
    MainBotLaunchMechanism launcher;
    MainBotIntakeMechanism intake;

    private double targetVelocity;
    private double minVeliocity;

    int shotsToFire = 3; // The number of shots to fire in this auto.

    private AutonomousState autonomousState;

    public AutoLaunchState getAutoLaunchState() {
        return autoLaunchState;
    }

    private AutoLaunchState autoLaunchState;

    /*
     * When we control our launcher motor, we are using encoders. These allow the control system
     * to read the current speed of the motor and apply more or less power to keep it at a constant
     * velocity. Here we are setting the target, and minimum velocity that the launcher should run
     * at. The minimum velocity is a threshold for determining when to fire.
     */
    final double LAUNCHER_TARGET_VELOCITY = 800;
    final double LAUNCHER_MIN_VELOCITY = 780;
    final double LAUNCHER_REVERSE_VELOCITY = 150;

    final double LAUNCHER_NEAR_ZONE_TARGET_VELOCITY = 600;
    final double LAUNCHER_NEAR_ZONE_MIN_VELOCITY = 400;

    ElapsedTime feederTimer = new ElapsedTime();
    ElapsedTime autoFeederTimer = new ElapsedTime();

    final double FEED_TIME = 0.20;
    final double TIME_BETWEEN_SHOTS = 3;

    private ElapsedTime shotTimer = new ElapsedTime();

    private enum AutonomousState {
        LAUNCH,
        GO_TO_LAUNCH_POSITION,
        WAIT_FOR_LAUNCH,
        GO_TO_LEAVE_ZONE,
        COMPLETE
    }

    private enum AutoLaunchState {IDLE, PREPARE, LAUNCH}

    /* ---------------- POSES ---------------- */
    Pose2d startPose = new Pose2d(60, -12, Math.toRadians(180));

    Pose2d shootPose = new Pose2d(56, -12, Math.toRadians(25));

    Pose2d intake1  = new Pose2d(-11.8, -23, Math.toRadians(-90));
    Pose2d intake2 = new Pose2d(13, -54, Math.toRadians(-180));

    Pose2d intake3 = new Pose2d(34.5, 23, Math.toRadians(-90));

    @Override
    public void runOpMode() {

        drive = new MainBotMecanumDrive(hardwareMap, startPose);
        launcher = new MainBotLaunchMechanism(hardwareMap, telemetry, "Blue");
        intake = new MainBotIntakeMechanism(hardwareMap, telemetry);

        String LAUNCH_ZONE = "FAR_ZONE";

        autonomousState = AutonomousState.GO_TO_LAUNCH_POSITION;
        autoLaunchState = AutoLaunchState.IDLE;

        TrajectoryActionBuilder goToLaunchZone =  drive.actionBuilder(startPose)
                .strafeTo(new Vector2d(56, -12))
                .turn(Math.toRadians(25));

        TrajectoryActionBuilder goToLeaveZone = goToLaunchZone.endTrajectory().fresh()
                .strafeTo(new Vector2d(46, -27));

        waitForStart();
        if (isStopRequested()) return;

        while(opModeIsActive()) {

            switch (autonomousState){
                /*
                 * Since the first state of our auto is LAUNCH, this is the first "case" we encounter.
                 * This case is very simple. We call our .launch() function with "true" in the parameter.
                 * This "true" value informs our launch function that we'd like to start the process of
                 * firing a shot. We will call this function with a "false" in the next case. This
                 * "false" condition means that we are continuing to call the function every loop,
                 * allowing it to cycle through and continue the process of launching the first ball.
                 */
                case GO_TO_LAUNCH_POSITION:
                    Actions.runBlocking(goToLaunchZone.build());
                    autonomousState = AutonomousState.LAUNCH;
                    break;
                case LAUNCH:
                    launcher.launchForAuto(true, LAUNCH_ZONE, intake, telemetry);
                    autonomousState = AutonomousState.WAIT_FOR_LAUNCH;
                    break;
                case WAIT_FOR_LAUNCH:
                    /*
                     * A technique we leverage frequently in this code are functions which return a
                     * boolean. We are using this function in two ways. This function actually moves the
                     * motors and servos in a way that launches the ball, but it also "talks back" to
                     * our main loop by returning either "true" or "false". We've written it so that
                     * after the shot we requested has been fired, the function will return "true" for
                     * one cycle. Once the launch function returns "true", we proceed in the code, removing
                     * one from the shotsToFire variable. If shots remain, we move back to the LAUNCH
                     * state on our state machine. Otherwise, we reset the encoders on our drive motors
                     * and move onto the next state.
                     */
                    telemetry.addData("shotsToFire", shotsToFire);

                    if(launcher.launchForAuto(false, LAUNCH_ZONE, intake, telemetry)) {
                        shotsToFire -= 1;
                        if(shotsToFire > 0) {
                            autonomousState = AutonomousState.LAUNCH;
                        } else {
                            launcher.stopLauncher();
                            intake.stopIntake();
                            autonomousState = AutonomousState.GO_TO_LEAVE_ZONE;
                        }
                    }
                    break;

                case GO_TO_LEAVE_ZONE:
                    /*
                     * This is another function that returns a boolean. This time we return "true" if
                     * the robot has been within a tolerance of the target position for "holdSeconds."
                     * Once the function returns "true" we reset the encoders again and move on.
                     */
                    Actions.runBlocking(goToLeaveZone.build());
                    autonomousState = AutonomousState.COMPLETE;

                    break;
            }
        }

        /* =========================================================
         * MOVE TO SHOOT POSITION
         * ========================================================= */
//        Actions.runBlocking(
//            new SequentialAction(
//                    new ParallelAction(
//                    goToLaunchZone.build(),
//                    launcher.launchArtifactsAction(true, LAUNCH_ZONE, intake, telemetry)
//                    ),
//                    new SleepAction(5),
//            new ParallelAction(
//                    intake.stopIntakeAction(),
//                    goToLeaveZone.build()
//                )
//            )
//        );
//        intake.startIntake();
//        fireLauncher("NEAR_ZONE");
//        intake.stopIntake();

        /* =========================================================
         * PARK
         * ========================================================= */
//        Actions.runBlocking(goToLeaveZone
//                        .build()
//        );
    }

    /**
     * Launches one ball, when a shot is requested spins up the motor and once it is above a minimum
     * velocity, runs the feeder servos for the right amount of time to feed the next ball.
     *
     * @param shotRequested "true" if the user would like to fire a new shot, and "false" if a shot
     *                      has already been requested and we need to continue to move through the
     *                      state machine and launch the ball.
     * @return "true" for one cycle after a ball has been successfully launched, "false" otherwise.
     */
    public boolean launchForAuto(boolean shotRequested, String launchZone, MainBotIntakeMechanism intake, Telemetry telemetry) {

        telemetry.addData("autoLaunchState", autoLaunchState);
        telemetry.addData("shotRequested", shotRequested);
        telemetry.addData("launchZone", launchZone);
//        telemetry.update();

        switch (autoLaunchState) {
            case IDLE:
                if (shotRequested) {
                    autoLaunchState = AutoLaunchState.PREPARE;
                    shotTimer.reset();
                }
                break;
            case PREPARE:
                if ("FAR_ZONE".equals(launchZone)) {
                    targetVelocity = LAUNCHER_TARGET_VELOCITY;
                    minVeliocity = LAUNCHER_MIN_VELOCITY;
                } else if ("NEAR_ZONE".equals(launchZone)) {
                    targetVelocity = LAUNCHER_NEAR_ZONE_TARGET_VELOCITY;
                    minVeliocity = LAUNCHER_NEAR_ZONE_MIN_VELOCITY;
                }
                telemetry.addData("Target Velocity", targetVelocity);
                telemetry.addData("Min Velocity", minVeliocity);
//                telemetry.update();

                launcher.setTargetVelocity(targetVelocity);
                if (launcher.getVelocity() >= minVeliocity) {
                    telemetry.addData("Current Velocity", launcher.getVelocity());
//                    telemetry.update();
                    autoLaunchState = autoLaunchState.LAUNCH;

//                    feederMechanism.allowArtifact();
                }
                break;
            case LAUNCH:
                if (autoFeederTimer.seconds() > FEED_TIME) {
//                    feederMechanism.leftFeeder.setPower(0);
//                    feederMechanism.rightFeeder.setPower(0);

                    intake.startIntake();

                    if (shotTimer.seconds() > TIME_BETWEEN_SHOTS) {
//                    stopLauncher();
                        autoLaunchState = AutoLaunchState.IDLE;
                        telemetry.update();
                        return true;
                    }
                }
        }
        telemetry.update();
        return false;
    }

    /* =============================================================
     * LAUNCHER HELPER
     * ============================================================= */
    private void fireLauncher(String launzhZone) {
        launcher.launchForAuto(true, launzhZone, intake, telemetry);
        while (opModeIsActive() && !launcher.launchForAuto(false, launzhZone, intake, telemetry)) {
            idle();
        }
    }
}