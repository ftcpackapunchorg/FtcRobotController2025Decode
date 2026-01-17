package org.firstinspires.ftc.teamcode.testbench.testOpModes;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;


@TeleOp(name="TestMotorWithEncoder", group="Linear OpMode")
public class TestMotorWithEncoder extends LinearOpMode {

    DcMotor motor;
    double power = 1;


    @Override
    public void runOpMode() throws InterruptedException {

        motor = hardwareMap.get(DcMotorEx.class, "testMotor");
        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
//        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setDirection(DcMotorEx.Direction.FORWARD);

        waitForStart();

        while (opModeIsActive()) {

            motor.setPower(power);

            double CPR = 383.6;

            int position = motor.getCurrentPosition();
            double revolutions = position/CPR;

            double angle = revolutions * 360;
            double angleNormalized = angle % 360;

            // Show the position of the motor on telemetry
            telemetry.addData("Encoder Position", position);
            telemetry.addData("Encoder Revolutions", revolutions);
            telemetry.addData("Encoder Angle (Degrees)", angle);
            telemetry.addData("Encoder Angle - Normalized (Degrees)", angleNormalized);
            telemetry.update();
        }
    }
}
