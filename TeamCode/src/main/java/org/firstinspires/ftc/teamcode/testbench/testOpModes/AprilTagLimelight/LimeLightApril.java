package org.firstinspires.ftc.teamcode.testbench.testOpModes.AprilTagLimelight;




import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
public class LimeLightApril extends OpMode{

    private Limelight3A limelight3A;

    @Override
    public void init(){
        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        limelight3A.pipelineSwitch(8);// april tag 12 pipeline
    }
    @Override
    public void start(){
        limelight3A.start();
    }

    @Override
    public void loop(){
        LLResult llResult = limelight3A.getLatestResult();
        if (llResult)
    }
}
