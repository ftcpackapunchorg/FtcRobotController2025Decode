//ONLY RUN THIS IF YOU HAVE ROADRUNNER INSTALLED AND SET UP PROPERLY

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import java.util.List;

@TeleOp(name="AprilTag Reader with Roadrunner")
public class AprilTagDetect_RRExample2 extends LinearOpMode {

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    // Roadrunner added
    private SampleMecanumDrive drive;

    //Look at Comments on AprilTagDetect.java for explanation of april tag + vision portal stuff
    @Override
    public void runOpMode() {
        aprilTag = new AprilTagProcessor.Builder().build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();

        // Roadrunner init
        drive = new SampleMecanumDrive(hardwareMap);
        // Set starting pose --> In this case, robot thinks its at (0,0)
        Pose2d startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);

        waitForStart();

        // Detect tag (look at AprilTagDetect.java for explanation)
        int tagId = 0;
        while (opModeIsActive() && tagId == 0) {
            List<AprilTagDetection> detections = aprilTag.getDetections();

            if (detections.isEmpty()) {
                telemetry.addData("Status", "No tag detected");
            } else {
                tagId = detections.get(0).id;
                telemetry.addData("Tag ID", tagId);
            }

            telemetry.update();
        }
        switch (tagId) {
            case 20:
                executeSequence20();
                break;
            case 21:
                executeSequence21();
                break;
            default:
                executeDefaultSequence();
                break;
        }

        telemetry.addData("Status", "Complete!");
        telemetry.update();
    }

    // Tag 20: Move back, turn left 45°, strafe left, move forward, activate intake
    private void executeSequence20() {
        telemetry.addData("Action", "Color Sequence Purple Purple Green");
        telemetry.update();

        Pose2d currentPose = drive.getPoseEstimate();

        // Move back 10 inches
        Trajectory moveBack = drive.trajectoryBuilder(currentPose)
                .back(10)
                .build();
        drive.followTrajectory(moveBack);

        // Turn left 45 degrees
        currentPose = drive.getPoseEstimate();
        drive.turn(Math.toRadians(45));

        // Strafe left 12 inches
        currentPose = drive.getPoseEstimate();
        Trajectory strafeLeft = drive.trajectoryBuilder(currentPose)
                .strafeLeft(12)
                .build();
        drive.followTrajectory(strafeLeft);

        // Move forward 15 inches
        currentPose = drive.getPoseEstimate();
        Trajectory moveForward = drive.trajectoryBuilder(currentPose)
                .forward(15)
                .build();
        drive.followTrajectory(moveForward);

        // Activate intake motor for 2 seconds
        intakeMotor.setPower(1.0);
        sleep(2000);
        intakeMotor.setPower(0);
    }

}
