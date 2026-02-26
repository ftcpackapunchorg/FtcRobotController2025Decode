/*
 * Copyright (c) 2025 FIRST
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.newbot.opmodes.teleop;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotDistanceSensor;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotRGBLightIndicator;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotSimpleLEDLight;
import org.firstinspires.ftc.teamcode.newbot.mechanisms.NewBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.newbot.mechanisms.NewBotLaunchWithStopperMechanism;
import org.firstinspires.ftc.teamcode.newbot.mechanisms.NewBotMecanumDrive;
import org.firstinspires.ftc.teamcode.newbot.sensors.NewBotColorSensor;
import org.firstinspires.ftc.teamcode.newbot.utils.NewBotConstants;

/*
 * This file includes a teleop (driver-controlled) file for the goBILDA® StarterBot for the
 * 2025-2026 FIRST® Tech Challenge season DECODE™. It leverages a differential/Skid-Steer
 * system for robot mobility, one high-speed motor driving two "launcher wheels", and two servos
 * which feed that launcher.
 *
 * Likely the most niche concept we'll use in this example is closed-loop motor velocity control.
 * This control method reads the current speed as reported by the motor's encoder and applies a varying
 * amount of power to reach, and then hold a target velocity. The FTC SDK calls this control method
 * "RUN_USING_ENCODER". This contrasts to the default "RUN_WITHOUT_ENCODER" where you control the power
 * applied to the motor directly.
 * Since the dynamics of a launcher wheel system varies greatly from those of most other FTC mechanisms,
 * we will also need to adjust the "PIDF" coefficients with some that are a better fit for our application.
 */

@TeleOp(name = "NewBot Teleop Main With Stopper OpMode", group = "NewBot")
@Disabled
public class NewBotTeleopMainWithStopperOpMode extends OpMode {

    NewBotMecanumDrive drive;

    NewBotLaunchWithStopperMechanism launchMechanism;

    NewBotIntakeMechanism intakeMechanism;
    MainBotDistanceSensor leftDistanceSensor;
    MainBotDistanceSensor rightDistanceSensor;
    MainBotSimpleLEDLight leftLED;
    MainBotSimpleLEDLight rightLED;

    MainBotRGBLightIndicator artifactIntakeIndicator, isArtifactAllowedIndicator;

    NewBotColorSensor intakeColorSensorArtifact1 = new NewBotColorSensor();
    NewBotColorSensor intakeColorSensorArtifact2 = new NewBotColorSensor();
    NewBotColorSensor intakeColorSensorArtifact3 = new NewBotColorSensor();

    boolean enableDistanceSensors;
    boolean enableRGBLights;
    boolean enableSimpleLEDLights;
    boolean enableIntakeColorSensor;
    int noOfArtifactsInTheRobot = 0;

    boolean isStopperBlockingArtifact = true;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        Pose2d initPose = new Pose2d(NewBotConstants.BLUE_NEAR_INIT_POSE_X, NewBotConstants.BLUE_NEAR_INIT_POSE_Y, NewBotConstants.BLUE_NEAR_INIT_POSE_HEADING_DEGREES);

        /*
         * Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step.
         */
        drive = new NewBotMecanumDrive(hardwareMap, initPose);

        launchMechanism = new NewBotLaunchWithStopperMechanism(hardwareMap, telemetry, "Blue");

        intakeMechanism = new NewBotIntakeMechanism(hardwareMap, telemetry);

