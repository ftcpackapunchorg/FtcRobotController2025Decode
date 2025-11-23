package org.firstinspires.ftc.teamcode.testbench.testOpModes;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.testbench.sensors.TestDistanceSensor;

public class TestdistancesensorLedLight {

    private TestDistanceSensor distance;

    public void init(HardwareMap hwMap) {
        distance = hwMap.get(TestDistanceSensor.class, "rightDistanceSensor");
    }


    public double getdistance(){
        return distance.getDistance();
    }
}
