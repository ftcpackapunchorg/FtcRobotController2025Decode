package org.firstinspires.ftc.teamcode.mainbot.opmodes.auto;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchMechanism;

@Autonomous(name = "MainBotAutoBlueRRGoAndLaunch", group = "MainBot")
public class MainBotAutoBlueRRGoAndLaunch extends LinearOpMode {

    MecanumDrive drive;
    MainBotLaunchMechanism launcher;
    MainBotIntakeMechanism intake;

    /* ---------------- POSES ---------------- */
    Pose2d startPose = new Pose2d(60, -12, Math.toRadians(180));
    Pose2d shootPose = new Pose2d(-12, -12, Math.toRadians(135));

    Pose2d intakeLeft  = new Pose2d(-13, -54, Math.toRadians(180));
    Pose2d intakeRight = new Pose2d(13, -54, Math.toRadians(180));

    @Override
    public void runOpMode() {

        drive = new MecanumDrive(hardwareMap, startPose);
        launcher = new MainBotLaunchMechanism(hardwareMap, telemetry, "Blue");
        intake = new MainBotIntakeMechanism(hardwareMap, telemetry);

        waitForStart();
        if (isStopRequested()) return;

        /* =========================================================
         * MOVE TO SHOOT POSITION
         * ========================================================= */
        Actions.runBlocking(
                drive.actionBuilder(startPose)
                        .waitSeconds(1)
                        .lineToX(-12)
                        .turn(Math.toRadians(45))
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
                        .strafeTo(new Vector2d(-13, -34))
                        .build()
        );

        intake.startIntake();
//        Actions.runBlocking(
//                drive.actionBuilder(new Pose2d(-13,-34,Math.toRadians(180)))
//                        .strafeTo(new Vector2d(-13, -54))
//                        .build()
//        );
        intake.stopIntake();

        Actions.runBlocking(
                drive.actionBuilder(intakeLeft)
                        .strafeTo(new Vector2d(-13, -12))
                        .strafeTo(new Vector2d(-11, -12))
                        .turn(Math.toRadians(-45))
                        .build()
        );
        intake.startIntake();
        fireLauncher();
        intake.stopIntake();

        /* =========================================================
         * RIGHT INTAKE
         * ========================================================= */
        Actions.runBlocking(
                drive.actionBuilder(shootPose)
                        .strafeTo(new Vector2d(13, -12))
                        .strafeTo(new Vector2d(13, -54))
                        .build()
        );

        intake.startIntake();
    //    sleep(700);
        intake.stopIntake();

        Actions.runBlocking(
                drive.actionBuilder(intakeRight)
                        .strafeTo(new Vector2d(13, -12))
                        .strafeTo(new Vector2d(-12, -12))
                        .turn(Math.toRadians(-45))
                        .build()
        );
        intake.startIntake();
        fireLauncher();
        intake.stopIntake();

        /* =========================================================
         * PARK
         * ========================================================= */
        Actions.runBlocking(
                drive.actionBuilder(shootPose)
                        .strafeTo(new Vector2d(13, -16))
                        .turn(Math.toRadians(180))
                        .build()
        );
    }

    /* =============================================================
     * LAUNCHER HELPER
     * ============================================================= */
    private void fireLauncher() {
        launcher.launchForAuto(true);
        while (opModeIsActive() && !launcher.launchForAuto(false)) {
            idle();
        }
    }
}
