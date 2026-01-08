package org.firstinspires.ftc.teamcode.mainbot.sensors;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class MainBotDistanceSensor {


    private DistanceSensor distanceSensor;

    public void init(HardwareMap hardwareMap, String sensorName) {

        distanceSensor = hardwareMap.get(com.qualcomm.robotcore.hardware.DistanceSensor.class, sensorName);

    }

    public double getDistance() {
        return distanceSensor.getDistance(DistanceUnit.CM);
    }

}
