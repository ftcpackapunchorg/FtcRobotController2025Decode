package org.firstinspires.ftc.teamcode.testbench.testOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.LED;


import org.firstinspires.ftc.teamcode.testbench.sensors.TestLED;
@TeleOp(name = "DistanceTest", group = "StarterBot")
public class DistanceTest extends OpMode{
    TestdistancesensorLedLight bench = new TestdistancesensorLedLight();
    double distance;
    TestLED led;
    private LED greenLED;
    private LED redLED;


    @Override
    public void init() {
        bench.init(hardwareMap);
        led.init(hardwareMap);
        greenLED = hardwareMap.get(LED.class, "greenLED");
        redLED = hardwareMap.get(LED.class, "redLED");
    }
    @Override
    public void loop() {
        distance = bench.getdistance();

        if (distance < 30) {
            telemetry.addLine("Too close");
            led.setRedLED(true);
        }
        else if ((distance >= 30) && (distance <= 55)) {
            telemetry.addLine("Getting close");
            led.setGreenLED(true);
            led.setRedLED(true);

        }
        else {
            telemetry.addLine("All good");
            led.setGreenLED(true);
        }



//            if (distance<=10, distance>=25);

    }
}
