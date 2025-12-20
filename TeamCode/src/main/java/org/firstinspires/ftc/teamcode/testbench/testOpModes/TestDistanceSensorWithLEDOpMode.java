package org.firstinspires.ftc.teamcode.testbench.testOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.testbench.sensors.TestDistanceSensor;
import org.firstinspires.ftc.teamcode.testbench.sensors.TestLED;

@TeleOp(name = "TestDistanceSensor2MWithLED", group = "TestBench")
public class TestDistanceSensorWithLEDOpMode extends OpMode {

    TestDistanceSensor distanceSensor = new TestDistanceSensor();

    TestLED testLED = new TestLED();

    @Override
    public void init() {

        distanceSensor.init(hardwareMap,"");
        testLED.init(hardwareMap);

    }

    @Override
    public void loop() {

        telemetry.addData("Distance : ", distanceSensor.getDistance());

        // Print "Too Close" if the distance is less than 10 cm
        double distance = distanceSensor.getDistance();

        if(distance < 30) {
            testLED.setGreenLED(false);
            testLED.setRedLED(true);

            telemetry.addLine("Too close. Turning on red");

        } else if(distance >= 30 && distance <= 55) {

            testLED.setGreenLED(true);
            testLED.setRedLED(true);

            telemetry.addLine("Be cautious. Turning on yellow");

        }
        else {

            testLED.setGreenLED(true);
            testLED.setRedLED(false);

            telemetry.addLine("Safe distance. Turning on green");

        }
    }
}

