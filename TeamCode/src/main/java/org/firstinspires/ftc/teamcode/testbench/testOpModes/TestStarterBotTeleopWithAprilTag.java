package org.firstinspires.ftc.teamcode.testbench.testOpModes;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.gobildastarterbot.mechanicals.StarterBotLaunchMechanism;
import org.firstinspires.ftc.teamcode.testbench.sensors.CameraConfigAndControls;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp(name = "TestStarterBotTeleopWithAprilTag", group = "StarterBot")
@Disabled
public class TestStarterBotTeleopWithAprilTag extends OpMode {

    private CameraConfigAndControls cameraConfigAndControls = new CameraConfigAndControls();

    ElapsedTime feederTimer = new ElapsedTime();

    MecanumDrive drive;

    StarterBotLaunchMechanism launchMechanism;

    // Setup a variable for each drive wheel to save power level for telemetry
    double leftFrontPower;
    double rightFrontPower;
    double leftBackPower;
    double rightBackPower;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        cameraConfigAndControls.initAprilTag(hardwareMap, telemetry);

        Pose2d initPose = new Pose2d(-43,43,0);

        /*
         * Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step.
         */
        drive = new MecanumDrive(hardwareMap, initPose);
        launchMechanism = new StarterBotLaunchMechanism(hardwareMap, telemetry);

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Initialized");

        // Wait for the DS start button to be touched.
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch START to start OpMode");
        telemetry.update();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits START
     */
    @Override
    public void start() {
    }

    /*
     * Code to run REPEATEDLY after the driver hits START but before they hit STOP
     */
    @Override
    public void loop() {
        /*
         * Here we call a function called arcadeDrive. The arcadeDrive function takes the input from
         * the joysticks, and applies power to the left and right drive motor to move the robot
         * as requested by the driver. "arcade" refers to the control style we're using here.
         * Much like a classic arcade game, when you move the left joystick forward both motors
         * work to drive the robot forward, and when you move the right joystick left and right
         * both motors work to rotate the robot. Combinations of these inputs can be used to create
         * more complex maneuvers.
         */
        mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        /*
         * Here we give the user control of the speed of the launcher motor without automatically
         * queuing a shot.
         */
        if (gamepad2.y) {
            launchMechanism.startLauncher();
        } else if (gamepad2.b) { // stop flywheel
            launchMechanism.stopLauncher();
        }

//        if (gamepad1.left_stick_button) {
//            drive.rightBack.setPower(1);
//            drive.leftBack.setPower(1);
//        }

        /*
         * Now we call our "Launch" function.
         */
        launchMechanism.launch(gamepad2.rightBumperWasPressed());

        cameraConfigAndControls.update();
        AprilTagDetection blueId = cameraConfigAndControls.getTagBySpecificID(20);
        if(blueId != null) {
            cameraConfigAndControls.displayDetectionTelemetry(blueId);
            telemetry.addData("Decode Blue April Tag : ", blueId.toString());
        } else {
            telemetry.addData("Null Decode Blue April Tag : ", "Blue Id is not detected");
        }

        AprilTagDetection redId = cameraConfigAndControls.getTagBySpecificID(24);
        if(redId != null) {
            cameraConfigAndControls.displayDetectionTelemetry(redId);
            telemetry.addData("Decode Red April Tag : ", redId.toString());
        } else {
            telemetry.addData("Null Decode Red April Tag : ", "Red Id is not detected");
        }

        /*
         * Show the state and motor powers
         */
        telemetry.addData("State", launchMechanism.getLaunchState());
        telemetry.addData("motorSpeed", launchMechanism.launcher.getVelocity());

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }

    /*
     * Remember, Y stick value is reversed
     * Counteract imperfect strafing
     *
     * forward = -gamepad1.left_stick_y
     * strafe = gamepad1.left_stick_x
     * rotate = gamepad1.right_stick_x
     */
    void mecanumDrive(double forward, double strafe, double rotate){

        /* the denominator is the largest motor power (absolute value) or 1
         * This ensures all the powers maintain the same ratio,
         * but only if at least one is out of the range [-1, 1]
         */
        double speed = 2.5;
        if(gamepad1.left_trigger > 0.1){
            speed = 1.1;
        }

        double denominator = Math.max(Math.abs(forward) + Math.abs(strafe) + Math.abs(rotate), speed);

        leftFrontPower = (forward + strafe + rotate) / denominator;
        rightFrontPower = (forward - strafe - rotate) / denominator;
        leftBackPower = (forward - strafe + rotate) / denominator;
        rightBackPower = (forward + strafe - rotate) / denominator;

        drive.leftFront.setPower(leftFrontPower);
        drive.rightFront.setPower(rightFrontPower);
        drive.leftBack.setPower(leftBackPower);
        drive.rightBack.setPower(rightBackPower);

    }

}