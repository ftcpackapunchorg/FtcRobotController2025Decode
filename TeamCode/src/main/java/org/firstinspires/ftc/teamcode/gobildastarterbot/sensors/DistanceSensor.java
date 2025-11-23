package org.firstinspires.ftc.teamcode.gobildastarterbot.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DistanceSensor {


    private com.qualcomm.robotcore.hardware.DistanceSensor distanceSensor;

    public void init(HardwareMap hardwareMap, String name) {

        distanceSensor = hardwareMap.get(com.qualcomm.robotcore.hardware.DistanceSensor.class, name);

    }

    public double getDistance() {
        return distanceSensor.getDistance(DistanceUnit.CM);
    }

}
