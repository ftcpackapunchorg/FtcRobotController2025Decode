package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MainBotNoIntakeNearSideBlue {
    public static void main(String[] args){
        MeepMeep meepMeep = new MeepMeep(400);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(60, -12, Math.toRadians(180)))
                .waitSeconds(1)
                .lineToX(56)
                .turn(Math.toRadians(45))
                .waitSeconds(2)
                .strafeTo(new Vector2d(53,-25))
                .turn(Math.toRadians(45))




//                .strafeTo(new Vector2d(-13,-54)) // Start intake motor and servo here
//                .waitSeconds(0.7)
//                .strafeTo(new Vector2d(-13,-12))
//                .strafeTo(new Vector2d(-11,-12))
//                .turn(Math.toRadians(-45))
//                .waitSeconds(2.35) // Launching takes place here
//                .turn(Math.toRadians(45))
//                .strafeTo(new Vector2d(13,-12))
//                .strafeTo(new Vector2d(13,-54)) // Start intake motor and servo here
//                .waitSeconds(0.7)
//                .strafeTo(new Vector2d(13,-12))
//                .strafeTo(new Vector2d(-12,-12))
//                .turn(Math.toRadians(-45))
//                .waitSeconds(2.35) // Launching takes place here
//                .strafeTo(new Vector2d(13,-16))
//                .turn(Math.toRadians(180))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}