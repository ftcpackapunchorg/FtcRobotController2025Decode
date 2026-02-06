package org.firstinspires.ftc.teamcode.newbot.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.newbot.utils.NewBotConstants;

public final class NewBotStopperMechanism {

//    public final CRServo leftFeeder, rightFeeder;

    public final Servo stopperServo;

    private double ALLOW_ARTIFACT_POSITION = 0.35;
            // 0.1; // 0.35;

    private double BLOCK_ARTIFACT_POSITION = 0.1;
            //0.4; // 0.1;

    public NewBotStopperMechanism(HardwareMap hardwareMap, Telemetry telemetry) {

        stopperServo = hardwareMap.get(Servo.class, NewBotConstants.STOPPER_SERVO_NAME);

        /*
         * set Feeders to an initial value to initialize the servo controller
         */
        blockArtifact();

    }

    public void blockArtifact() {
        stopperServo.setPosition(BLOCK_ARTIFACT_POSITION);
    }

    public void allowArtifact() {
        stopperServo.setPosition(ALLOW_ARTIFACT_POSITION);
    }

    public boolean isArtifactAllowedToFlow(Telemetry telemetry) {

        telemetry.addData("Stopper position : ", stopperServo.getPosition());

        if(stopperServo.getPosition() <= ALLOW_ARTIFACT_POSITION) {

            return true;
        }

        return false;
    }
}
