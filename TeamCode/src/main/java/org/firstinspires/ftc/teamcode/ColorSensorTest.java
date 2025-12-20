package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.testbench.sensors.TestBenchColor;

public class ColorSensorTest extends OpMode {
    TestBenchColor bench = new TestBenchColor();
    TestBenchColor.DetectedColor detectedColor;
    @Override
    public void init() {
        bench.init(hardwareMap);
    }

    @Override
    public void loop() {
        TestBenchColor.DetectedColor  = bench.getdetectedColor(telemetry);
        telemetry.addData("Color Detected", detectedColor);

    }
}
