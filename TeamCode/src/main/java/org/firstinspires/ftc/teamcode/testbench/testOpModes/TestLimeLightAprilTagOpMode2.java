package org.firstinspires.ftc.teamcode.testbench.testOpModes;


import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.gobildastarterbot.mechanicals.StarterBotFeederMechanism;
import org.firstinspires.ftc.teamcode.gobildastarterbot.mechanicals.StarterBotLaunchMechanism;
import org.firstinspires.ftc.teamcode.mainbot.mechanisms.MainBotMecanumDrive;
import org.firstinspires.ftc.teamcode.utils.StarterBotConstants;

import java.util.List;


@TeleOp(name="TestLimeLightAprilTagOpMode2", group="TestBench")
public class TestLimeLightAprilTagOpMode2 extends OpMode {
    double power = 1;

    private Limelight3A limelight;

    MainBotMecanumDrive drive;

    StarterBotLaunchMechanism launchMechanism;

    StarterBotFeederMechanism feederMechanism;

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

    @Override
    public void init() {

        Pose2d initPose = new Pose2d(StarterBotConstants.BLUE_INIT_POSE_X,StarterBotConstants.BLUE_INIT_POSE_Y, Math.toRadians(StarterBotConstants.BLUE_INIT_POSE_HEADING_DEGREES));

        Pose2d currentPoseFromAprilTag = null;

        drive = new MainBotMecanumDrive(hardwareMap, initPose);

        drive.localizer.update();

        double headingRadians = drive.localizer.getPose().heading.toDouble();

        double headingDegrees = Math.toDegrees(headingRadians);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(2); // 0 is purple artifact, 1 is green artifact. // 2 - April tag 20 (Blue) // 3 - April tag 24 (Red) // 4 - April Tag Multiple 19 - 24

        limelight.updateRobotOrientation(headingDegrees);

//        TrajectoryActionBuilder goToNearLaunchZone = createActionForLaunchZoneNear(currentPoseFromAprilTag);

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Initialized");

    }

    private TrajectoryActionBuilder createActionForLaunchZoneNear(Pose2d currentPoseFromAprilTag) {

        TrajectoryActionBuilder goToLaunchZoneNear = drive.actionBuilder(currentPoseFromAprilTag)
                .strafeToLinearHeading(new Vector2d(-20, -20), Math.toRadians(-135));

        return goToLaunchZoneNear;
    }

    @Override
    public void start() {

        limelight.start();
//        limelight.setPollRateHz();
    }

    @Override
    public void loop() {

        YawPitchRollAngles orientation = drive.lazyImu.get().getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw());

        LLResult llResult = limelight.getLatestResult();

        // Access fiducial results
        List<LLResultTypes.FiducialResult> fiducialResults = llResult.getFiducialResults();
//        for (LLResultTypes.FiducialResult fr : fiducialResults) {
//            telemetry.addData("Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees());
//        }

//        LLResultTypes.DetectorResult tagDetection;
//
//        List<LLResultTypes.DetectorResult> detections = llResult.getDetectorResults();
//        for (LLResultTypes.DetectorResult detection : detections) {
//            String className = detection.getClassName(); // What was detected
//            double x = detection.getTargetXDegrees(); // Where it is (left-right)
//            double y = detection.getTargetYDegrees(); // Where it is (up-down)
//            telemetry.addData(className, "at (" + x + ", " + y + ") degrees");
//        }