        initSensors(hardwareMap, telemetry);

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Initialized");
    }

    private void initSensors(HardwareMap hardwareMap, Telemetry telemetry) {

        // Initiate distance sensors if configured
        try {

            leftDistanceSensor.init(hardwareMap, NewBotConstants.LEFT_DISTANCE_SENSOR_NAME);
            rightDistanceSensor.init(hardwareMap, NewBotConstants.RIGHT_DISTANCE_SENSOR_NAME);
            enableDistanceSensors = true;

        } catch (Exception e) {
            telemetry.addData("Initialization", "No Distance Sensors");
            enableDistanceSensors = false;
        }

        // Initiate Simple LED Lights if configured
        try {

            rightLED.init(hardwareMap, NewBotConstants.LED_NAME_PREFIX_RIGHT);
            leftLED.init(hardwareMap, NewBotConstants.LED_NAME_PREFIX_LEFT);

            enableSimpleLEDLights = true;

            rightLED.setNameOfLED("Right LED");
            leftLED.setNameOfLED("Left LED");
            rightLED.turnLEDOff();
            leftLED.turnLEDOff();

        } catch (Exception e) {
            telemetry.addData("Initialization", "No LED Lights");
            enableSimpleLEDLights = false;
        }

        // Initiate RGB Lights if configured
        try {

            artifactIntakeIndicator = new MainBotRGBLightIndicator();
            isArtifactAllowedIndicator = new MainBotRGBLightIndicator();

            artifactIntakeIndicator.init(hardwareMap, NewBotConstants.ARTIFACT_INTAKE_INDICATOR);
            isArtifactAllowedIndicator.init(hardwareMap, NewBotConstants.ALLOW_ARTIFACT_SERVO_INDICATOR);
            enableRGBLights = true;

        } catch (Exception e) {

            telemetry.addData("Initialization", "No RGB Light Indicators");
            enableRGBLights = false;
        }

        // Initiate Color Sensor if configured
        try {

            intakeColorSensorArtifact1.init(hardwareMap, NewBotConstants.COLOR_SENSOR_ARTIFACT_TOP_1);
            intakeColorSensorArtifact2.init(hardwareMap, NewBotConstants.COLOR_SENSOR_ARTIFACT_MIDDLE_2);
            intakeColorSensorArtifact3.init(hardwareMap, NewBotConstants.COLOR_SENSOR_ARTIFACT_LOW_3);

            enableIntakeColorSensor = true;

        } catch (Exception e) {
            telemetry.addData("Initialization", "No Intake Color Sensor");
            enableRGBLights = false;
        }
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
        drive.mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.left_trigger, telemetry);

        /*
         * Here we give the user control of the speed of the launcher motor without automatically
         * queuing a shot.
         */
        if (gamepad2.rightBumperWasPressed()) {
//            launchMechanism.startLauncher();
//            launchMechanism.launch(true);
            launchMechanism.launch(true, "FAR_ZONE");
        } else if(gamepad2.yWasReleased()) {
//            launchMechanism.startLauncherNearZone();
            launchMechanism.launch(true, "NEAR_ZONE");
        } else if (gamepad2.bWasPressed()) { // stop flywheel
            launchMechanism.stopLauncher();
        } else if(gamepad2.dpad_down) {
            launchMechanism.reverseLauncher();
        }

        /*
         * Here we give the user control of the speed of the launcher motor without automatically
         * queuing a shot.
         */
        if (gamepad2.x) {
            intakeMechanism.startIntake();
        } else if (gamepad2.a) {
            // Stop intake
            intakeMechanism.stopIntake();
        } else if (gamepad2.leftBumperWasPressed()) {
            intakeMechanism.reverseIntake();
        } else if(gamepad2.dpad_right) {
            launchMechanism.reverseLauncher();
            intakeMechanism.startIntake();
        }

        if(gamepad2.right_trigger > 0.0) {
            launchMechanism.blockArtifact();
            isStopperBlockingArtifact = true;
        } else if(gamepad2.left_trigger > 0.0) {
            launchMechanism.allowArtifact();
            isStopperBlockingArtifact = false;
        }

//        intakeMechanism.startIntakeWithInput(gamepad2.right_trigger);

        /*
         * Now we call our "Launch" function.
         */
//        launchMechanism.launch(gamepad2.rightBumperWasPressed(), "FAR_ZONE");

        /*
         * Now we call our "Intake" function.
         */
