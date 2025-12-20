package org.firstinspires.ftc.teamcode.testbench.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestBenchColor {
    public static DetectedColor DetectedColor;
    NormalizedColorSensor colorSensor;

    public enum DetectedColor{
        GREEN,
        PURPLE,
        UNKOWN
    }

    public void init(HardwareMap hwmap){
        colorSensor = hwmap.get(NormalizedColorSensor.class, "sensor_color_distance");
        colorSensor.setGain(8);
    }
    public DetectedColor getdetectedColor(Telemetry telemetry){
        NormalizedRGBA colors = colorSensor.getNormalizedColors(); // return 4 values

        float normPurple, normGreen;
        normGreen = colors.green/colors.alpha;
        normPurple = colors.toColor()/colors.alpha;

        telemetry.addData("Purple", normPurple);
        telemetry.addData("green", normGreen);


        //TODO add if statements for specific colors added
        /*
        red,green,blue
        RED = >.35 on red, <.3 on green, <.3 on blue /This sample is from the guy in the video
        yellow = >.5 on red, >.9 on green, <.6 on blue
        BLUE = <.2 on red, <.5 on green, >.5 on blue

         */
        if(normGreen > 0.35 && normGreen < 0.3 && normGreen < 0.3){
            return DetectedColor.GREEN;
        } else if (normPurple > 0.5 && normGreen >0.9 && normPurple < 0.6) {
            return TestBenchColor.DetectedColor.PURPLE;
            
        }
            

        return DetectedColor.UNKOWN;


        return null;
    }
}