        for (LLResultTypes.FiducialResult fiducial : fiducialResults) {
            int id = fiducial.getFiducialId(); // The ID number of the fiducial
            double x = fiducial.getTargetXDegrees(); // Where it is (left-right)
            double y = fiducial.getTargetYDegrees(); // Where it is (up-down)
            double StrafeDistance_3D = fiducial.getRobotPoseTargetSpace().getPosition().y;;
            telemetry.addData("Fiducial " + id, "is " + StrafeDistance_3D + " meters away");

//            fiducial.getRobotPoseTargetSpace(); // Robot pose relative it the AprilTag Coordinate System (Most Useful)
//            fiducial.getCameraPoseTargetSpace(); // Camera pose relative to the AprilTag (useful)
//            fiducial.getRobotPoseFieldSpace(); // Robot pose in the field coordinate system based on this tag alone (useful)
//            fiducial.getTargetPoseCameraSpace(); // AprilTag pose in the camera's coordinate system (not very useful)
//            fiducial.getTargetPoseRobotSpace(); // AprilTag pose in the robot's coordinate system (not very useful)

            double currentPositionX = fiducial.getRobotPoseFieldSpace().getPosition().x;
            double currentPositionY = fiducial.getRobotPoseFieldSpace().getPosition().y;
            double currentPositionAngleInDegrees = fiducial.getRobotPoseFieldSpace().getOrientation().getYaw(AngleUnit.DEGREES);

            Pose2d currRobotPose = new Pose2d(currentPositionX * 39.3701, currentPositionY * 39.3701, Math.toRadians(currentPositionAngleInDegrees));

            telemetry.addData("Current Position X : ", currentPositionX);
            telemetry.addData("Current Position Y : ", currentPositionY);
            telemetry.addData("Current Position X Inches : ", currentPositionX * 39.3701);
            telemetry.addData("Current Position Y Inches : ", currentPositionY * 39.3701);
            telemetry.addData("Current Position Angle In Degrees : ", currentPositionAngleInDegrees);
//            telemetry.update();

            double robotFromTargetPosePosX = fiducial.getRobotPoseTargetSpace().getPosition().x;
            double robotFromTargetPosePosY = fiducial.getRobotPoseTargetSpace().getPosition().y;
            double robotFromTargetPosePosAngleInDegrees = fiducial.getRobotPoseTargetSpace().getOrientation().getYaw(AngleUnit.DEGREES);

            Pose2d robotFromTargetPose = new Pose2d(robotFromTargetPosePosX * 39.3701, robotFromTargetPosePosY * 39.3701, Math.toRadians(robotFromTargetPosePosAngleInDegrees));

            telemetry.addData("Robot Position From Target X : ", robotFromTargetPosePosX);
            telemetry.addData("Robot Position From Target Y : ", robotFromTargetPosePosY);
            telemetry.addData("Robot Position From Target X Inches : ", robotFromTargetPosePosX * 39.3701);
            telemetry.addData("Robot Position From Target Y Inches : ", robotFromTargetPosePosY * 39.3701);
            telemetry.addData("Robot Position From Target Angle In Degrees : ", robotFromTargetPosePosAngleInDegrees);
            telemetry.update();

            Pose2d targetPose = new Pose2d(-20, -20, Math.toRadians(-135));

//            Actions.runBlocking(createActionForLaunchZoneNear(currRobotPose).build());

        }

//        drive.localizer.update();
//
//        double headingRadians = drive.localizer.getPose().heading.toDouble();
//
//        double headingDegrees = Math.toDegrees(headingRadians);
//
//        telemetry.addData("headingRadians", headingRadians);
//
//        limelight.updateRobotOrientation(headingDegrees);

