package org.firstinspires.ftc.teamcode.testbench.sensors;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class TestDistanceSensor {


    private DistanceSensor distanceSensor;

    public void init(HardwareMap hardwareMap, String name) {

        distanceSensor = hardwareMap.get(DistanceSensor.class, name);

    }

    public double getDistance() {
        return distanceSensor.getDistance(DistanceUnit.CM);
    }

}
