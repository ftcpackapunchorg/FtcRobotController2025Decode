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

@Autonomous(name = "Red Far", group = "MainBot")
public class MainBotAutoRedFarWithIntakeLatest extends LinearOpMode {

    MainBotMecanumDrive drive;
    MainBotLaunchWithFeederMechanism launcher;
    MainBotIntakeMechanism intake;
    MainBotRGBLightIndicator artifactIntakeIndicator, isArtifactAllowedIndicator;

    int shotsToFire = 3;
    int maxShotsToFire = 3;

    int currNoOfIntakePaths = 0;
    int maxNoOfIntakePaths = 2;

    private AutonomousState autonomousState;

    ElapsedTime autonomousTimer = new ElapsedTime();

    private enum AutonomousState {
        INIT,
        LAUNCH,
        GO_TO_LAUNCH_POSITION,
        WAIT_FOR_LAUNCH,
        INTAKE,
        GO_TO_INTAKE_POS,
        GO_TO_LEAVE_ZONE,
        COMPLETE
    }

    /* ---------------- POSES (FAR, MeepMeep) ---------------- */
    Pose2d startPose = new Pose2d(67, 12, Math.toRadians(180));

    Pose2d farLaunchPose = new Pose2d(56, 12, Math.toRadians(154));

    Pose2d farParkPose = new Pose2d(46, 27, Math.toRadians(180));

    Pose2d intake1Start = new Pose2d(37, 23, Math.toRadians(90));
    Pose2d intake1End   = new Pose2d(37, 55.5, Math.toRadians(90));

    Pose2d intake2Start = new Pose2d(12, 23, Math.toRadians(90));
    Pose2d intake2End   = new Pose2d(12, 53, Math.toRadians(90));

    TrajectoryActionBuilder currentTrajectory = null;
    TrajectoryActionBuilder newTrajectory = null;

    @Override
    public void runOpMode() {

        drive = new MainBotMecanumDrive(hardwareMap, startPose);
        launcher = new MainBotLaunchWithFeederMechanism(hardwareMap, telemetry, "Red");
        intake = new MainBotIntakeMechanism(hardwareMap, telemetry);

        String LAUNCH_ZONE = "FAR_ZONE";

        autonomousState = MainBotAutoRedFarWithIntakeLatest.AutonomousState.INIT;
        isArtifactAllowedIndicator = new MainBotRGBLightIndicator();
        isArtifactAllowedIndicator.init(hardwareMap, MainBotConstants.ALLOW_ARTIFACT_SERVO_INDICATOR);

        waitForStart();
        autonomousTimer.reset();
        if (isStopRequested()) return;

        while (opModeIsActive()) {

            switch (autonomousState) {

                case INIT:
                    autonomousState = MainBotAutoRedFarWithIntakeLatest.AutonomousState.GO_TO_LAUNCH_POSITION;
                    break;

                case GO_TO_LAUNCH_POSITION:
                    newTrajectory = getTrajectoryActionBuilderForLaunchZone(currentTrajectory);
                    Actions.runBlocking(newTrajectory.build());
                    currentTrajectory = newTrajectory;
                    autonomousState = MainBotAutoRedFarWithIntakeLatest.AutonomousState.LAUNCH;
                    break;

                case LAUNCH:
                    launcher.launchForAuto(true, LAUNCH_ZONE, intake,isArtifactAllowedIndicator, drive,  telemetry);
                    autonomousState = MainBotAutoRedFarWithIntakeLatest.AutonomousState.WAIT_FOR_LAUNCH;
                    break;

                case WAIT_FOR_LAUNCH:
                    if (launcher.launchForAuto(false, LAUNCH_ZONE, intake,isArtifactAllowedIndicator , drive, telemetry)) {
                        shotsToFire--;
                        if (shotsToFire > 0) {
                            autonomousState = MainBotAutoRedFarWithIntakeLatest.AutonomousState.LAUNCH;
                        } else {
                            Actions.runBlocking(
                                    new ParallelAction(
                                            launcher.reverseLauncherAction(false, LAUNCH_ZONE, intake, drive, telemetry),
                                            intake.stopIntakeAction()
                                    )
                            );
                            autonomousState = MainBotAutoRedFarWithIntakeLatest.AutonomousState.INTAKE;
                        }
                    }
                    break;

                case INTAKE:
                    intake.startIntake();
                    autonomousState = MainBotAutoRedFarWithIntakeLatest.AutonomousState.GO_TO_INTAKE_POS;
                    break;

                case GO_TO_INTAKE_POS:
                    currNoOfIntakePaths++;

                    if (currNoOfIntakePaths <= maxNoOfIntakePaths) {
                        newTrajectory = getTrajectoryActionBuilderForIntake(currNoOfIntakePaths, currentTrajectory);
                        Actions.runBlocking(newTrajectory.build());
                        currentTrajectory = newTrajectory;

                        intake.stopIntake();
                        shotsToFire = maxShotsToFire;
                        launcher.startLauncher();

                        autonomousState = MainBotAutoRedFarWithIntakeLatest.AutonomousState.GO_TO_LAUNCH_POSITION;
                    } else {
                        autonomousState = MainBotAutoRedFarWithIntakeLatest.AutonomousState.GO_TO_LEAVE_ZONE;
                    }
                    break;

                case GO_TO_LEAVE_ZONE:
                    newTrajectory = getTrajectoryActionBuilderForLeaveZone(currentTrajectory);
                    Actions.runBlocking(newTrajectory.build());
                    autonomousState = MainBotAutoRedFarWithIntakeLatest.AutonomousState.COMPLETE;
                    break;

                case COMPLETE:
                    return;
            }
        }
    }

