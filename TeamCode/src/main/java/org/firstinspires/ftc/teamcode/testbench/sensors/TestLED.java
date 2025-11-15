package org.firstinspires.ftc.teamcode.testbench.sensors;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LED;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class TestLED {


    private LED greenLED;
    private LED redLED;

    public void init(HardwareMap hardwareMap) {

        greenLED = hardwareMap.get(LED.class, "greenLED");

        redLED = hardwareMap.get(LED.class, "redLED");

        setRedLED(false);
        setGreenLED(false);

    }

    public void setRedLED(boolean turnOn) {

        if(turnOn) {
            redLED.on();
        } else {
            redLED.off();
        }
    }

    public void setGreenLED(boolean turnOn) {

        if(turnOn) {
            greenLED.on();
        } else {
            greenLED.off();
        }
    }
}
