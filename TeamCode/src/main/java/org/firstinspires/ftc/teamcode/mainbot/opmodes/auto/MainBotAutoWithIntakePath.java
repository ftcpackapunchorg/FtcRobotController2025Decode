package org.firstinspires.ftc.teamcode.mainbot.opmodes.auto;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.gobildastarterbot.mechanicals.StarterBotLaunchMechanism;


@Autonomous(name="MainBotAutoWithIntakePath")
public class MainBotAutoWithIntakePath extends LinearOpMode {
    MecanumDrive drive;

    StarterBotLaunchMechanism launchMechanism;

    /*
     * The number of seconds that we wait between each of our 3 shots from the launcher. This
     * can be much shorter, but the longer break is reasonable since it maximizes the likelihood
     * that each shot will score.
     */

    int shotsToFire = 3; //The number of shots to fire in this auto.

    double robotRotationAngle = 45;

    /*
     * Here we capture a few variables used in driving the robot. DRIVE_SPEED and ROTATE_SPEED
     * are from 0-1, with 1 being full speed. Encoder ticks per revolution is specific to the motor
     * ratio that we use in the kit; if you're using a different motor, this value can be found on
     * the product page for the motor you're using.
     * Track width is the distance between the center of the drive wheels on either side of the
     * robot. Track width is used to determine the amount of linear distance each wheel needs to
     * travel to create a specified rotation of the robot.
     */

    final double DRIVE_SPEED = 0.5;
    final double ROTATE_SPEED = 0.2;


    /*
     * Here is our auto state machine enum. This captures each action we'd like to do in auto.
     */
    private enum AutonomousState {
        LAUNCH,
        WAIT_FOR_LAUNCH,
        GO_TO_INTAKE,
        GO_TO_INTAKE2,
        GO_TO_LAUNCH_ZONE,
        GO_TO_LAUNCH_ZONE2,
        DRIVING_AWAY_FROM_GOAL,
        ROTATING,
        DRIVING_OFF_LINE,
        COMPLETE
    }

    private AutonomousState autonomousState;

    /*
     * Here we create an enum not to create a state machine, but to capture which alliance we are on.
     */
    private enum Alliance {
        RED,
        BLUE
    }

    /*
     * When we create the instance of our enum we can also assign a default state.
     */
    private Alliance alliance = Alliance.RED;

    Pose2d initPose;

    TrajectoryActionBuilder goToIntake;

    TrajectoryActionBuilder goToLaunchZone;
    TrajectoryActionBuilder goToIntake2;
    TrajectoryActionBuilder goToLaunchZone2;



    /*
     * This code runs ONCE when the driver hits INIT.
     */
    @Override
    public void runOpMode() throws InterruptedException {
        /*
         * Here we set the first step of our autonomous state machine by setting autoStep = AutoStep.LAUNCH.
         * Later in our code, we will progress through the state machine by moving to other enum members.
         * We do the same for our launcher state machine, setting it to IDLE before we use it later.
         */
//        autonomousState = AutonomousState.LAUNCH;
        initPose = new Pose2d(-48, -48, Math.toRadians(-135));

        drive = new MecanumDrive(hardwareMap, initPose);

//        launchMechanism = new StarterBotLaunchMechanism(hardwareMap, telemetry);
        ;
        goToIntake = drive.actionBuilder(initPose)
                .waitSeconds(1)
                .turn(Math.toRadians(135))
                .strafeTo(new Vector2d(-24, -24))
                .strafeTo(new Vector2d(-12, -24))
                .turn(Math.toRadians(-90))
                .strafeTo(new Vector2d(-12, -40));

        goToLaunchZone = goToIntake.endTrajectory().fresh()
                .turn(Math.toRadians(180))
                .strafeTo(new Vector2d(-12, -24))
                .strafeTo(new Vector2d(-24, -24))
                .turn(Math.toRadians(-45))
                .strafeTo(new Vector2d(-43, -43));

        goToIntake2 = goToLaunchZone.endTrajectory().fresh()
                .turn(Math.toRadians(135))
                .strafeTo(new Vector2d(-24, -24))
                .strafeTo(new Vector2d(12, -24))
                .turn(Math.toRadians(-90))
                .strafeTo(new Vector2d(12, -40));

        goToLaunchZone2 = goToIntake2.endTrajectory().fresh()

                .turn(Math.toRadians(180))
                .strafeTo(new Vector2d(12, -24))
                .strafeTo(new Vector2d(-24, -24))
                .turn(Math.toRadians(-45))
                .strafeTo(new Vector2d(-43, -43));


        waitForStart();
        if (isStopRequested()) return;

        Actions.runBlocking(goToIntake.build());

    }
        // Tell the driver that initialization is complete.
//        telemetry.addData("Status", "Initialized");
    }

    /*
     * This code runs REPEATEDLY after the driver hits INIT, but before they hit START.
     */