    /* ---------------- TRAJECTORY HELPERS ---------------- */

    private TrajectoryActionBuilder getTrajectoryActionBuilderForLaunchZone(TrajectoryActionBuilder currTrajectory) {
        if (currTrajectory == null) {
            return drive.actionBuilder(startPose)
                    .strafeToLinearHeading(
                            new Vector2d(farLaunchPose.position.x, farLaunchPose.position.y),
                            farLaunchPose.heading
                    );
        } else {
            return currTrajectory.endTrajectory().fresh()
                    .strafeToLinearHeading(
                            new Vector2d(farLaunchPose.position.x, farLaunchPose.position.y),
                            farLaunchPose.heading
                    );
        }
    }

    private TrajectoryActionBuilder getTrajectoryActionBuilderForIntake(int intakeNo, TrajectoryActionBuilder currTrajectory) {

        if (intakeNo == 1) {
            return currTrajectory.endTrajectory().fresh()
                    .strafeToLinearHeading(
                            intake1Start.position,
                            intake1Start.heading,
                            new TranslationalVelConstraint(20.0)
                    )
//                    .waitSeconds(0.5)
                    .strafeTo(new Vector2d(37,49), new TranslationalVelConstraint(15.0))
                    .waitSeconds(0.5)
                    .strafeTo(new Vector2d(37,52), new TranslationalVelConstraint(15.0))
                    .strafeTo(intake1End.position, new TranslationalVelConstraint(15.0));
        } else {
            return currTrajectory.endTrajectory().fresh()
                    .strafeToLinearHeading(
                            intake2Start.position,
                            intake2Start.heading,
                            new TranslationalVelConstraint(10.0)
                    )
                    .waitSeconds(1)
                    .strafeTo(intake2End.position)
                    .waitSeconds(1);
        }
    }

    private TrajectoryActionBuilder getTrajectoryActionBuilderForLeaveZone(TrajectoryActionBuilder currTrajectory) {
        return currTrajectory.endTrajectory().fresh()
                .strafeTo(farParkPose.position);
    }
}
