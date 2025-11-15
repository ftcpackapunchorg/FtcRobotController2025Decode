package org.firstinspires.ftc.teamcode.testbench.testOpModes;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class TestdistancesensorLedLight {

    private DistanceSensor distance;

    public void init(HardwareMap hwMap) {
        distance = hwMap.get(DistanceSensor.class, "rightDistanceSensor");
    }


    public double getdistance(){
        return distance.getDistance(DistanceUnit.CM);
    }
    }
