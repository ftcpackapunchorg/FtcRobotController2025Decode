package org.firstinspires.ftc.teamcode.mainbot.opmodes.auto;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotMecanumDrive;

@Autonomous(name = "MainBotAutoBlueFarWithIntakeLatest", group = "MainBot")
public class MainBotAutoBlueFarWithIntakeLatest extends LinearOpMode {

    MainBotMecanumDrive drive;
    MainBotLaunchMechanism launcher;
    MainBotIntakeMechanism intake;

    int shotsToFire = 3;

    private AutonomousState autonomousState;

    ElapsedTime feederTimer = new ElapsedTime();
    ElapsedTime autoFeederTimer = new ElapsedTime();

    final double FEED_TIME = 0.20;
    final double TIME_BETWEEN_SHOTS = 3;

    private enum AutonomousState {
        LAUNCH,
        GO_TO_LAUNCH_POSITION,
        WAIT_FOR_LAUNCH,
        START_INTAKE,
        GO_TO_INTAKE_POS1,
        GO_TO_LEAVE_ZONE,
        COMPLETE
    }

    /* ---------------- POSES ---------------- */
    Pose2d startPose = new Pose2d(60, -12, Math.toRadians(180));
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

        TrajectoryActionBuilder goToLaunchZone = drive.actionBuilder(startPose)
                .strafeToLinearHeading(new Vector2d(56, -12), Math.toRadians(195));

        TrajectoryActionBuilder goToLeaveZone = goToLaunchZone.endTrajectory().fresh()
                .strafeTo(new Vector2d(46, -27));

        TrajectoryActionBuilder goToIntakePos1 = goToLaunchZone.endTrajectory().fresh()
                .strafeToLinearHeading(new Vector2d(36, -30), Math.toRadians(270), new TranslationalVelConstraint(20.0))
                .strafeToLinearHeading(new Vector2d(36, -53), Math.toRadians(270), new TranslationalVelConstraint(20.0));

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {

            switch (autonomousState) {

                case GO_TO_LAUNCH_POSITION:
                    Actions.runBlocking(goToLaunchZone.build());
                    autonomousState = AutonomousState.LAUNCH;
                    break;

                case LAUNCH:
                    launcher.launchForAuto(true, LAUNCH_ZONE, intake, telemetry);
                    autonomousState = AutonomousState.WAIT_FOR_LAUNCH;
                    break;

                case WAIT_FOR_LAUNCH:
                    telemetry.addData("shotsToFire", shotsToFire);

                    if (launcher.launchForAuto(false, LAUNCH_ZONE, intake, telemetry)) {
                        shotsToFire -= 1;
                        if (shotsToFire > 0) {
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

                case START_INTAKE:
                    intake.startIntake();
                    autonomousState = AutonomousState.GO_TO_INTAKE_POS1;
                    break;

                case GO_TO_INTAKE_POS1:
                    Actions.runBlocking(goToIntakePos1.build());
                    autonomousState = AutonomousState.GO_TO_LEAVE_ZONE;
                    break;

                case GO_TO_LEAVE_ZONE:
                    Actions.runBlocking(goToLeaveZone.build());
                    autonomousState = AutonomousState.COMPLETE;
                    break;

                case COMPLETE:
                    return;
            }
        }
    }
}
