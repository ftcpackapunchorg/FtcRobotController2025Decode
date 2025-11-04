package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;


@TeleOp(name = "Dual Gamepad TeleOp", group = "Main")
public class Tutorial extends LinearOpMode {

    // --- Drive Motors ---
    private DcMotor frontLeft, frontRight, backLeft, backRight;

    // --- Launcher Motor ---
    private DcMotorEx launcher;

    // --- Constants ---
    private static final double STRAFE_SPEED = 0.6;
    private static final double DRIVE_SPEED = 0.8;
    private static final double STOP_SPEED = 0;
    private static final double LAUNCHER_TARGET_VELOCITY = 1800; // adjust for your motor

    // --- Variables ---
    private String launchState = "Stopped";

    @Override
    public void runOpMode() {
        // Initialize hardware
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        launcher = hardwareMap.get(DcMotorEx.class, "launcher");

        // Set motor directions (adjust as needed)
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // Stop all motors
        stopAllMotors();

        telemetry.addLine("Ready to start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // --- Gamepad 1 Controls ---
            handleShooter(gamepad1);
            handleStrafing(gamepad1);

            // --- Gamepad 2 Controls ---
            handleDriving(gamepad2);

            // --- Telemetry ---
            telemetry.addData("State", launchState);
            telemetry.addData("Motors", "left (%.2f), right (%.2f)",
                    frontLeft.getPower(), frontRight.getPower());
            telemetry.addData("motorSpeed", launcher.getVelocity());
            telemetry.update();
        }

        stopAllMotors();
    }

    // ================= Helper Functions ================= //

    private void handleShooter(Gamepad gp) {
        if (gp.y) {
            launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
            launchState = "Spinning Up";
        } else if (gp.b) {
            launcher.setVelocity(STOP_SPEED);
            launchState = "Stopped";
        }

        if (gp.right_bumper) {
            launch(true);
        }
    }

    private void handleStrafing(Gamepad gp) {
        if (gp.dpad_left) {
            strafeLeft(STRAFE_SPEED);
        } else if (gp.dpad_right) {
            strafeRight(STRAFE_SPEED);
        } else {
            stopStrafe();
        }
    }

    private void handleDriving(Gamepad gp) {
        double drive = -gp.left_stick_y * DRIVE_SPEED;
        double turn = gp.right_stick_x * DRIVE_SPEED;

        double leftPower = drive + turn;
        double rightPower = drive - turn;

        frontLeft.setPower(leftPower);
        backLeft.setPower(leftPower);
        frontRight.setPower(rightPower);
        backRight.setPower(rightPower);
    }

    private void strafeLeft(double speed) {
        frontLeft.setPower(-speed);
        frontRight.setPower(speed);
        backLeft.setPower(speed);
        backRight.setPower(-speed);
    }

    private void strafeRight(double speed) {
        frontLeft.setPower(speed);
        frontRight.setPower(-speed);
        backLeft.setPower(-speed);
        backRight.setPower(speed);
    }

    private void stopStrafe() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    private void stopAllMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        launcher.setVelocity(0);
    }

    private void launch(boolean trigger) {
        if (trigger) {
            // Example: perform a quick power pulse to launch
            launcher.setPower(1.0);
            sleep(300); // adjust timing for your launcher
            launcher.setPower(0.8);
            launchState = "Launched";
        }
    }
}


