import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import java.util.List;


//GOAL: Create an opmode that detects AprilTags and displays their IDs on the driver station.
//IDs: ID # provides sequencing for auto path
@TeleOp(name="AprilTag Identifier")
public class AprilTagDetect extends LinearOpMode /*I've extended LinearOpMode cause it'll be handy for future operations*/{

    //Init April Tag
    private AprilTagProcessor aprilTag;
    //Init Vision Portal (So you can see on the drivers Hub -- I think?)
    private VisionPortal visionPortal;

    //Standard runOpMode Method
    @Override
    public void runOpMode() {
        //creating + initializing the april tag
        aprilTag = new AprilTagProcessor.Builder().build();
        //creating + initializing the vision portal
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam")) //set the camera name on drivers hub to webcam
                //allowing it so that the camera can process the april tag
                .addProcessor(aprilTag)
                .build(); //build the vision portal

        waitForStart();

        while (opModeIsActive()) {
            List<AprilTagDetection> detections = aprilTag.getDetections();
            //create a list to store the detected april tags

            if (detections.isEmpty()) {
                telemetry.addData("Status", "No tag detected");
            } else {
                telemetry.addData("Tag ID", detections.get(0).id);
                //Every time a tag is detected, it will show the ID of the first tag detected,
                // and store it an the index 0 (first value) of the list
            }

            //update telemetry automatically
            // (cause you normally click the screen to refresh the vision portal, this should be easire)
            telemetry.update();
        }
    }
}