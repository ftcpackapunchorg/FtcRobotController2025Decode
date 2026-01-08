package org.firstinspires.ftc.teamcode.mainbot.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utils.StarterBotConstants;

public final class MainBotLaunchFeederMechanism {

    public final CRServo leftFeeder, rightFeeder;

    public MainBotLaunchFeederMechanism(HardwareMap hardwareMap, Telemetry telemetry) {

        leftFeeder = hardwareMap.get(CRServo.class, StarterBotConstants.LEFT_FEEDER_CRSERVO_NAME);
        rightFeeder = hardwareMap.get(CRServo.class, StarterBotConstants.RIGHT_FEEDER_CRSERVO_NAME);

        /*
         * set Feeders to an initial value to initialize the servo controller
         */
        final double STOP_SPEED = 0.0;
        leftFeeder.setPower(STOP_SPEED);
        rightFeeder.setPower(STOP_SPEED);


        /*
         * Much like our drivetrain motors, we set the left feeder servo to reverse so that they
         * both work to feed the ball into the robot.
         */
        leftFeeder.setDirection(DcMotorSimple.Direction.REVERSE);

    }

}
