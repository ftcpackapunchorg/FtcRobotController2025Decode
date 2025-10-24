package org.firstinspires.ftc.teamcode.testbench.testOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.testbench.sensors.TestDistanceSensor;

@TeleOp(name = "TestDistanceSensor2M", group = "TestBench")
public class TestDistanceSensorOpMode extends OpMode {

    TestDistanceSensor distanceSensor = new TestDistanceSensor();

    @Override
    public void init() {

        distanceSensor.init(hardwareMap);

    }

    @Override
    public void loop() {

        telemetry.addData("Distance : ", distanceSensor.getDistance());

        // Print "Too Close" if the distance is less than 10 cm
        double distance = distanceSensor.getDistance();

        if(distance < 10) {
            telemetry.addLine("Too close");
        }
    }
}