//    @Override
//    public void init_loop() {
//        /*
//         * We also set the servo power to 0 here to make sure that the servo controller is booted
//         * up and ready to go.
//         */
//        launchMechanism.getFeederMechanism().rightFeeder.setPower(0);
//        launchMechanism.getFeederMechanism().leftFeeder.setPower(0);
//
//        /*
//         * Here we allow the driver to select which alliance we are on using the gamepad.
//         */
//        if (gamepad1.b) {
//            alliance = Alliance.RED;
//        } else if (gamepad1.x) {
//            alliance = Alliance.BLUE;
//        }
//
//        telemetry.addData("Press X", "for BLUE");
//        telemetry.addData("Press B", "for RED");
//        telemetry.addData("Selected Alliance", alliance);
//    }
//
//    /*
//     * This code runs ONCE when the driver hits START.
//     */
//    @Override
//    public void start() {
//    }
//
//    /*
//     * This code runs REPEATEDLY after the driver hits START but before they hit STOP.
//     */
//    @Override
//    public void loop() {
//        /*
//         * TECH TIP: Switch Statements
//         * switch statements are an excellent way to take advantage of an enum. They work very
//         * similarly to a series of "if" statements, but allow for cleaner and more readable code.
//         * We switch between each enum member and write the code that should run when our enum
//         * reflects that state. We end each case with "break" to skip out of checking the rest
//         * of the members of the enum for a match, since if we find the "break" line in one case,
//         * we know our enum isn't reflecting a different state.
//         */
//        switch (autonomousState){
//            /*
//             * Since the first state of our auto is LAUNCH, this is the first "case" we encounter.
//             * This case is very simple. We call our .launch() function with "true" in the parameter.
//             * This "true" value informs our launch function that we'd like to start the process of
//             * firing a shot. We will call this function with a "false" in the next case. This
//             * "false" condition means that we are continuing to call the function every loop,
//             * allowing it to cycle through and continue the process of launching the first ball.
//             */
//            case LAUNCH:
//                launchMechanism.launchForAuto(true);
//                autonomousState = AutonomousState.WAIT_FOR_LAUNCH;
//                break;
//
//            case WAIT_FOR_LAUNCH:
//                /*
//                 * A technique we leverage frequently in this code are functions which return a
//                 * boolean. We are using this function in two ways. This function actually moves the
//                 * motors and servos in a way that launches the ball, but it also "talks back" to
//                 * our main loop by returning either "true" or "false". We've written it so that
//                 * after the shot we requested has been fired, the function will return "true" for
//                 * one cycle. Once the launch function returns "true", we proceed in the code, removing
//                 * one from the shotsToFire variable. If shots remain, we move back to the LAUNCH
//                 * state on our state machine. Otherwise, we reset the encoders on our drive motors
//                 * and move onto the next state.
//                 */
//                if(launchMechanism.launchForAuto(false)) {
//                    shotsToFire -= 1;
//                    if(shotsToFire > 0) {
//                        autonomousState = AutonomousState.LAUNCH;
//                    } else {
//                        drive.leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//                        drive.rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//                        launchMechanism.launcher.setVelocity(0);
//                        autonomousState = AutonomousState.GO_TO_INTAKE;
//                    }
//                }
//                break;
//            case GO_TO_INTAKE:
//                Actions.runBlocking(goToIntake.build());
//                autonomousState = autonomousState.GO_TO_INTAKE;
//
////            case DRIVING_AWAY_FROM_GOAL:
////                /*
////                 * This is another function that returns a boolean. This time we return "true" if
////                 * the robot has been within a tolerance of the target position for "holdSeconds."
////                 * Once the function returns "true" we reset the encoders again and move on.
////                 */
//////                if(drive.drive(DRIVE_SPEED, -4, DistanceUnit.INCH, 1)){
//////                    drive.leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//////                    drive.rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//////                    autonomousState = AutonomousState.ROTATING;
//////                }
////
//                Actions.runBlocking(goToIntake.build());
//                autonomousState = autonomousState.GO_TO_LAUNCH_ZONE;
////                autonomousState = StarterBotRRBlueAutoWithIntake.AutonomousState.COMPLETE;
//                break;
////
//            case GO_TO_LAUNCH_ZONE:
//                Actions.runBlocking(goToLaunchZone.build());
//                autonomousState = autonomousState.GO_TO_INTAKE2;
//                break;
//
//            case GO_TO_INTAKE2:
//                Actions.runBlocking(goToIntake2.build());
//                autonomousState = autonomousState.GO_TO_LAUNCH_ZONE;
//                break;
//
//            case GO_TO_LAUNCH_ZONE2:
//                Actions.runBlocking(goToLaunchZone2.build());
//                autonomousState = autonomousState.COMPLETE;
//                break;
//
//            case ROTATING:
//                if(alliance == Alliance.RED){
//                    robotRotationAngle = 45;
//                } else if (alliance == Alliance.BLUE){
//                    robotRotationAngle = -45;
//                }
//
//                if(drive.rotate(ROTATE_SPEED, robotRotationAngle, AngleUnit.DEGREES,1)){
//                    drive.leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//                    drive.rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//                    autonomousState = AutonomousState.DRIVING_OFF_LINE;
//                }
//                break;
//
//            case DRIVING_OFF_LINE:
//                if(drive.drive(DRIVE_SPEED, -26, DistanceUnit.INCH, 1)){
//                    autonomousState = AutonomousState.COMPLETE;
//                }
//                break;
//        }

        /*
         * Here is our telemetry that keeps us informed of what is going on in the robot. Since this
         * part of the code exists outside of our switch statement, it will run once every loop.
         * No matter what state our robot is in. This is the huge advantage of using state machines.
         * We can have code inside of our state machine that runs only when necessary, and code
         * after the last "case" that runs every loop. This means we can avoid a lot of
         * "copy-and-paste" that non-state machine autonomous routines fall into.
         */
//        telemetry.addData("AutoState", autonomousState);
//        telemetry.addData("Motor Current Positions", "left (%d), right (%d)",
//                drive.leftFront.getCurrentPosition(), drive.rightFront.getCurrentPosition(),
//                drive.leftBack.getCurrentPosition(), drive.rightBack.getCurrentPosition());
//        telemetry.addData("Motor Target Positions", "left (%d), right (%d)",
//                drive.leftFront.getTargetPosition(), drive.rightFront.getTargetPosition(),
//                drive.leftBack.getTargetPosition(), drive.rightBack.getTargetPosition());
//        telemetry.update();
//    }

    /*
     * This code runs ONCE after the driver hits STOP.
     */
//    @Override
//    public void stop() {
//    }