//        intakeMechanism.intakeAction(gamepad2.leftBumperWasPressed());

        if(enableDistanceSensors) {

            double leftDistance = leftDistanceSensor.getDistance();

            lightUpDistanceBasedLEDs(leftLED, telemetry, leftDistance);

            double rightDistance = rightDistanceSensor.getDistance();

            telemetry.addData("Distance : ", rightDistanceSensor.getDistance());

            lightUpDistanceBasedLEDs(rightLED, telemetry, rightDistance);

        }

        if(enableIntakeColorSensor) {

            intakeColorSensorArtifact1.getDetectedColor(telemetry);
            intakeColorSensorArtifact2.getDetectedColor(telemetry);
            intakeColorSensorArtifact3.getDetectedColor(telemetry);

        }

        if(enableRGBLights) {

            String color = glowColorSensorRGBLight(intakeColorSensorArtifact1, intakeColorSensorArtifact2, intakeColorSensorArtifact3);

            telemetry.addData("Color of RGB Light : ", color);

            if ("GREEN".equals(color)) {
                artifactIntakeIndicator.setRGBLightToGreen();
            } else if ("YELLOW".equals(color)) {
                artifactIntakeIndicator.setRGBLightToYellow();
            } else if ("ORANGE".equals(color)) {
                artifactIntakeIndicator.setRGBLightToOrange();
            } else if ("GREEN".equals(color)) {
                artifactIntakeIndicator.setRGBLightToGreen();
            } else if ("RED".equals(color)) {
                artifactIntakeIndicator.setRGBLightToRed();
            } else {
                artifactIntakeIndicator.setRGBLightToWhite();
            }

            if (isStopperBlockingArtifact) {
                isArtifactAllowedIndicator.setRGBLightToRed();
            } else {
                isArtifactAllowedIndicator.setRGBLightToGreen();
            }
        }

        /*
         * Show the state and motor powers
         */
        telemetry.addData("Launch State", launchMechanism.getLaunchState());
        telemetry.addData("Launcher MotorSpeed", launchMechanism.launcher.getVelocity());

        telemetry.addData("Intake State", intakeMechanism.getIntakeState());
        telemetry.addData("Intake MotorSpeed", intakeMechanism.intake.getPower());

        telemetry.addData("Stopper Blocking Artifact?", isStopperBlockingArtifact);
//        telemetry.update();

    }


    private boolean hasColorSensorDetectedAnArtifact(NewBotColorSensor colorSensor) {

        boolean artifactDetected;

        if(enableIntakeColorSensor && (colorSensor.getDetectedColor(telemetry).equals(NewBotColorSensor.DetectedColor.GREEN) ||
                colorSensor.getDetectedColor(telemetry).equals(NewBotColorSensor.DetectedColor.PURPLE))) {

            artifactDetected = true;

        } else {

            artifactDetected = false;
        }

        return artifactDetected;
    }

    private String glowColorSensorRGBLight(NewBotColorSensor intakeColorSensorArtifact1, NewBotColorSensor intakeColorSensorArtifact2, NewBotColorSensor intakeColorSensorArtifact3) {

        String colorToSet;

        boolean artifactOneDetected = hasColorSensorDetectedAnArtifact(intakeColorSensorArtifact1);
        boolean artifactTwoDetected = hasColorSensorDetectedAnArtifact(intakeColorSensorArtifact2);
        boolean artifactThreeDetected = hasColorSensorDetectedAnArtifact(intakeColorSensorArtifact3);

        if (artifactOneDetected && artifactTwoDetected && artifactThreeDetected) {
            // All three artifacts are detected
            colorToSet = "GREEN";
        } else if (artifactOneDetected && (artifactTwoDetected || artifactThreeDetected)) {
            // Two artifacts are detected
            colorToSet = "ORANGE";
        } else if (artifactTwoDetected && (artifactOneDetected || artifactThreeDetected)) {
            // Two artifacts are detected
            colorToSet = "ORANGE";
        } else if (artifactThreeDetected && (artifactTwoDetected || artifactOneDetected)) {
            // Two artifacts are detected
            colorToSet = "ORANGE";
        } else if (artifactOneDetected && !(artifactTwoDetected || artifactThreeDetected)) {
            // One artifact detected
            colorToSet = "YELLOW";
        } else if (artifactTwoDetected && !(artifactOneDetected || artifactThreeDetected)) {
            // One artifact detected
            colorToSet = "YELLOW";
        } else if (artifactThreeDetected && !(artifactTwoDetected || artifactOneDetected)) {
            // One artifact detected
            colorToSet = "YELLOW";
        } else {
            colorToSet = "RED";
        }

        return colorToSet;
    }

    private void lightUpDistanceBasedLEDs(MainBotSimpleLEDLight ledInput, Telemetry telemetry, double distance) {

        if(distance < 15) {

            ledInput.turnLEDToRed();

            telemetry.addData("Too close", ledInput.getNameOfLED());

        } else if(distance >= 15 && distance <= 30) {

            ledInput.turnLEDToAmber();

            telemetry.addData("Watch out", ledInput.getNameOfLED());

        }
        else {

            ledInput.turnLEDToGreen();

            telemetry.addData("Safe distance", ledInput.getNameOfLED());

        }

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}