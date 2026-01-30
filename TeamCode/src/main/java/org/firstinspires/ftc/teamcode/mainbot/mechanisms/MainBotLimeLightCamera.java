package org.firstinspires.ftc.teamcode.mainbot.mechanisms;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MainBotLimeLightCamera {

    public Limelight3A limelight;

    final double DESIRED_DISTANCE = 12.0; //  this is how close the camera should get to the target (inches)

    //  Set the GAIN constants to control the relationship between the measured position error, and how much power is
    //  applied to the drive motors to correct the error.
    //  Drive = Error * Gain    Make these values smaller for smoother control, or larger for a more aggressive response.
    final double SPEED_GAIN  =  0.02  ;   //  Forward Speed Control "Gain". e.g. Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    final double STRAFE_GAIN =  0.015 ;   //  Strafe Speed Control "Gain".  e.g. Ramp up to 37% power at a 25 degree Yaw error.   (0.375 / 25.0)
    final double TURN_GAIN   =  0.01  ;   //  Turn Control "Gain".  e.g. Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_STRAFE= 0.5;   //  Clip the strafing speed to this max value (adjust for your robot)
    final double MAX_AUTO_TURN  = 0.3;   //  Clip the turn speed to this max value (adjust for your robot)

    private double distance;

    public MainBotLimeLightCamera(HardwareMap hardwareMap, Telemetry telemetry, String alliance) {

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        if("BLUE".equals(alliance)) {
            limelight.pipelineSwitch(2); // 0 is purple artifact, 1 is green artifact. // 2 - April tag 20 (Blue) // 3 - April tag 24 (Red)
        } else if("RED".equals(alliance)) {
            limelight.pipelineSwitch(3); // 0 is purple artifact, 1 is green artifact. // 2 - April tag 20 (Blue) // 3 - April tag 24 (Red)
        }

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Limelight Initialized");
    }

    public void startLimeLightCamera() {
        limelight.start();
    }

    public void stopLimeLightCamera() {
        limelight.stop();
    }
}
