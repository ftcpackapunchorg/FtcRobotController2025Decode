package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(500);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(60, -12, Math.toRadians(180)))
                .strafeToLinearHeading(new Vector2d(56,-12),Math.toRadians(195))
                .waitSeconds(3) //Shoot
                .strafeToLinearHeading(new Vector2d(36,-30), Math.toRadians(270))
                .waitSeconds(1) //Start Intake
                .strafeTo(new Vector2d(36,-53))
                .waitSeconds(1) // Stop Intake
                .strafeToLinearHeading(new Vector2d(56,-12), Math.toRadians(195))
                .waitSeconds(3) //Shoot
                .strafeToLinearHeading(new Vector2d(12,-30), Math.toRadians(270))
                .waitSeconds(1) // Start Intake 2
                .strafeTo(new Vector2d(12, -53))
                .waitSeconds(1) // Stop Intake 2
                .strafeToLinearHeading(new Vector2d(56,-12), Math.toRadians(195))
                .waitSeconds(3) // Shoot
                .strafeTo(new Vector2d(46,-27))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }}
