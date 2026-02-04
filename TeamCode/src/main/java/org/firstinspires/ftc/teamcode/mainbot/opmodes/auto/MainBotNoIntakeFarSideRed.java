package org.firstinspires.ftc.teamcode.mainbot.opmodes.auto;

import com.acmerobotics.roadrunner.*;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchMechanism;

import java.lang.Math;

@Autonomous(name = "MainBotNoIntakeFarSide", group = "MainBot")
public class MainBotNoIntakeFarSideRed extends LinearOpMode {

    /* ---------------- MECHANISMS ---------------- */
    MecanumDrive drive;
    MainBotLaunchMechanism launcher;
    MainBotIntakeMechanism intake;

    /* ---------------- POSES ---------------- */
    Pose2d startPose = new Pose2d(-60,12,Math.toRadians(180));

    Pose2d shootPose = new Pose2d(-56, 12, Math.toRadians(-45));
    Pose2d leftIntakePose = new Pose2d(-13, -54, Math.toRadians(180));
    Pose2d rightIntakePose = new Pose2d(13, -54, Math.toRadians(180));

    @Override
    public void runOpMode() {

        drive = new MecanumDrive(hardwareMap, startPose);
        launcher = new MainBotLaunchMechanism(hardwareMap, telemetry);
        intake = new MainBotIntakeMechanism(hardwareMap, telemetry);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        /* =====================================================
         * MOVE TO SHOOT POSITION
         * ===================================================== */
        Actions.runBlocking(
                drive.actionBuilder(startPose)
                        .strafeTo(new Vector2d(-56,12))
                        .turn(Math.toRadians(30))
                        .build()
        );

        /* ---------------- FIRST SHOT ---------------- */
        intake.startIntake();
        fireLauncher();
        intake.stopIntake();

        /* =====================================================
         * LEFT INTAKE
         * ===================================================== */
        Actions.runBlocking(
                drive.actionBuilder(shootPose)
                        .strafeTo(new Vector2d(-53,25))
                        .turn(Math.toRadians(-60))
                        .build()
        );

//        runIntake(700);
//
//        Actions.runBlocking(
//                drive.actionBuilder(leftIntakePose)
//                        .strafeTo(new Vector2d(-13, -12))
//                        .strafeTo(new Vector2d(-11, -12))
//                        .turn(Math.toRadians(-45))
//                        .build()
//        );
//
//        /* ---------------- SECOND SHOT ---------------- */
//        fireLauncher();
//
//        /* =====================================================
//         * RIGHT INTAKE
//         * ===================================================== */
//        Actions.runBlocking(
//                drive.actionBuilder(shootPose)
//                        .strafeTo(new Vector2d(13, -12))
//                        .strafeTo(rightIntakePose.position)
//                        .build()
//        );
//
//        runIntake(700);
//
//        Actions.runBlocking(
//                drive.actionBuilder(rightIntakePose)
//                        .strafeTo(new Vector2d(13, -12))
//                        .strafeTo(new Vector2d(-12, -12))
//                        .turn(Math.toRadians(-45))
//                        .build()
//        );
//
//        /* ---------------- THIRD SHOT ---------------- */
//        fireLauncher();
//
//        /* =====================================================
//         * PARK
//         * ===================================================== */
//        Actions.runBlocking(
//                drive.actionBuilder(shootPose)
//                        .strafeTo(new Vector2d(13, -16))
//                        .turn(Math.toRadians(180))
//                        .build()
//        );
    }

    /* =====================================================
     * HELPER METHODS
     * ===================================================== */

    private void fireLauncher() {
        launcher.launchForAuto(true);
        while (opModeIsActive() && !launcher.launchForAuto(false)) {
            idle();
        }
        launcher.stopLauncher();
    }

    private void runIntake(long durationMs) {
        intake.startIntake();
        sleep(durationMs);
        intake.stopIntake();
    }
}
