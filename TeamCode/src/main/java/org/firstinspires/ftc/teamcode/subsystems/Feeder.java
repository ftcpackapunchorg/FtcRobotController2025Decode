package org.firstinspires.ftc.teamcode.subsystems;

import org.firstinspires.ftc.teamcode.StarterBotAuto;

public class Feeder {


    boolean launch(boolean shotRequested){
        switch (launchState) {
            case IDLE:
                if (shotRequested) {
                launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_MIN_VELOCITY){
                    launchState = StarterBotAuto.LaunchState.LAUNCH;
                    feeder.setPower(1);

                    feederTimer.reset();
                }
                }
                break;
            case LAUNCH:
                if (feederTimer.seconds() > FEED_TIME) {
                    leftFeeder.setPower(0);
                    rightFeeder.setPower(0);

                    if(shotTimer.seconds() > TIME_BETWEEN_SHOTS){
                        launchState = StarterBotAuto.LaunchState.IDLE;
                        return true;
                    }
                }
        }
        return false;
    }


}
