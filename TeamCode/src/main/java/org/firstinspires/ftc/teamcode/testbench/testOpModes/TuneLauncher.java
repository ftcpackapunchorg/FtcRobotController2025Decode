package org.firstinspires.ftc.teamcode.testbench.testOpModes;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;


@TeleOp(name="Tune Launcher", group="Linear OpMode")
public class TuneLauncher extends LinearOpMode {

    DcMotorEx motor;
    double power = 1;

    double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};

    int stepIndex = 1;

    double highVelocity = 1500;

    double lowVelovity = 450;

    double curTargetVelocity = highVelocity;

    double P = 0;

    double F = 0;

    @Override
    public void runOpMode() throws InterruptedException {

        motor = hardwareMap.get(DcMotorEx.class, "launcher");
//        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(0, 0, 0, 0);

        motor.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);
//        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        telemetry.addLine("Init Complete");

        waitForStart();

        while (opModeIsActive()) {

           if(gamepad1.yWasPressed()) {

               if(curTargetVelocity == highVelocity) {
                   curTargetVelocity = lowVelovity;
               } else {
                   curTargetVelocity = highVelocity;
               }
           }

           if(gamepad1.bWasPressed()) {

               stepIndex = (stepIndex + 1) % stepSizes.length;
           }

           if(gamepad1.dpadLeftWasPressed()) {

               F += stepSizes[stepIndex];
           }

            if(gamepad1.dpadRightWasPressed()) {

                F -= stepSizes[stepIndex];
            }

            if(gamepad1.dpadUpWasPressed()) {

                P += stepSizes[stepIndex];
            }

            if(gamepad1.dpadDownWasPressed()) {

                P -= stepSizes[stepIndex];
            }

            PIDFCoefficients pidfCoefficients1 = new PIDFCoefficients(P, 0, 0, F);
            motor.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients1);

            motor.setVelocity(curTargetVelocity);

            double curVelocity = motor.getVelocity();
            double error = curTargetVelocity - curVelocity;

            telemetry.addData("Target Velocity : ", curTargetVelocity);
            telemetry.addData("Current Velocity : ", curVelocity);
            telemetry.addData("Error : ", error);
            telemetry.addLine("-----------------------------");
            telemetry.addData("Tuning P : "," %.4f (D-Pad U/D)", P); // -2
            telemetry.addData("Tuning F : "," %.4f (D-Pad L/R)", F); // -12.8, 1
            telemetry.addData("Step Size : "," %.4f (B Button)", stepSizes[stepIndex]);
            telemetry.update();


        }
    }
}
