package org.firstinspires.ftc.teamcode.newbot.opmodes.auto;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.newbot.mechanisms.NewBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.newbot.mechanisms.NewBotLaunchWithStopperMechanism;
import org.firstinspires.ftc.teamcode.newbot.mechanisms.NewBotMecanumDrive;

@Autonomous(name = "NewBotAutoNearLaunchAndLeave", group = "NewBot")
public class NewBotAutoNearLaunchAndLeave extends LinearOpMode {

    NewBotMecanumDrive drive;
    NewBotLaunchWithStopperMechanism launcher;
    NewBotIntakeMechanism intake;

    int shotsToFire = 3; // The number of shots to fire in this auto.

    private AutonomousState autonomousState;

    private AutoLaunchState autoLaunchState;

    private enum AutonomousState {
        LAUNCH,
        GO_TO_LAUNCH_POSITION,
        WAIT_FOR_LAUNCH,
        GO_TO_LEAVE_ZONE,
        COMPLETE
    }

    private enum AutoLaunchState {IDLE, PREPARE, LAUNCH}

    /* ---------------- POSES ---------------- */
    Pose2d startPose = new Pose2d(-49, -48, Math.toRadians(-135));
    Pose2d shootPose = new Pose2d(-34, -34, Math.toRadians(-135));

    Pose2d intake1  = new Pose2d(-11.8, -23, Math.toRadians(-90));
    Pose2d intake2 = new Pose2d(13, -54, Math.toRadians(-180));

    Pose2d intake3 = new Pose2d(34.5, 23, Math.toRadians(-90));

    @Override
    public void runOpMode() {

        drive = new NewBotMecanumDrive(hardwareMap, startPose);
        launcher = new NewBotLaunchWithStopperMechanism(hardwareMap, telemetry, "Blue");
        intake = new NewBotIntakeMechanism(hardwareMap, telemetry);

        String LAUNCH_ZONE = "NEAR_ZONE";

        autonomousState = AutonomousState.GO_TO_LAUNCH_POSITION;
        autoLaunchState = AutoLaunchState.IDLE;

        TrajectoryActionBuilder goToLaunchZone = drive.actionBuilder(startPose)
                .strafeTo(new Vector2d(-24, -24));

        TrajectoryActionBuilder goToLeaveZone = goToLaunchZone.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(-20, -48), Math.toRadians(-180));


        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {

            switch (autonomousState) {
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
                    launcher.launchForAuto(true, LAUNCH_ZONE, intake, drive, telemetry);
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

                    if (launcher.launchForAuto(false, LAUNCH_ZONE, intake, drive, telemetry)) {
                        shotsToFire -= 1;
                        if (shotsToFire > 0) {
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
    }
}