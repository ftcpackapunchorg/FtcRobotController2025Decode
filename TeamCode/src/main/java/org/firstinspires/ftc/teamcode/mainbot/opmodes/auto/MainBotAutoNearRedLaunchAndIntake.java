package org.firstinspires.ftc.teamcode.mainbot.opmodes.auto;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchWithFeederMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotMecanumDrive;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotRGBLightIndicator;
import org.firstinspires.ftc.teamcode.mainbot.utils.MainBotConstants;

@Autonomous(name = "Red Near", group = "MainBot")
public class MainBotAutoNearRedLaunchAndIntake extends LinearOpMode {

    MainBotMecanumDrive drive;
    MainBotLaunchWithFeederMechanism launcher;
    MainBotIntakeMechanism intake;
    MainBotRGBLightIndicator artifactIntakeIndicator, isArtifactAllowedIndicator;
    int shotsToFire = 2; // The number of shots to fire in this auto.
    int maxShotsToFire = 2;

    int currNoOfIntakePaths = 0;
    int maxNoOfIntakePaths = 1;

    private AutonomousState autonomousState;

    private double STOP_AUTO_MODE_FOR_LEAVE = 30;

    ElapsedTime autonomousTimer = new ElapsedTime();

    ElapsedTime autoFeederTimer = new ElapsedTime();
    ElapsedTime autoIntakeTimer = new ElapsedTime();

    final double FEED_TIME = 0.20;
    final double TIME_BETWEEN_SHOTS = 3;

    private enum AutonomousState {
        INIT,
        LAUNCH,
        GO_TO_LAUNCH_POSITION,
        WAIT_FOR_LAUNCH,
        INTAKE_ARTIFACTS,
        INTAKE,
        GO_TO_INTAKE_POS,
        GO_TO_LEAVE_ZONE,
        COMPLETE
    }


    /* ---------------- POSES ---------------- */
    Pose2d startPose = new Pose2d(-49, 48, Math.toRadians(135));

    Pose2d nearLaunchPose = new Pose2d(-24, 24, Math.toRadians(-45));

    Pose2d nearLeavePose = new Pose2d(-20, 48, Math.toRadians(180));

    Pose2d intake1  = new Pose2d(-12, 23, Math.toRadians(90));

    Pose2d intake2 = new Pose2d(13, 54, Math.toRadians(-180));

    Pose2d intake3 = new Pose2d(34.5, -23, Math.toRadians(-90));

