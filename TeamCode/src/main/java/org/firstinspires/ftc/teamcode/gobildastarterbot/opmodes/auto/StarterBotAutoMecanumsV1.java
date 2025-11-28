package org.firstinspires.ftc.teamcode.gobildastarterbot.opmodes.auto;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.gobildastarterbot.mechanicals.StarterBotLaunchMechanism;

@Autonomous
@Disabled
public class StarterBotAutoMecanumsV1 extends OpMode {

    final double DRIVE_SPEED = 0.5;
    final double ROTATE_SPEED = 0.2;
    final double WHEEL_DIAMETER_MM = 96;
    final double ENCODER_TICKS_PER_REV = 537.7;
    final double TICKS_PER_MM = (ENCODER_TICKS_PER_REV / (WHEEL_DIAMETER_MM * Math.PI));
    final double TRACK_WIDTH_MM = 404;

    int shotsToFire = 3;
    double robotRotationAngle = 45;

    private MecanumDrive drive;

    private StarterBotLaunchMechanism launchMechanism;

    private ElapsedTime driveTimer = new ElapsedTime();


    private enum AutonomousState {
        LAUNCH,
        WAIT_FOR_LAUNCH,
        DRIVING_AWAY_FROM_GOAL,
        ROTATING,
        DRIVING_OFF_LINE,
        STRAFE_RIGHT_4,
        ROTATE_NEG_90,
        DRIVE_FORWARD_5,
        DRIVE_BACK_5,
        ROTATE_NEG_80,
        DRIVE_FORWARD_5_FINAL,
        COMPLETE
    }
    private AutonomousState autonomousState;

    private enum Alliance { RED, BLUE }
    private Alliance alliance = Alliance.RED;