        if(llResult != null && llResult.isValid()) {

            Pose3D robotPose = llResult.getBotpose_MT2(); // If using Metatag 2. If using metatag 1, use getBotpose

            telemetry.addData("Target X Offset : ", llResult.getTx());
            telemetry.addData("Target Y Offset : ", llResult.getTy());
            telemetry.addData("Target Area Offset : ", llResult.getTa());
            telemetry.addData("Robot Pose : ", robotPose.toString());
            telemetry.addData("Yaw : ", robotPose.getOrientation().getYaw());

            // Field coordinates in meters
//            double fieldX = robotPose.getPosition().x;
//            double fieldY = robotPose.getPosition().y;
//
//            // If you need inches for Roadrunner:
//            double xInches = fieldX * 39.37;
//            double yInches = fieldY * 39.37;

            double xInches = robotPose.getPosition().toUnit(DistanceUnit.INCH).x;
            double yInches = robotPose.getPosition().toUnit(DistanceUnit.INCH).y;

            telemetry.addData("Field X (in)", xInches);
            telemetry.addData("Field Y (in)", yInches);
            telemetry.update();

//            double distance = getDistanceFromTag(llResult.getTa());
//
//            telemetry.addData("Calculated Distance : ", distance);

//            y = 8939.352*x^-1.923755

//            Pose3D mt1RobotPose = llResult.getBotpose();
//
//            telemetry.addData("Target X Offset : ", llResult.getTx());
//            telemetry.addData("Target Y Offset : ", llResult.getTy());
//            telemetry.addData("Target Area Offset : ", llResult.getTa());
//            telemetry.addData("Robot Pose : ", mt1RobotPose.toString());
//            telemetry.addData("Yaw : ", mt1RobotPose.getOrientation().getYaw());
//            telemetry.addData("X : ", mt1RobotPose.getPosition().x);
//            telemetry.addData("Y : ", mt1RobotPose.getPosition().y);
//            telemetry.addData("Z : ", mt1RobotPose.getPosition().z);
//
//
//
//
//////            Pose2d aprilTagPose = new Pose2d(llResult.getTx(), llResult.getTy(), Math.toRadians(llResult.getTa()));
////
////            // Determine heading, range and Yaw (tag image rotation) error so we can use them to control the robot automatically.
////            double  rangeError      = (llResult.getTx() - DESIRED_DISTANCE);
////            double  headingError    = llResult.getTa();
////            double  yawError        = llResult.getTy();
////
//////            limelight.stop();
////
////            // Use the speed and turn "gains" to calculate how we want the robot to move.
////            double forward  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
////            double strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);
////            double rotate   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN) ;
////
////            telemetry.addData("Auto","Drive %5.2f, Strafe %5.2f, Turn %5.2f ", forward, strafe, rotate);
////
////            if(gamepad1.a) {
////
////                Pose2d currentPose = drive.localizer.getPose();
////
////                Pose2d targetPose = new Pose2d(rangeError, yawError, Math.toRadians(headingError));
////
////                telemetry.addData("Current Pose : ", currentPose.toString());
////                telemetry.addData("Target Pose : ", targetPose.toString());
////                telemetry.update();
////
////                TrajectoryActionBuilder toToAprilTag = drive.actionBuilder(currentPose)
////                        .waitSeconds(1)
////                        .strafeToLinearHeading(new Vector2d(rangeError, yawError), Math.toRadians(headingError))
////                        .waitSeconds(2);
////
////                Actions.runBlocking(toToAprilTag.build());
////
////                Pose2d newCurrentPose = drive.localizer.getPose();
////                telemetry.addData("New Current Pose : ", newCurrentPose.toString());
////                telemetry.addData("Current Pose X : ", newCurrentPose.position.x);
////                telemetry.addData("Current Pose Y : ", newCurrentPose.position.y);
////                telemetry.addData("Current Pose A : ", newCurrentPose.heading);
////                telemetry.update();
////
//////                drive.mecanumDrive(forward, strafe, rotate, 0, telemetry);
////            }
        }



//        PoseVelocity2d currentPose = drive.updatePoseEstimate();

//        Pose2d currentPose = new Pose2d(currentPoseVel.component1(), currentPoseVel.component2());
//        telemetry.addData("New Current Pose : ", currentPose.toString());
//        telemetry.addData("Current Pose X : ", currentPoseVel.component1().x);
//        telemetry.addData("Current Pose Y : ", currentPoseVel.component1().y);
//        telemetry.addData("Current Pose A : ", currentPoseVel.component2());

//        Pose2d currentPose = new Pose2d(currentPoseVel.component1(), currentPoseVel.component2());
//        telemetry.addData("New Current Pose : ", newCurrentPose.toString());
//        telemetry.addData("Current Pose X : ", newCurrentPose.position.x);
//        telemetry.addData("Current Pose Y : ", newCurrentPose.position.y);
//        telemetry.addData("Current Pose A : ", newCurrentPose.heading);


//        if(gamepad1.a) {
//
//            drive.leftFront.setPower(-.5);
//            drive.rightFront.setPower(.5);
//            drive.rightBack.setPower(.5);
//            drive.leftBack.setPower(-.5);
//        }

//        if(gamepad1.aWasReleased()) {
//
//            drive.leftFront.setPower(-.5);
//            drive.rightFront.setPower(.5);
//            drive.rightBack.setPower(.5);
//            drive.leftBack.setPower(-.5);
//        }
//
//        if(gamepad1.b) {
//
//            launchMechanism.launcher.setPower(.5);
//
//        }
//
//        if(gamepad1.bWasReleased()) {
//
//            launchMechanism.launcher.setPower(0);
//        }
//
//        if(gamepad1.x) {
//
//            feederMechanism.leftFeeder.setPower(-.5);
//            feederMechanism.rightFeeder.setPower(.5);
//        }
//
//        if(gamepad1.xWasReleased()) {
//            feederMechanism.leftFeeder.setPower(0);
//            feederMechanism.rightFeeder.setPower(0);
//        }

        telemetry.update();
    }

    public double getDistanceFromTag(double ta) {

        double scale = 38665.88;

        double distance = scale/ta;

        return distance;
    }

    /*
     * This code runs ONCE after the driver hits STOP.
     */
    @Override
    public void stop() {
    }
}