    @Override
    public void runOpMode() {

        drive = new MainBotMecanumDrive(hardwareMap, startPose);
        launcher = new MainBotLaunchWithFeederMechanism(hardwareMap, telemetry, "Blue");
        intake = new MainBotIntakeMechanism(hardwareMap, telemetry);

        String LAUNCH_ZONE = "NEAR_ZONE";

        autonomousState = AutonomousState.INIT;

//        VelConstraint baseVelConstraint = new MinVelConstraint(Arrays.asList(
//                new TranslationalVelConstraint(50.0),
//                new AngularVelConstraint(Math.PI / 2)
//        ));
//        AccelConstraint baseAccelConstraint = new ProfileAccelConstraint(-10.0, 25.0);
//
//        TrajectoryActionBuilder goToLaunchZone = drive.actionBuilder(startPose)
//                .strafeToLinearHeading(new Vector2d(nearLaunchPose.position.x, nearLaunchPose.position.y), nearLaunchPose.heading);
//
//        TrajectoryActionBuilder goToLeaveZone = goToLaunchZone.endTrajectory().fresh()
//                .strafeToLinearHeading(new Vector2d(nearLeavePose.position.x, nearLeavePose.position.y), nearLeavePose.heading);
//
//        TrajectoryActionBuilder goToIntakePos1 = goToLaunchZone.endTrajectory().fresh()
//                .strafeToLinearHeading(new Vector2d(-11.8, -23), Math.toRadians(-90), new TranslationalVelConstraint(20.0))
//                .strafeToLinearHeading(new Vector2d(-11.8, -40), Math.toRadians(-90), new TranslationalVelConstraint(20.0));
//
//        TrajectoryActionBuilder goToIntakePos2 = goToLaunchZone.endTrajectory().fresh()
//                .strafeToLinearHeading(new Vector2d(-11.8, -23), Math.toRadians(-90), new TranslationalVelConstraint(20.0))
//                .strafeToLinearHeading(new Vector2d(-11.8, -40), Math.toRadians(-90), new TranslationalVelConstraint(20.0));
//
//        TrajectoryActionBuilder goToIntakePos3 = goToLaunchZone.endTrajectory().fresh()
//                .strafeToLinearHeading(new Vector2d(-11.8, -23), Math.toRadians(-90), new TranslationalVelConstraint(20.0))
//                .strafeToLinearHeading(new Vector2d(-11.8, -40), Math.toRadians(-90), new TranslationalVelConstraint(20.0));
//
//        TrajectoryActionBuilder goToLaunchZone2 = goToLaunchZone.endTrajectory().fresh()
//                .strafeTo(new Vector2d(-24,-27));

        TrajectoryActionBuilder currentIntakeTrajectory;
        TrajectoryActionBuilder currentTrajectory = null;
        TrajectoryActionBuilder newTrajectory = null;
        isArtifactAllowedIndicator = new MainBotRGBLightIndicator();
        isArtifactAllowedIndicator.init(hardwareMap, MainBotConstants.ALLOW_ARTIFACT_SERVO_INDICATOR);


        waitForStart();
        autonomousTimer.reset();
        if (isStopRequested()) return;

        while(opModeIsActive()) {

            if(autonomousTimer.seconds() >= STOP_AUTO_MODE_FOR_LEAVE) {
                telemetry.addData("Autonomous timer reached. Now, go to leave position", autonomousTimer.seconds());
                launcher.stopLauncher();
                intake.stopIntake();
//                Actions.runBlocking(goToLeaveZone.build());
                Actions.runBlocking(newTrajectory.build());
                autonomousState = AutonomousState.COMPLETE;
            }
            switch (autonomousState){
                case INIT:
                    autonomousState = AutonomousState.GO_TO_LAUNCH_POSITION;
                    break;
                /*
                 * Since the first state of our auto is LAUNCH, this is the first "case" we encounter.
                 * This case is very simple. We call our .launch() function with "true" in the parameter.
                 * This "true" value informs our launch function that we'd like to start the process of
                 * firing a shot. We will call this function with a "false" in the next case. This
                 * "false" condition means that we are continuing to call the function every loop,
                 * allowing it to cycle through and continue the process of launching the first ball.
                 */
                case GO_TO_LAUNCH_POSITION:
                    newTrajectory = getTrajectoryActionBuilderForLaunchZone(startPose, currentTrajectory, newTrajectory);
//                    Actions.runBlocking(goToLaunchZone.build());
                    Actions.runBlocking(newTrajectory.build());
                    currentTrajectory = newTrajectory;
                    autonomousState = AutonomousState.LAUNCH;
                    break;
                case LAUNCH:
                    launcher.launchForAuto(true, LAUNCH_ZONE, intake, isArtifactAllowedIndicator, drive, telemetry);
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

                    if(launcher.launchForAuto(false, LAUNCH_ZONE, intake, isArtifactAllowedIndicator, drive, telemetry)) {
                        shotsToFire -= 1;
                        if(shotsToFire > 0) {
                            currentTrajectory.endTrajectory().fresh()
//                                    .waitSeconds(1)
                                    .strafeToLinearHeading(new Vector2d(nearLaunchPose.position.x, nearLaunchPose.position.y-4), nearLaunchPose.heading);

                            autonomousState = AutonomousState.LAUNCH;
                        } else {

                            Actions.runBlocking(
                                    new ParallelAction(
                                            launcher.reverseLauncherAction(false, LAUNCH_ZONE, intake, drive, telemetry),
                                            intake.stopIntakeAction()
                                    )
                            );
                            autonomousState = AutonomousState.INTAKE;
                        }
                    }
                    break;

                case INTAKE:
//                    launcher.blockArtifact();
                    isArtifactAllowedIndicator.setRGBLightToRed();
                    intake.startIntake();
                    autonomousState = AutonomousState.GO_TO_INTAKE_POS;
                    break;

                case GO_TO_INTAKE_POS:
                    // This line has to be here before calling the trajectory action
                    currNoOfIntakePaths++;
                    telemetry.addData("currNoOfIntakePaths", currNoOfIntakePaths);

                    if(currNoOfIntakePaths <= maxNoOfIntakePaths) {
//                        currentIntakeTrajectory = getTrajectoryActionBuilderForIntake(null, currNoOfIntakePaths);
                        newTrajectory = getTrajectoryActionBuilderForIntake(startPose, currNoOfIntakePaths, currentTrajectory, newTrajectory);
                        Actions.runBlocking(newTrajectory.build());
                        currentTrajectory = newTrajectory;
                        shotsToFire = maxShotsToFire;
                        intake.stopIntake();
                        launcher.startLauncher();
                        autonomousState = AutonomousState.GO_TO_LAUNCH_POSITION;
                    } else {

                        autonomousState = AutonomousState.GO_TO_LEAVE_ZONE;
                    }

                    break;
                case GO_TO_LEAVE_ZONE:
                    launcher.stopLauncher();
                    intake.stopIntake();
                    /*
                     * This is another function that returns a boolean. This time we return "true" if
                     * the robot has been within a tolerance of the target position for "holdSeconds."
                     * Once the function returns "true" we reset the encoders again and move on.
                     */
                    currentTrajectory = newTrajectory;
//                    Actions.runBlocking(goToLeaveZone.build());
                    newTrajectory = getTrajectoryActionBuilderForLeaveZone(startPose, currentTrajectory, newTrajectory);
                    Actions.runBlocking(newTrajectory.build());
                    autonomousState = AutonomousState.COMPLETE;
                    break;
            }
        }
    }


