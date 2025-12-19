package org.firstinspires.ftc.teamcode.testbench.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public final class TestServo {

    public final Servo leftIntakeFeeder, rightIntakeFeeder;
    final double STOP_SPEED = 0.0;
    final double MAX_SPEED = 0.5;

    public TestServo(HardwareMap hardwareMap, Telemetry telemetry) {

//        leftIntakeFeeder = hardwareMap.get(Servo.class, "leftIntakeServo");
        leftIntakeFeeder = null;
        rightIntakeFeeder = hardwareMap.get(Servo.class, "rightIntakeServo");
//        rightIntakeFeeder = null;


        /*
         * Much like our drivetrain motors, we set the left feeder servo to reverse so that they
         * both work to feed the ball into the robot.
         */
//        leftIntakeFeeder.setDirection(DcMotorSimple.Direction.REVERSE);

        /*
         * set Feeders to an initial value to initialize the servo controller
         */
        lowPosition();
    }

    public void topPosition() {
//        rightIntakeFeeder.setPosition(1); // Right servo
        rightIntakeFeeder.setPosition(1); // Right servo
//        leftIntakeFeeder.setPosition(0); // Left servo

    }

    public void lowPosition() {
//        rightIntakeFeeder.setPosition(0); // Right servo
        rightIntakeFeeder.setPosition(0);
//        leftIntakeFeeder.setPosition(1); // Left servo

    }

    public void middlePosition() {
        rightIntakeFeeder.setPosition(.5); // Right servo
//        leftIntakeFeeder.setPosition(.7); // Left servo

    }

}
