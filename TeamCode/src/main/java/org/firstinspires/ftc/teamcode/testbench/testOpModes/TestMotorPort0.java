package org.firstinspires.ftc.teamcode.testbench.testOpModes;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@TeleOp(name="TestMotorPort0", group="Linear OpMode")
public class TestMotorPort0 extends LinearOpMode {

    DcMotor motor;
    double power = 1;


    @Override
    public void runOpMode() throws InterruptedException {

        motor = hardwareMap.get(DcMotor.class, "testMotor");
        motor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {

            motor.setPower(power);
        }
    }
}
