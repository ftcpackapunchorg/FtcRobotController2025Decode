package org.firstinspires.ftc.teamcode.gobildastarterbot.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LED;

public class SimpleLEDLight {


    private LED greenLED;

    private LED redLED;

    private String name;

    public void init(HardwareMap hardwareMap) {

        greenLED = hardwareMap.get(LED.class, "greenLED");

        redLED = hardwareMap.get(LED.class, "redLED");

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
