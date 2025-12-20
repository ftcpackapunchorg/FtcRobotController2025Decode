package org.firstinspires.ftc.teamcode.testbench.testOpModes;


import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.testbench.mechanisms.TestServo;


@TeleOp(name="TestServoOpMode", group="TestBench")
public class TestServoOpMode extends OpMode {
    double power = 1;

    MecanumDrive drive;

    TestServo servo;


    @Override
    public void init() {
        Pose2d initPose = new Pose2d(-43,43,0);

        /*
         * Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step.
         */
        servo = new TestServo(hardwareMap, telemetry);

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Initialized");

    }

    @Override
    public void loop() {

        if(gamepad1.a) {

            servo.middlePosition();
        }

        if(gamepad1.aWasReleased()) {

            servo.lowPosition();
        }

        if(gamepad1.b) {

            servo.topPosition();

        }

        if(gamepad1.bWasReleased()) {

            servo.middlePosition();
        }
    }
}
