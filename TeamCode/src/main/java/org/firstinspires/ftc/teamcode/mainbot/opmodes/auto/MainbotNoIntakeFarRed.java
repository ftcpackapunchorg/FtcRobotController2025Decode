package org.firstinspires.ftc.teamcode.mainbot.opmodes.auto;

import com.acmerobotics.roadrunner.*;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.gobildastarterbot.mechanicals.StarterBotLaunchMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchWithFeederMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotMecanumDrive;
import org.firstinspires.ftc.teamcode.prototypebot.mechanicals.PrototypeBotIntakeMechanism;

import java.lang.Math;

@Autonomous(name = "MainBotAutoFarRed", group = "MainBot")
public class MainbotNoIntakeFarRed extends LinearOpMode {

    MainBotMecanumDrive drive;
    MainBotLaunchWithFeederMechanism launcher;
    MainBotIntakeMechanism intake;

    /* ---------------- POSES ---------------- */
    Pose2d startPose = new Pose2d(-45, 51, Math.toRadians(135));
    Pose2d shootPose = new Pose2d(-40, 30, Math.toRadians(135));

    Pose2d intake1  = new Pose2d(-13, -54, Math.toRadians(180));
    Pose2d intake2 = new Pose2d(13, -54, Math.toRadians(180));

    Pose2d intake3 = new Pose2d(34.5, 23, Math.toRadians(90));



    @Override
    public void runOpMode() {

        drive = new MainBotMecanumDrive(hardwareMap, startPose);
        launcher = new MainBotLaunchWithFeederMechanism(hardwareMap, telemetry,"Blue" );
        intake = new MainBotIntakeMechanism(hardwareMap, telemetry);

        waitForStart();
        if (isStopRequested()) return;

        /* =========================================================
         * MOVE TO SHOOT POSITION
         * ========================================================= */
        Actions.runBlocking(
                drive.actionBuilder(startPose)
                        .strafeTo(new Vector2d(-40,30))
                        .turn(Math.toRadians(135))
                        .build()
        );
        intake.startIntake();
        fireLauncher();
        intake.stopIntake();


        /* =========================================================
         * LEFT INTAKE
         * ========================================================= */
        Actions.runBlocking(
                drive.actionBuilder(shootPose)
                        .strafeToLinearHeading(new Vector2d(-11.8, 23),Math.toRadians(90))
                        .build()
        );

        intake.startIntake();
        //   sleep(700);
        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(-11.8,23,Math.toRadians(90)))
                        .strafeToLinearHeading(new Vector2d(-11.8, 51),Math.toRadians(90))
                        .build()
        );
        intake.stopIntake();

        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(-11.8,51,Math.toRadians(90)))
                        .strafeToLinearHeading(new Vector2d(-40, 30),Math.toRadians(135))
                        .build()
        );
        intake.startIntake();
        fireLauncher();
        intake.stopIntake();


        /* =========================================================
         * RIGHT INTAKE
         * ========================================================= */
        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(-40,30,Math.toRadians(135)))
                        .strafeToLinearHeading(new Vector2d(11.5,23),Math.toRadians(90))
                        .build()
        );

        intake.startIntake();
        //    sleep(700);

        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(11.5,23,Math.toRadians(90)))
                        .strafeToLinearHeading(new Vector2d(11.5, 51),Math.toRadians(90))
                        .build()
        );
        intake.stopIntake();
        /* =========================================================
         * PARK
         * ========================================================= */
        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(11.5,51,Math.toRadians(90)))
                        .strafeToLinearHeading(new Vector2d(0, 40),Math.toRadians(180))
                        .build()
        );
        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(0,40,Math.toRadians(180)))
                        .strafeToLinearHeading(new Vector2d(0, 53),Math.toRadians(180))
                        .build()
        );
        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(0,53,Math.toRadians(180)))
                        .strafeToLinearHeading(new Vector2d(-40, 30),Math.toRadians(135))
                        .build()
        );
        intake.startIntake();
        fireLauncher();
        intake.stopIntake();

        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(-40,30,Math.toRadians(135)))
                        .strafeToLinearHeading(new Vector2d(34.5, 23),Math.toRadians(90))
                        .build()
        );
        intake.startIntake();
        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(34.5,23,Math.toRadians(90)))
                        .strafeToLinearHeading(new Vector2d(34.5, 51),Math.toRadians(90))
                        .build()
        );
        intake.stopIntake();
        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(34.5,51,Math.toRadians(90)))
                        .strafeToLinearHeading(new Vector2d(-40, 30),Math.toRadians(135))
                        .build()
        );
        intake.startIntake();
        fireLauncher();
        intake.stopIntake();
        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(-40,30,Math.toRadians(135)))
                        .strafeToLinearHeading(new Vector2d(8, 23),Math.toRadians(45))
                        .build()
        );
    }

    private TrajectoryActionBuilder strafeToLinearHeading(Vector2d vector2d, double radians) {
        return null;
    }

    /* =============================================================
     * LAUNCHER HELPER
     * ============================================================= */
    private void fireLauncher() {
        launcher.launchForAuto(true, null, null);
        while (opModeIsActive() && !launcher.launchForAuto(false, null, null)) {
            idle();
        }
    }
}

