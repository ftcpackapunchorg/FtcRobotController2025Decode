package org.firstinspires.ftc.teamcode.cadbot.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LED;

public class CADBotSimpleLEDLight {


    private LED greenLED;

    private LED redLED;

    private String name;

    public void init(HardwareMap hardwareMap, String robotSideName) {

        String greenLEDName = "greenLED";

        String redLEDName = "redLED";

        if(robotSideName != null && !robotSideName.trim().isEmpty()) {

            greenLEDName = robotSideName + "GreenLED";
            redLEDName = robotSideName + "RedLED";

        }

        greenLED = hardwareMap.get(LED.class, greenLEDName);

        redLED = hardwareMap.get(LED.class, redLEDName);

        redLED.off();
        greenLED.off();

    }

    public void setNameOfLED(String name) {
        this.name = name;
    }

    public String getNameOfLED() {

        return name;
    }

    public void turnLEDToRed() {

        redLED.on();
        greenLED.off();
    }

    public void turnLEDToGreen() {

        redLED.off();
        greenLED.on();
    }

    public void turnLEDToAmber() {

        redLED.on();
        greenLED.on();
    }

    public void turnLEDOff() {
        redLED.off();
        greenLED.off();
    }
}
