package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.mainbot.utils.MainBotConstants;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(10.8)
            .forwardZeroPowerAcceleration(-53.15050408949898)
            .lateralZeroPowerAcceleration(-71.3525267688645);
//            .translationalPIDFCoefficients(new PIDFCoefficients(0.07, 0, 0.05, 0.07));
//            .forwardZeroPowerAcceleration(-383.27292125439027)
//            .lateralZeroPowerAcceleration(-221.532463826907);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localizerConstants)
//                .twoWheelLocalizer(localizerConstants)
                .build();
    }

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName(MainBotConstants.FRONT_RIGHT_WHEEL_MOTOR_NAME)
            .rightRearMotorName(MainBotConstants.BACK_RIGHT_WHEEL_MOTOR_NAME)
            .leftRearMotorName(MainBotConstants.BACK_LEFT_WHEEL_MOTOR_NAME)
            .leftFrontMotorName(MainBotConstants.FRONT_LEFT_WHEEL_MOTOR_NAME)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(54.28465210922121)
            .yVelocity(28.552962415800316);
//            .xVelocity(98.35647551468978)
//            .yVelocity(55.057696554604476);

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(0.75) // Distance from center to the forward backward moving odometry pod
            .strafePodX(-6) // Distance from center to the left right moving odometry pod
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);



//    public static TwoWheelConstants localizerConstants = new TwoWheelConstants()
//            .forwardEncoder_HardwareMapName("leftFront")
//            .strafeEncoder_HardwareMapName("rightRear")
//            .IMU_HardwareMapName("imu")
//            .forwardPodY(-5)
//            .strafePodX(5)
////            .forwardEncoderDirection(Encoder.REVERSE) // Set after running localization test
////            .strafeEncoderDirection(Encoder.REVERSE) // Set after running localization test
////            .forwardTicksToInches(multiplier) // Calculated after running forward tuner
////            .strafeTicksToInches(multiplier) // Calculated after running lateral tuner
//            .IMU_Orientation(
//                    new RevHubOrientationOnRobot(
//                            RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
//                            RevHubOrientationOnRobot.UsbFacingDirection.UP
//                    )
//            );

}