    @Override
    public void init() {
        autonomousState = AutonomousState.LAUNCH;
        Pose2d initPose = new Pose2d(-43,43,0);
        drive = new MecanumDrive(hardwareMap, initPose);
        launchMechanism = new StarterBotLaunchMechanism(hardwareMap, telemetry);
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void init_loop() {
        launchMechanism.getFeederMechanism().rightFeeder.setPower(0);
        launchMechanism.getFeederMechanism().leftFeeder.setPower(0);

        if (gamepad1.b) alliance = Alliance.RED;
        else if (gamepad1.x) alliance = Alliance.BLUE;

        telemetry.addData("Press X", "for BLUE");
        telemetry.addData("Press B", "for RED");
        telemetry.addData("Selected Alliance", alliance);
    }

    @Override
    public void start() {}

    @Override
    public void loop() {
        switch (autonomousState) {
            case LAUNCH:
                launchMechanism.launchForAuto(true);
                autonomousState = AutonomousState.WAIT_FOR_LAUNCH;
                break;

            case WAIT_FOR_LAUNCH:
                if (launchMechanism.launchForAuto(false)) {
                    shotsToFire--;
                    if (shotsToFire > 0) autonomousState = AutonomousState.LAUNCH;
                    else {
                        resetDriveEncoders();
                        launchMechanism.launcher.setVelocity(0);
                        autonomousState = AutonomousState.DRIVING_AWAY_FROM_GOAL;
                    }
                }
                break;

            case DRIVING_AWAY_FROM_GOAL:
                if (drive(DRIVE_SPEED, -12, DistanceUnit.INCH, 1)) {
                    resetDriveEncoders();
                    autonomousState = AutonomousState.ROTATING;
                }
                break;

            case ROTATING:
                robotRotationAngle = (alliance == Alliance.RED) ? 45 : -45;
                if (rotate(ROTATE_SPEED, robotRotationAngle, AngleUnit.DEGREES, 1)) {
                    resetDriveEncoders();
                    autonomousState = AutonomousState.DRIVING_OFF_LINE;
                }
                break;

            case DRIVING_OFF_LINE:
                if (drive(DRIVE_SPEED, -38, DistanceUnit.INCH, 1)) {
                    resetDriveEncoders();
                    autonomousState = AutonomousState.STRAFE_RIGHT_4;
                }
                break;

            case STRAFE_RIGHT_4:
                if (drive(DRIVE_SPEED, 4, DistanceUnit.INCH, 1, true)) {
                    resetDriveEncoders();
                    autonomousState = AutonomousState.ROTATE_NEG_90;
                }
                break;

            case ROTATE_NEG_90:
                if (rotate(ROTATE_SPEED, -90, AngleUnit.DEGREES, 1)) {
                    resetDriveEncoders();
                    autonomousState = AutonomousState.DRIVE_FORWARD_5;
                }
                break;

            case DRIVE_FORWARD_5:
                if (drive(DRIVE_SPEED, 5, DistanceUnit.INCH, 1)) {
                    resetDriveEncoders();
                    autonomousState = AutonomousState.DRIVE_BACK_5;
                }
                break;

            case DRIVE_BACK_5:
                if (drive(DRIVE_SPEED, -5, DistanceUnit.INCH, 1)) {
                    resetDriveEncoders();
                    autonomousState = AutonomousState.ROTATE_NEG_80;
                }
                break;

            case ROTATE_NEG_80:
                if (rotate(ROTATE_SPEED, -80, AngleUnit.DEGREES, 1)) {
                    resetDriveEncoders();
                    autonomousState = AutonomousState.DRIVE_FORWARD_5_FINAL;
                }
                break;

            case DRIVE_FORWARD_5_FINAL:
                if (drive(DRIVE_SPEED, 5, DistanceUnit.INCH, 1)) {
                    autonomousState = AutonomousState.COMPLETE;
                }
                break;

            case COMPLETE:
                break;
        }

        telemetry.addData("AutoState", autonomousState);
        telemetry.addData("LauncherState", launchMechanism.getAutoLaunchState());
        telemetry.addData("Motor Positions", "LF: %d, RF: %d, LB: %d, RB: %d",
                drive.leftFront.getCurrentPosition(), drive.rightFront.getCurrentPosition(),
                drive.leftBack.getCurrentPosition(), drive.rightBack.getCurrentPosition());
        telemetry.addData("Motor Targets", "LF: %d, RF: %d, LB: %d, RB: %d",
                drive.leftFront.getTargetPosition(), drive.rightFront.getTargetPosition(),
                drive.leftBack.getTargetPosition(), drive.rightBack.getTargetPosition());
        telemetry.update();
    }

    private void resetDriveEncoders() {
        drive.leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drive.rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drive.leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drive.rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    boolean drive(double speed, double distance, DistanceUnit unit, double holdSeconds) {
        return drive(speed, distance, unit, holdSeconds, false);
    }

    boolean drive(double speed, double distance, DistanceUnit unit, double holdSeconds, boolean strafe) {
        final double TOLERANCE_MM = 10;
        double target = unit.toMm(distance) * TICKS_PER_MM;

        if (!strafe) {
            drive.leftFront.setTargetPosition((int) target);
            drive.rightFront.setTargetPosition((int) target);
            drive.leftBack.setTargetPosition((int) target);
            drive.rightBack.setTargetPosition((int) target);
        } else {
            // Strafe right = LF+, RF-, LB-, RB+
            drive.leftFront.setTargetPosition((int) target);
            drive.rightFront.setTargetPosition((int) -target);
            drive.leftBack.setTargetPosition((int) -target);
            drive.rightBack.setTargetPosition((int) target);
        }

        drive.leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        drive.rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        drive.leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        drive.rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        drive.leftFront.setPower(speed);
        drive.rightFront.setPower(speed);
        drive.leftBack.setPower(speed);
        drive.rightBack.setPower(speed);

        if (Math.abs(drive.leftFront.getTargetPosition() - drive.leftFront.getCurrentPosition()) > (TOLERANCE_MM * TICKS_PER_MM)) {
            driveTimer.reset();
        }

        return (driveTimer.seconds() > holdSeconds);
    }

    boolean rotate(double speed, double angle, AngleUnit unit, double holdSeconds) {
        final double TOLERANCE_MM = 10;
        double targetMm = unit.toRadians(angle) * (TRACK_WIDTH_MM / 2);
        double leftTarget = -targetMm * TICKS_PER_MM;
        double rightTarget = targetMm * TICKS_PER_MM;

        drive.leftFront.setTargetPosition((int) leftTarget);
        drive.rightFront.setTargetPosition((int) rightTarget);
        drive.leftBack.setTargetPosition((int) leftTarget);
        drive.rightBack.setTargetPosition((int) rightTarget);

        drive.leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        drive.rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        drive.leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        drive.rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        drive.leftFront.setPower(speed);
        drive.rightFront.setPower(speed);
        drive.leftBack.setPower(speed);
        drive.rightBack.setPower(speed);

        if (Math.abs(drive.leftFront.getTargetPosition() - drive.leftFront.getCurrentPosition()) > (TOLERANCE_MM * TICKS_PER_MM)) {
            driveTimer.reset();
        }

        return (driveTimer.seconds() > holdSeconds);
    }

    @Override
    public void stop() {}
}