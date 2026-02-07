package org.firstinspires.ftc.teamcode.mainbot.opmodes.auto;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchWithFeederMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotMecanumDrive;

@Autonomous(name = "LaunchandleaveCloseRed", group = "MainBot")
public class LaunchandLeaveCloseRed extends LinearOpMode {

    MainBotMecanumDrive drive;
    MainBotLaunchWithFeederMechanism launcher;
    MainBotIntakeMechanism intake;

    /* ---------------- POSES ---------------- */
    Pose2d startPose = new Pose2d(-48, 51, Math.toRadians(135));
    Pose2d shootPose = new Pose2d(-16, 20, Math.toRadians(135));





    @Override
    public void runOpMode() {

        drive = new MainBotMecanumDrive(hardwareMap, startPose);
        launcher = new MainBotLaunchWithFeederMechanism(hardwareMap, telemetry, "Red");



        /* =========================================================
         * MOVE TO SHOOT POSITION
         * ========================================================= */
        Actions.runBlocking(
                drive.actionBuilder(startPose)
                        .strafeTo(new Vector2d(-16,20))
                        .build()
        );
        fireLauncher();



        /* =========================================================
         * LEFT INTAKE
         * ========================================================= */
        Actions.runBlocking(
                drive.actionBuilder(shootPose)
                        .strafeToLinearHeading(new Vector2d(-10, 25),Math.toRadians(90))
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
        launcher.launchForAuto(true, null, null, null, null, telemetry);
        while (opModeIsActive() && !launcher.launchForAuto(false, null, null, null, null, telemetry)) {
            idle();
        }
    }
}


