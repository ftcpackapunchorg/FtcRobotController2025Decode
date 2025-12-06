package org.firstinspires.ftc.teamcode.cadbot.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class CADBotRGBLightIndicator {

    private Servo rgbLightIndicator;

    public void init(HardwareMap hardwareMap, String lightName) {

        rgbLightIndicator = hardwareMap.get(Servo.class, lightName);

        turnOff();

    }

    public void setRGBLightToGreen() {

        rgbLightIndicator.setPosition(0.5);

    }

    public void setRGBLightToRed() {

        rgbLightIndicator.setPosition(0.3);

    }

    public void setRGBLightToWhite() {

        rgbLightIndicator.setPosition(1);
    }


    public void turnOff() {

        rgbLightIndicator.setPosition(0);
    }
    public double getCurrentPosition() {

        return rgbLightIndicator.getPosition();

    }
}
