package org.firstinspires.ftc.teamcode.mainbot.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utils.StarterBotConstants;

public final class MainBotLaunchFeederMechanism {

//    public final CRServo leftFeeder, rightFeeder;

    public final Servo feederServo;

    private double ALLOW_ARTIFACT_POSITION = 0.1;

    private double BLOCK_ARTIFACT_POSITION = 0.45;

    public MainBotLaunchFeederMechanism(HardwareMap hardwareMap, Telemetry telemetry) {

        feederServo = hardwareMap.get(Servo.class, StarterBotConstants.LEFT_FEEDER_CRSERVO_NAME);
//        rightFeeder = hardwareMap.get(CRServo.class, StarterBotConstants.RIGHT_FEEDER_CRSERVO_NAME);

        /*
         * set Feeders to an initial value to initialize the servo controller
         */
        blockArtifact();

    }

    public void blockArtifact() {
        feederServo.setPosition(BLOCK_ARTIFACT_POSITION);
    }

    public void allowArtifact() {
        feederServo.setPosition(ALLOW_ARTIFACT_POSITION);
    }

    public boolean isArtifactAllowedToFlow(Telemetry telemetry) {

        telemetry.addData("Stopper position : ", feederServo.getPosition());

        if(feederServo.getPosition() <= ALLOW_ARTIFACT_POSITION) {

            return true;
        }

        return false;
    }
}
