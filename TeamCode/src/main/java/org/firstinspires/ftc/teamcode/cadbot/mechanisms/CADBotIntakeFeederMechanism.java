package org.firstinspires.ftc.teamcode.cadbot.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.cadbot.utils.CADBotConstants;

public final class CADBotIntakeFeederMechanism {

    public final CRServo leftIntakeFeeder, rightIntakeFeeder;
    final double STOP_SPEED = 0.0;
    final double MAX_SPEED = 0.5;

    public CADBotIntakeFeederMechanism(HardwareMap hardwareMap, Telemetry telemetry) {

        leftIntakeFeeder = hardwareMap.get(CRServo.class, CADBotConstants.LEFT_INTAKE_CR_SERVO_NAME);
        rightIntakeFeeder = hardwareMap.get(CRServo.class, CADBotConstants.RIGHT_INTAKE_CR_SERVO_NAME);

        /*
         * Much like our drivetrain motors, we set the left feeder servo to reverse so that they
         * both work to feed the ball into the robot.
         */
        leftIntakeFeeder.setDirection(DcMotorSimple.Direction.REVERSE);

        /*
         * set Feeders to an initial value to initialize the servo controller
         */
        stopIntakeFeeders();
    }

    public void startIntakeFeeders() {
        leftIntakeFeeder.setPower(MAX_SPEED);
        rightIntakeFeeder.setPower(MAX_SPEED);
    }

    public void stopIntakeFeeders() {
        leftIntakeFeeder.setPower(STOP_SPEED);
        rightIntakeFeeder.setPower(STOP_SPEED);
    }

}
