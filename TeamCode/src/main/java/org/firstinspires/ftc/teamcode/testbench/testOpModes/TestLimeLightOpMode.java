package org.firstinspires.ftc.teamcode.testbench.testOpModes;


import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.gobildastarterbot.mechanicals.StarterBotFeederMechanism;
import org.firstinspires.ftc.teamcode.gobildastarterbot.mechanicals.StarterBotLaunchMechanism;


@TeleOp(name="TestLimeLightOpMode", group="TestBench")
public class TestLimeLightOpMode extends OpMode {
    double power = 1;

    private Limelight3A limelight;

    MecanumDrive drive;

    StarterBotLaunchMechanism launchMechanism;

    StarterBotFeederMechanism feederMechanism;

    @Override
    public void init() {
        Pose2d initPose = new Pose2d(-43,43,0);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Initialized");

    }

    @Override
    public void start() {



    }

    @Override
    public void loop() {

        if(gamepad1.a) {

            drive.leftFront.setPower(-.5);
            drive.rightFront.setPower(.5);
            drive.rightBack.setPower(.5);
            drive.leftBack.setPower(-.5);
        }

        if(gamepad1.aWasReleased()) {

            drive.leftFront.setPower(-.5);
            drive.rightFront.setPower(.5);
            drive.rightBack.setPower(.5);
            drive.leftBack.setPower(-.5);
        }

        if(gamepad1.b) {

            launchMechanism.launcher.setPower(.5);

        }

        if(gamepad1.bWasReleased()) {

            launchMechanism.launcher.setPower(0);
        }

        if(gamepad1.x) {

            feederMechanism.leftFeeder.setPower(-.5);
            feederMechanism.rightFeeder.setPower(.5);
        }

        if(gamepad1.xWasReleased()) {
            feederMechanism.leftFeeder.setPower(0);
            feederMechanism.rightFeeder.setPower(0);
        }
    }
}