    private TrajectoryActionBuilder getTrajectoryActionBuilderForLaunchZone(Pose2d currentPose, TrajectoryActionBuilder currTrajectory, TrajectoryActionBuilder toBeTrajectory) {

        if(currTrajectory == null) {

            telemetry.addData("Current trajectory is null", "True");

            return drive.actionBuilder(startPose)
                    .strafeToLinearHeading(new Vector2d(nearLaunchPose.position.x, nearLaunchPose.position.y), nearLaunchPose.heading)
                    ;
        } else {

            telemetry.addData("Current trajectory is null", "False");

            return currTrajectory.endTrajectory().fresh()
                    .strafeToLinearHeading(new Vector2d(nearLaunchPose.position.x, nearLaunchPose.position.y), nearLaunchPose.heading)
                    ;
        }

    }

    private TrajectoryActionBuilder getTrajectoryActionBuilderForIntake(Pose2d currentPose, int intakeNo, TrajectoryActionBuilder currTrajectory, TrajectoryActionBuilder toBeTrajectory) {

        TrajectoryActionBuilder intakeTrajectory = null;
        double xIntakeInch = 0.0;
        double yIntakeInch = 0.0;
        double headingRadiansIntake = 0.0;

        switch (intakeNo) {
            case 1:
//                intakeTrajectory = drive.actionBuilder(currentPose)
//                        .strafeToLinearHeading(new Vector2d(intake1.position.x, intake1.position.y), intake1.heading)
//                        .waitSeconds(.5)
//                        .strafeToLinearHeading(new Vector2d(intake1.position.x, (intake1.position.y + 5)), intake1.heading)
//                        .waitSeconds(.5)
//                        .strafeToLinearHeading(new Vector2d(intake1.position.x, (intake1.position.y + 10)), intake1.heading);
                xIntakeInch = intake1.position.x;
                yIntakeInch = intake1.position.y;
                headingRadiansIntake = intake1.heading.toDouble();

                intakeTrajectory = currTrajectory.endTrajectory().fresh()
                        .strafeToLinearHeading(new Vector2d(-12, 23), Math.toRadians(90), new TranslationalVelConstraint(20.0))
                        .waitSeconds(0.5)
                        .strafeToLinearHeading(new Vector2d(-12, 46), Math.toRadians(90), new TranslationalVelConstraint(20.0))
                        .waitSeconds(0.5)
                        .strafeToLinearHeading(new Vector2d(-12, 51),Math.toRadians(90), new TranslationalVelConstraint(20.0));

                break;
            case 2:
//                intakeTrajectory = drive.actionBuilder(currentPose)
//                        .strafeToLinearHeading(new Vector2d(intake2.position.x, intake2.position.y), intake2.heading);
                xIntakeInch = intake1.position.x + 24;
                yIntakeInch = intake1.position.y;
                headingRadiansIntake = intake1.heading.toDouble();

                intakeTrajectory = currTrajectory.endTrajectory().fresh()
                        .strafeToLinearHeading(new Vector2d(12, 23), Math.toRadians(90), new TranslationalVelConstraint(20.0))
                        .waitSeconds(0.5)
                        .strafeToLinearHeading(new Vector2d(12, 46), Math.toRadians(90), new TranslationalVelConstraint(20.0))
                        .waitSeconds(0.5)
                        .strafeToLinearHeading(new Vector2d(12, 51),Math.toRadians(90), new TranslationalVelConstraint(20.0));

                break;
            case 3:
//                intakeTrajectory = drive.actionBuilder(currentPose)
//                        .strafeToLinearHeading(new Vector2d(intake3.position.x, intake3.position.y), intake3.heading);
                xIntakeInch = intake1.position.x + 48;
                yIntakeInch = intake1.position.y;
                headingRadiansIntake = intake1.heading.toDouble();
                break;
        }

//        intakeTrajectory = currTrajectory.endTrajectory().fresh()
//                .strafeToLinearHeading(new Vector2d(xIntakeInch, yIntakeInch), headingRadiansIntake)
//                .waitSeconds(1)
//                .strafeToLinearHeading(new Vector2d(xIntakeInch, yIntakeInch - 8), headingRadiansIntake, new TranslationalVelConstraint(20.0))
//                .waitSeconds(1)
//                .strafeToLinearHeading(new Vector2d(xIntakeInch, yIntakeInch - 13), headingRadiansIntake, new TranslationalVelConstraint(20.0));
//                        .waitSeconds(.5)
//                        .strafeToLinearHeading(new Vector2d(xIntakeInch, yIntakeInch - 15), headingRadiansIntake, new TranslationalVelConstraint(15.0));

        return intakeTrajectory;
    }

    private TrajectoryActionBuilder getTrajectoryActionBuilderForLeaveZone(Pose2d currentPose, TrajectoryActionBuilder currTrajectory, TrajectoryActionBuilder toBeTrajectory) {

        if(currTrajectory == null) {

            telemetry.addData("Current trajectory is null", "True");

            return drive.actionBuilder(startPose)
                    .strafeToLinearHeading(new Vector2d(nearLaunchPose.position.x, nearLaunchPose.position.y), nearLaunchPose.heading);
        } else {

            telemetry.addData("Current trajectory is null", "False");

            return currTrajectory.endTrajectory().fresh()
                    .strafeToLinearHeading(new Vector2d(nearLeavePose.position.x, nearLeavePose.position.y), nearLeavePose.heading);
        }

    }
}