package org.firstinspires.ftc.teamcode.testbench.sensors;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LED;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class TestLED {


    private LED leftGreenLED;

    private LED leftRedLED;
    private LED rightGreenLED;

    private LED rightRedLED;

    public void init(HardwareMap hardwareMap) {

        leftGreenLED = hardwareMap.get(LED.class, "leftGreenLED");

        leftRedLED = hardwareMap.get(LED.class, "leftRedLED");

        rightGreenLED = hardwareMap.get(LED.class, "rightGreenLED");

        rightRedLED = hardwareMap.get(LED.class, "rightRedLED");

        setRedLED(false);
        setGreenLED(false);

    }

    public void setRedLED(boolean turnOn) {

        if(turnOn) {
            leftRedLED.on();
        } else {
            leftRedLED.off();
        }
    }

    public void setGreenLED(boolean turnOn) {

        if(turnOn) {
            leftGreenLED.on();
        } else {
            leftGreenLED.off();
        }
    }
}
