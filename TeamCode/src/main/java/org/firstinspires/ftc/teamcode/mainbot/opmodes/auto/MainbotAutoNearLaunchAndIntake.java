package org.firstinspires.ftc.teamcode.mainbot.opmodes.auto;

import com.acmerobotics.roadrunner.AccelConstraint;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ProfileAccelConstraint;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.VelConstraint;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotMecanumDrive;

import java.util.Arrays;

@Autonomous(name = "MainbotAutoNearLaunchAndIntake", group = "MainBot")
public class MainbotAutoNearLaunchAndIntake extends LinearOpMode {

    MainBotMecanumDrive drive;
    MainBotLaunchMechanism launcher;
    MainBotIntakeMechanism intake;

    int shotsToFire = 2; // The number of shots to fire in this auto.
    int maxShotsToFire = 2;

    int noOfIntakePaths = 3;
    int maxNoOfIntakePaths = 3;

    private AutonomousState autonomousState;

    private double STOP_AUTO_MODE_FOR_LEAVE = 30;

    ElapsedTime autonomousTimer = new ElapsedTime();

    ElapsedTime autoFeederTimer = new ElapsedTime();
    ElapsedTime autoIntakeTimer = new ElapsedTime();

    final double FEED_TIME = 0.20;
    final double TIME_BETWEEN_SHOTS = 3;

    private enum AutonomousState {
        LAUNCH,
        GO_TO_LAUNCH_POSITION,
        WAIT_FOR_LAUNCH,
        INTAKE_ARTIFACTS,
        START_INTAKE,
        GO_TO_INTAKE_POS1,
        GO_TO_LEAVE_ZONE,
        COMPLETE
    }


    /* ---------------- POSES ---------------- */
    Pose2d startPose = new Pose2d(-49, -48, Math.toRadians(-135));

    Pose2d nearLaunchPose = new Pose2d(-24, -24, Math.toRadians(-135));

    Pose2d nearLeavePose = new Pose2d(-20, -48, Math.toRadians(-180));

    Pose2d intake1  = new Pose2d(-11.8, -23, Math.toRadians(-90));

    Pose2d intake2 = new Pose2d(13, -54, Math.toRadians(-180));

    Pose2d intake3 = new Pose2d(34.5, 23, Math.toRadians(-90));

    @Override
    public void runOpMode() {

        drive = new MainBotMecanumDrive(hardwareMap, startPose);
        launcher = new MainBotLaunchMechanism(hardwareMap, telemetry, "Blue");
        intake = new MainBotIntakeMechanism(hardwareMap, telemetry);

        String LAUNCH_ZONE = "NEAR_ZONE";

        autonomousState = AutonomousState.GO_TO_LAUNCH_POSITION;

        VelConstraint baseVelConstraint = new MinVelConstraint(Arrays.asList(
                new TranslationalVelConstraint(50.0),
                new AngularVelConstraint(Math.PI / 2)
        ));
        AccelConstraint baseAccelConstraint = new ProfileAccelConstraint(-10.0, 25.0);

        TrajectoryActionBuilder goToLaunchZone =  drive.actionBuilder(startPose)
                .strafeToLinearHeading(new Vector2d(nearLaunchPose.position.x, nearLaunchPose.position.y), nearLaunchPose.heading);

        TrajectoryActionBuilder goToLeaveZone = goToLaunchZone.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(nearLeavePose.position.x, nearLeavePose.position.y), nearLeavePose.heading);

        TrajectoryActionBuilder goToIntakePos1 = goToLaunchZone.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(-11.8, -23), Math.toRadians(-90), new TranslationalVelConstraint(20.0))
                .strafeToLinearHeading(new Vector2d(-11.8, -40), Math.toRadians(-90), new TranslationalVelConstraint(20.0));

        TrajectoryActionBuilder goToLaunchZone2 = goToLaunchZone.endTrajectory().fresh()
                .strafeTo(new Vector2d(-24,-27));

        waitForStart();
        autonomousTimer.reset();
        if (isStopRequested()) return;

        while(opModeIsActive()) {

            if(autonomousTimer.seconds() >= STOP_AUTO_MODE_FOR_LEAVE) {
                launcher.stopLauncher();
                intake.stopIntake();
                Actions.runBlocking(goToLeaveZone.build());
                autonomousState = AutonomousState.COMPLETE;
            }
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
                            if(shotsToFire < maxShotsToFire) {
                                Actions.runBlocking(goToLaunchZone2.build());
                            }
                            autonomousState = AutonomousState.LAUNCH;
                        } else {
                            Actions.runBlocking(
                                    new ParallelAction(
                                            launcher.reverseLauncherAction(false, LAUNCH_ZONE, intake, telemetry),
                                            intake.stopIntakeAction()
                                    )
                            );
                            autonomousState = AutonomousState.START_INTAKE;
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
                case START_INTAKE:
                    intake.startIntake();
                    autonomousState = AutonomousState.GO_TO_INTAKE_POS1;
                    break;
                case GO_TO_INTAKE_POS1:
                    Actions.runBlocking(goToIntakePos1.build());
                    autonomousState = AutonomousState.GO_TO_LAUNCH_POSITION;
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