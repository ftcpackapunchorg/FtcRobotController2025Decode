package org.firstinspires.ftc.teamcode.gobildastarterbot.mechanicals;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utils.StarterBotConstants;

public final class StarterBotLaunchMechanism {

    public final DcMotorEx launcher;

    public StarterBotLaunchMechanism(HardwareMap hardwareMap, Telemetry telemetry) {

        launcher= hardwareMap.get(DcMotorEx.class, StarterBotConstants.LAUNCHER_ONE_TO_ONE_RATIO_MOTOR_NAME);

        /*
         * Here we set our launcher to the RUN_USING_ENCODER runmode.
         * If you notice that you have no control over the velocity of the motor, it just jumps
         * right to a number much higher than your set point, make sure that your encoders are plugged
         * into the port right beside the motor itself. And that the motors polarity is consistent
         * through any wiring.
         */
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));

    }
}
