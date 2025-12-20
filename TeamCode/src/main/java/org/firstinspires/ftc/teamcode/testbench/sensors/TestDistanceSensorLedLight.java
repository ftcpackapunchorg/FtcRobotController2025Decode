package org.firstinspires.ftc.teamcode.testbench.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class TestDistanceSensorLedLight {

    private TestDistanceSensor distance;

    public void init(HardwareMap hwMap) {
        distance = hwMap.get(TestDistanceSensor.class, "rightDistanceSensor");
    }


    public double getdistance(){
        return distance.getDistance();
    }
}
