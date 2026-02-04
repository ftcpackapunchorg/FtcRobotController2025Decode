package org.firstinspires.ftc.teamcode.mainbot.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.JavaUtil;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MainBotColorSensor {


    private NormalizedColorSensor colorSensor;

//    private DistanceSensor colorSensor;

    public enum DetectedColor {

        PURPLE,
        GREEN,
        BLACK,
        RED,
        BLUE,
        YELLOW,
        ORANGE,
        UNKNOWN

    }

    public void init(HardwareMap hardwareMap, String sensorName) {

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, sensorName);
        colorSensor.setGain(4); // Starts with a value of 1

//        colorSensor = hardwareMap.get(DistanceSensor.class, sensorName);

    }

//    public double getDetectedColor(Telemetry telemetry) {
//
//        return colorSensor.getDistance(DistanceUnit.CM);
//
//    }

    public DetectedColor getDetectedColor(Telemetry telemetry) {

        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        // 4 values: RED, GREEN, BLUE, ALPHA. ALPHA is how much light is being returned.

        float normRed, normGreen, normBlue;

        //Determining the amount of red, green, and blue
//        telemetry.addData("Red", "%.3f", colors.red);
//        telemetry.addData("Green", "%.3f", colors.green);
//        telemetry.addData("Blue", "%.3f", colors.blue);

        //Determining HSV and alpha
        telemetry.addData("Hue", JavaUtil.colorToHue(colors.toColor()));
        telemetry.addData("Saturation", "%.3f", JavaUtil.colorToSaturation(colors.toColor()));
        telemetry.addData("Value", "%.3f", JavaUtil.colorToValue(colors.toColor()));
        telemetry.addData("Alpha", "%.3f", colors.alpha);

        normRed = colors.red/ colors.alpha;
        normGreen = colors.green/ colors.alpha;
        normBlue = colors.blue/ colors.alpha;
        double hue = JavaUtil.colorToHue(colors.toColor());

        telemetry.addData("Red = ", normRed);
        telemetry.addData("Green = ", normGreen);
        telemetry.addData("Blue = ", normBlue);

        DetectedColor detectedColor = DetectedColor.UNKNOWN;

        //Using hue to detect color
        if(hue < 30){
            telemetry.addData("Color", "Red");
            detectedColor = DetectedColor.RED;
        }
        else if (hue < 60) {
            telemetry.addData("Color", "Orange");
            detectedColor = DetectedColor.ORANGE;
        }
        else if (hue < 90){
            telemetry.addData("Color", "Yellow");
            detectedColor = DetectedColor.YELLOW;
        }
        else if (hue < 175){
            telemetry.addData("Color", "Green");
            detectedColor = DetectedColor.GREEN;
        }
        else if (hue < 225){
            telemetry.addData("Color", "Blue");
            detectedColor = DetectedColor.BLUE;
        }
        else if (hue < 350){
            telemetry.addData("Color", "Purple");
            detectedColor = DetectedColor.PURPLE;
        }
        else{
            telemetry.addData("Color", "Red");
            detectedColor = DetectedColor.UNKNOWN;
        }
//        telemetry.update();

        return detectedColor;

    }
}
