package org.firstinspires.ftc.teamcode.mainbot.opmodes.auto;

import com.acmerobotics.roadrunner.*;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotMecanumDrive;

import java.lang.Math;

@Autonomous(name = "MainBotNoIntakeFarSideBlue", group = "MainBot")
public class MainBotNoIntakeFarSideBlue extends LinearOpMode {

    /* ---------------- MECHANISMS ---------------- */
    MainBotMecanumDrive drive;
    MainBotLaunchMechanism launcher;
    MainBotIntakeMechanism intake;

    /* ---------------- POSES ---------------- */
    Pose2d startPose = new Pose2d(60, -12, Math.toRadians(180));

    Pose2d shootPose = new Pose2d(56, -12, Math.toRadians(25));
    Pose2d leftIntakePose = new Pose2d(-13, -54, Math.toRadians(180));
    Pose2d rightIntakePose = new Pose2d(13, -54, Math.toRadians(180));
    final String FAR = "FAR_ZONE";
    @Override
    public void runOpMode() {

        drive = new MainBotMecanumDrive(hardwareMap, startPose);
        launcher = new MainBotLaunchMechanism(hardwareMap, telemetry, "Red");
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
                        .strafeTo(new Vector2d(56, -12))
                        .turn(Math.toRadians(25))
                        .build()
        );

        /* ---------------- FIRST SHOT ---------------- */
        intake.startIntake();
        fireLauncher(FAR);
        intake.stopIntake();

        /* =====================================================
         * LEFT INTAKE
         * ===================================================== */
        Actions.runBlocking(
                drive.actionBuilder(shootPose)
                        .strafeTo(new Vector2d(53, -25))
                        .turn(Math.toRadians(100))
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

    private void fireLauncher(String launchZone) {
        launcher.launchForAuto(true, launchZone, intake, telemetry);
        while (opModeIsActive() && !launcher.launchForAuto(false, launchZone, intake, telemetry)) {
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
