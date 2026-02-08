package org.firstinspires.ftc.teamcode.mainbot.opmodes.notneeded;

import com.acmerobotics.roadrunner.*;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchMechanism;

import java.lang.Math;
@Disabled
@Autonomous(name = "MainBotAutoWithIntakePath", group = "MainBot")
public class MainBotAutoWithIntakePath extends LinearOpMode {

    MecanumDrive drive;
    MainBotLaunchMechanism launcher;
    MainBotIntakeMechanism intake;

    /* ---------------- POSES ---------------- */
    Pose2d startPose = new Pose2d(-48, -48, Math.toRadians(-135));
    Pose2d shootPose = new Pose2d(-20, -24, Math.toRadians(-135));

    @Override
    public void runOpMode() {

        drive = new MecanumDrive(hardwareMap, startPose);
        launcher = new MainBotLaunchMechanism(hardwareMap, telemetry, "Blue");
        intake = new MainBotIntakeMechanism(hardwareMap, telemetry);

        waitForStart();
        if (isStopRequested()) return;

        Actions.runBlocking(
                drive.actionBuilder(startPose)
                        .strafeToLinearHeading(new Vector2d(-20, -24), Math.toRadians(-135))
                        .waitSeconds(3)
                        .build()
        );

//        intake.startIntake();
//        fireLauncher();
//        intake.stopIntake();

        Actions.runBlocking(
                drive.actionBuilder(shootPose)
                        .strafeToLinearHeading(new Vector2d(-11.8, -23), Math.toRadians(-90))
                        .build()
        );

//        intake.startIntake();


        Actions.runBlocking(drive.actionBuilder(new Pose2d(-11.8, -23, Math.toRadians(-90)))
                        .strafeToLinearHeading(new Vector2d(-11.8, -51), Math.toRadians(-90))
                        .build()
        );

//        intake.stopIntake();


        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(-11.8, -51, Math.toRadians(-90)))
                        .strafeToLinearHeading(new Vector2d(-20, -24), Math.toRadians(-135))
                        .waitSeconds(3)
                        .build()
        );

//        intake.startIntake();
//        fireLauncher();
//        intake.stopIntake();

        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(-20, -24, Math.toRadians(-135)))
                        .strafeToLinearHeading(new Vector2d(11.5, -23), Math.toRadians(-90))
                        .build()
        );

//        intake.startIntake();


        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(11.5, -23, Math.toRadians(-90)))
                        .strafeToLinearHeading(new Vector2d(11.5, -51), Math.toRadians(-90))
                        .build()
        );

//        intake.stopIntake();

        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(11.5, -51, Math.toRadians(-90)))
                        .strafeToLinearHeading(new Vector2d(0, -40), Math.toRadians(-180))
                        .strafeToLinearHeading(new Vector2d(0, -53), Math.toRadians(-180))
                        .strafeToLinearHeading(new Vector2d(-20, -24), Math.toRadians(-135))
                        .waitSeconds(3)
                        .build()
        );

//        intake.startIntake();
//        fireLauncher();
//        intake.stopIntake();

        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(-20, -24, Math.toRadians(-135)))
                        .strafeToLinearHeading(new Vector2d(-16, -40), Math.toRadians(0))
                        .build()
        );
    }

    private void fireLauncher(String launchZone) {
        launcher.launchForAuto(true, launchZone, intake, telemetry);
        while (opModeIsActive() && !launcher.launchForAuto(false, launchZone, intake, telemetry)) {
            idle();
        }
    }
}
