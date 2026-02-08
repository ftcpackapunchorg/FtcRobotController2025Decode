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

package org.firstinspires.ftc.teamcode.mainbot.opmodes.teleop;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotIntakeMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchWithFeederMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotMecanumDrive;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotColorSensor;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotDistanceSensor;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotRGBLightIndicator;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotSimpleLEDLight;
import org.firstinspires.ftc.teamcode.mainbot.utils.MainBotConstants;
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

@TeleOp(name = "MainBot Teleop Main With Stopper", group = "MainBot")
public class MainBotTeleopMainWithStopper extends OpMode {

    MainBotMecanumDrive drive;

    MainBotLaunchWithFeederMechanism launchMechanism;

    MainBotIntakeMechanism intakeMechanism;

    MainBotDistanceSensor leftDistanceSensor;
    MainBotDistanceSensor rightDistanceSensor;
    MainBotSimpleLEDLight leftLED;
    MainBotSimpleLEDLight rightLED;
//    MainBotRGBLightIndicator firstRGBLightIndicator;
//    MainBotRGBLightIndicator secondRGBLightIndicator;
//    MainBotRGBLightIndicator thirdRGBLightIndicator;

    MainBotRGBLightIndicator artifactIntakeIndicator, isArtifactAllowedIndicator;

    MainBotColorSensor intakeColorSensorArtifact1 = new MainBotColorSensor();
    MainBotColorSensor intakeColorSensorArtifact2 = new MainBotColorSensor();

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

        Pose2d initPose = new Pose2d(MainBotConstants.BLUE_NEAR_INIT_POSE_X, MainBotConstants.BLUE_NEAR_INIT_POSE_Y, MainBotConstants.BLUE_NEAR_INIT_POSE_HEADING_DEGREES);

        /*
         * Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step.
         */
        drive = new MainBotMecanumDrive(hardwareMap, initPose);

        launchMechanism = new MainBotLaunchWithFeederMechanism(hardwareMap, telemetry, "Blue");

        intakeMechanism = new MainBotIntakeMechanism(hardwareMap, telemetry);

        initSensors(hardwareMap, telemetry);

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Initialized");
    }

    private void initSensors(HardwareMap hardwareMap, Telemetry telemetry) {

        // Initiate distance sensors if configured
        try {

            leftDistanceSensor.init(hardwareMap, MainBotConstants.LEFT_DISTANCE_SENSOR_NAME);
            rightDistanceSensor.init(hardwareMap, MainBotConstants.RIGHT_DISTANCE_SENSOR_NAME);
            enableDistanceSensors = true;

        } catch (Exception e) {
            telemetry.addData("Initialization", "No Distance Sensors");
            enableDistanceSensors = false;
        }

        // Initiate Simple LED Lights if configured
        try {

            rightLED.init(hardwareMap, MainBotConstants.LED_NAME_PREFIX_RIGHT);
            leftLED.init(hardwareMap, MainBotConstants.LED_NAME_PREFIX_LEFT);

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

//            firstRGBLightIndicator.init(hardwareMap, MainBotConstants.FIRST_RGB_LIGHT_INDICATOR_NAME);
//            secondRGBLightIndicator.init(hardwareMap, MainBotConstants.SECOND_RGB_LIGHT_INDICATOR_NAME);
//            thirdRGBLightIndicator.init(hardwareMap, MainBotConstants.THIRD_RGB_LIGHT_INDICATOR_NAME);
            artifactIntakeIndicator.init(hardwareMap, MainBotConstants.ARTIFACT_INTAKE_INDICATOR);
            isArtifactAllowedIndicator.init(hardwareMap, MainBotConstants.ALLOW_ARTIFACT_SERVO_INDICATOR);
            enableRGBLights = true;

        } catch (Exception e) {
            telemetry.addData("Initialization", "No RGB Light Indicators");
            enableRGBLights = false;
        }

        // Initiate Color Sensor if configured
        try {

            intakeColorSensorArtifact1.init(hardwareMap, NewBotConstants.COLOR_SENSOR_ARTIFACT_TOP_1);
            intakeColorSensorArtifact2.init(hardwareMap, NewBotConstants.COLOR_SENSOR_ARTIFACT_MIDDLE_2);

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

        }

        if(enableRGBLights) {

            String color = glowColorSensorRGBLight(intakeColorSensorArtifact1, intakeColorSensorArtifact2);

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


//            if(enableIntakeColorSensor && (intakeColorSensor.getDetectedColor(telemetry).equals(MainBotColorSensor.DetectedColor.GREEN) ||
//                    intakeColorSensor.getDetectedColor(telemetry).equals(MainBotColorSensor.DetectedColor.PURPLE))) {
//
//                telemetry.addData("Detected color : ", intakeColorSensor.getDetectedColor(telemetry));

//            if(intakeColorSensor.getDetectedColor(telemetry) <= 4) {
//
//                telemetry.addData("Distance : ", intakeColorSensor.getDetectedColor(telemetry));

                artifactIntakeIndicator.setRGBLightToGreen();

//                    if(noOfArtifactsInTheRobot == 0) {
//                        firstRGBLightIndicator.setRGBLightToGreen();
//                        noOfArtifactsInTheRobot++;
//                    } else if(noOfArtifactsInTheRobot == 1) {
//                        secondRGBLightIndicator.setRGBLightToGreen();
//                        noOfArtifactsInTheRobot++;
//                    } else if(noOfArtifactsInTheRobot == 2) {
//                        thirdRGBLightIndicator.setRGBLightToGreen();
//                        noOfArtifactsInTheRobot++;
//                    }
//            } else {
//                artifactIntakeIndicator.setRGBLightToWhite();
//            }

            if(enableRGBLights && isStopperBlockingArtifact) {
                isArtifactAllowedIndicator.setRGBLightToRed();
            } else if(enableRGBLights && !isStopperBlockingArtifact){
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

    private String glowColorSensorRGBLight(MainBotColorSensor intakeColorSensorArtifact1, MainBotColorSensor intakeColorSensorArtifact2) {

        String colorToSet;

        boolean artifactOneDetected = hasColorSensorDetectedAnArtifact(intakeColorSensorArtifact1);
        boolean artifactTwoDetected = hasColorSensorDetectedAnArtifact(intakeColorSensorArtifact2);

        if (artifactOneDetected && artifactTwoDetected) {
            // All three artifacts are detected
            colorToSet = "GREEN";
        } else if (artifactOneDetected || artifactTwoDetected) {
            // Two artifacts are detected
            colorToSet = "ORANGE";
        } else {
            colorToSet = "RED";
        }

        return colorToSet;
    }

    private boolean hasColorSensorDetectedAnArtifact(MainBotColorSensor colorSensor) {

        boolean artifactDetected;

        if(enableIntakeColorSensor && (colorSensor.getDetectedColor(telemetry).equals(MainBotColorSensor.DetectedColor.GREEN) ||
                colorSensor.getDetectedColor(telemetry).equals(MainBotColorSensor.DetectedColor.PURPLE))) {

            artifactDetected = true;

        } else {

            artifactDetected = false;
        }

        return artifactDetected;
    }
}