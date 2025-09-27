package org.firstinspires.ftc.teamcode.test;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name="testmotor1", group="Linear OpMode")
public class testmotor extends LinearOpMode {

    DcMotor motor;
    double power = 1;


    @Override
    public void runOpMode() throws InterruptedException {

        motor = hardwareMap.get(DcMotor.class, "testMotor");

        waitForStart();

        while (opModeIsActive()) {

            motor.setPower(power);
        }
    }
}
