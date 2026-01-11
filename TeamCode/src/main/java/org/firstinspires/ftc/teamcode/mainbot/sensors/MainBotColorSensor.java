package org.firstinspires.ftc.teamcode.mainbot.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MainBotColorSensor {


    private NormalizedColorSensor colorSensor;

    public enum DetectedColor {

        PURPLE,
        GREEN,
        BLACK,
        UNKNOWN

    }

    public void init(HardwareMap hardwareMap, String sensorName) {

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, sensorName);
        colorSensor.setGain(4); // Starts with a value of 1

    }

    public DetectedColor getDetectedColor(Telemetry telemetry) {

        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        // 4 values: RED, GREEN, BLUE, ALPHA. ALPHA is how much light is being returned.

        float normRed, normGreen, normBlue;

        normRed = colors.red/ colors.alpha;
        normGreen = colors.green/ colors.alpha;
        normBlue = colors.blue/ colors.alpha;

        telemetry.addData("Red = ", normRed);
        telemetry.addData("Green = ", normGreen);
        telemetry.addData("Blue = ", normBlue);

        return DetectedColor.UNKNOWN;

    }

}
