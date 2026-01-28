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
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotLaunchMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotMecanumDrive;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotColorSensor;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotDistanceSensor;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotRGBLightIndicator;
import org.firstinspires.ftc.teamcode.mainbot.sensors.MainBotSimpleLEDLight;
import org.firstinspires.ftc.teamcode.mainbot.utils.MainBotConstants;

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

@TeleOp(name = "MainBot Teleop Main", group = "MainBot")
public class MainBotTeleopMain extends OpMode {

    MainBotMecanumDrive drive;

    MainBotLaunchMechanism launchMechanism;

    MainBotIntakeMechanism intakeMechanism;

    MainBotDistanceSensor leftDistanceSensor;
    MainBotDistanceSensor rightDistanceSensor;
    MainBotSimpleLEDLight leftLED;
    MainBotSimpleLEDLight rightLED;
    MainBotRGBLightIndicator firstRGBLightIndicator;
    MainBotRGBLightIndicator secondRGBLightIndicator;
    MainBotRGBLightIndicator thirdRGBLightIndicator;

    MainBotColorSensor intakeColorSensor = new MainBotColorSensor();

    boolean enableDistanceSensors;
    boolean enableRGBLights;
    boolean enableSimpleLEDLights;
    boolean enableIntakeColorSensor;
    int noOfArtifactsInTheRobot = 0;

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

        launchMechanism = new MainBotLaunchMechanism(hardwareMap, telemetry);

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

            firstRGBLightIndicator.init(hardwareMap, MainBotConstants.FIRST_RGB_LIGHT_INDICATOR_NAME);
            secondRGBLightIndicator.init(hardwareMap, MainBotConstants.SECOND_RGB_LIGHT_INDICATOR_NAME);
            thirdRGBLightIndicator.init(hardwareMap, MainBotConstants.THIRD_RGB_LIGHT_INDICATOR_NAME);
            enableRGBLights = true;

        } catch (Exception e) {
            telemetry.addData("Initialization", "No RGB Light Indicators");
            enableRGBLights = false;
        }

        // Initiate Color Sensor if configured
        try {

            intakeColorSensor.init(hardwareMap, "intakeColorSensor");

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
            launchMechanism.launch(true);
        } else if(gamepad2.yWasReleased()) {
            launchMechanism.startLauncherNearZone();
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
        }

//        intakeMechanism.startIntakeWithInput(gamepad2.right_trigger);

        /*
         * Now we call our "Launch" function.
         */
        launchMechanism.launch(gamepad2.rightBumperWasPressed());

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

        if(enableRGBLights) {

            if(enableIntakeColorSensor) {

                if(intakeColorSensor.getDetectedColor(telemetry).equals(MainBotColorSensor.DetectedColor.GREEN) ||
                        intakeColorSensor.getDetectedColor(telemetry).equals(MainBotColorSensor.DetectedColor.PURPLE)) {

                    if(noOfArtifactsInTheRobot == 0) {
                        firstRGBLightIndicator.setRGBLightToGreen();
                        noOfArtifactsInTheRobot++;
                    } else if(noOfArtifactsInTheRobot == 1) {
                        secondRGBLightIndicator.setRGBLightToGreen();
                        noOfArtifactsInTheRobot++;
                    } else if(noOfArtifactsInTheRobot == 2) {
                        thirdRGBLightIndicator.setRGBLightToGreen();
                        noOfArtifactsInTheRobot++;
                    }
                }
            }
        }

        if(enableIntakeColorSensor) {

            intakeColorSensor.getDetectedColor(telemetry);

        }

        /*
         * Show the state and motor powers
         */
        telemetry.addData("Launch State", launchMechanism.getLaunchState());
        telemetry.addData("Launcher MotorSpeed", launchMechanism.launcher.getVelocity());

        telemetry.addData("Intake State", intakeMechanism.getIntakeState());
        telemetry.addData("Intake MotorSpeed", intakeMechanism.intake.getPower());
//        telemetry.update();

    }

    private void lightUpDistanceBasedLEDs(MainBotSimpleLEDLight ledInput, Telemetry telemetry, double distance) {

        if(distance < 30) {

            ledInput.turnLEDToRed();

            telemetry.addData("Too close", ledInput.getNameOfLED());

        } else if(distance >= 30 && distance <= 55) {

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