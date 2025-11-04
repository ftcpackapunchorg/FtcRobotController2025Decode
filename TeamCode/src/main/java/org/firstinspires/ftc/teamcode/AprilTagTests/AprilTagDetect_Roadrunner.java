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
public class AprilTagDetect_Roadrunner extends LinearOpMode {

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

        // Move based on tag ID --> If tag is odd, move left; if even, move right
        // Uses Modulus operator to determine odd/even. Ex: 5 % 2 = 1 (odd), 4 % 2 = 0 (even)

        // Creates trajectory
        Trajectory Path;

        if (tagId % 2 == 1) {
            // Odd - move left
            // Starts trajectory from startPose defined earlier
            Path = drive.trajectoryBuilder(startPose)
                    .strafeLeft(10)
                    .build();
            //Telemetery to explain action
            telemetry.addData("Action", "Moving LEFT");
        } else {
            // Even - move right
            // Starts trajectory from startPose defined earlier
            Path = drive.trajectoryBuilder(startPose)
                    .strafeRight(10)
                    .build();
            //Telemetery to explain action
            telemetry.addData("Action", "Moving RIGHT");
        }

        //Update Telemetry before trajectory execution
        telemetry.update();
        // Execute 'trajectory' aka the RoadRunner movement (of moving left or right) based on tag ID
        drive.followTrajectory(Path);

        // Indicate completion of trajectory after trajectory executes
        telemetry.addData("Status", "Complete!");
        // Final telemetry update
        telemetry.update();
    }
}