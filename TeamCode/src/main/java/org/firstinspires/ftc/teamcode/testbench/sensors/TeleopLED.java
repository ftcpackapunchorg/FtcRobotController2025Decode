package org.firstinspires.ftc.teamcode.testbench.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LED;

public class TeleopLED {


    private LED leftGreenLED;

    private LED leftRedLED;
    private LED rightGreenLED;

    private LED rightRedLED;

    public void init(HardwareMap hardwareMap) {

        leftGreenLED = hardwareMap.get(LED.class, "leftGreenLED");

        leftRedLED = hardwareMap.get(LED.class, "leftRedLED");

        rightGreenLED = hardwareMap.get(LED.class, "rightGreenLED");

        rightRedLED = hardwareMap.get(LED.class, "rightRedLED");

        setRedLED(false, "right");
        setRedLED(false, "left");
        setGreenLED(false, "right");
        setRedLED(false, "left");

    }

    public void setRedLED(boolean turnOn, String name) {

        if(turnOn) {
            if (name.equals("right")) {
                rightRedLED.on();
            } else if (name.equals("left")) {
                leftRedLED.on();
            }
        } else {
            if (name.equals("right")) {
                rightRedLED.off();
            } else if (name.equals("left")) {
                leftRedLED.off();
            }
        }
    }

    public void setGreenLED(boolean turnOn, String name) {

        if(turnOn) {
            if (name.equals("right")) {
                rightGreenLED.on();
            } else if (name.equals("left")) {
                leftGreenLED.on();
        } else {
            if (name.equals("right")) {
                rightGreenLED.off();
            } else if (name.equals("left"))
                leftGreenLED.off();
            }
        }
    }
}
