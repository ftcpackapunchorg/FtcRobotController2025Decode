package org.firstinspires.ftc.teamcode.testbench.testOpModes;


import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MecanumDrive;


@TeleOp(name="StarterBotMecTestBench", group="TestBench")
public class StarterBotMecTestBench extends OpMode {
    double power = 1;

    MecanumDrive drive;

    @Override
    public void init() {
        Pose2d initPose = new Pose2d(-43,43,0);

        /*
         * Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step.
         */
        drive = new MecanumDrive(hardwareMap, initPose);

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Initialized");

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

            drive.launcher.setPower(.5);

        }

        if(gamepad1.bWasReleased()) {

            drive.launcher.setPower(0);
        }

        if(gamepad1.x) {

            drive.leftFeeder.setPower(-.5);
            drive.rightFeeder.setPower(.5);
        }

        if(gamepad1.xWasReleased()) {
            drive.leftFeeder.setPower(0);
            drive.rightFeeder.setPower(0);
        }
    }
}
