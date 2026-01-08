package org.firstinspires.ftc.teamcode.mainbot.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mainbot.utils.MainBotConstants;

public final class MainBotIntakeMechanism {

    public final DcMotorEx intake;

//    private MainBotIntakeFeederMechanism mainBotIntakeFeederMechanism;

    final double INTAKE_FEED_TIME_SECONDS = 10.0; //The feeder servos run this long when a shot is requested.

//    final double MIN_INTAKE_POWER = 0.0;

//    final double MAX_INTAKE_POWER = 1.0;

    /*
     * When we control our intake motor, we are using encoders. These allow the control system
     * to read the current speed of the motor and apply more or less power to keep it at a constant
     * velocity. Here we are setting the target, and minimum velocity that the intake should run
     * at. The minimum velocity is a threshold for determining when to fire.
     */
    final double INTAKE_TARGET_VELOCITY = 1825;
    final double INTAKE_MIN_VELOCITY = 1075;

    final double INTAKE_STOP_SPEED = 0;

    ElapsedTime intakeFeedTimer = new ElapsedTime();

    /** Auto related **/

    final double INTAKE_TIME = 3.0;
    final double TIME_BETWEEN_SHOTS = 2;

    /*
     * TECH TIP: State Machines
     * We use a "state machine" to control our intake motor and feeder servos in this program.
     * The first step of a state machine is creating an enum that captures the different "states"
     * that our code can be in.
     * The core advantage of a state machine is that it allows us to continue to loop through all
     * of our code while only running specific code when it's necessary. We can continuously check
     * what "State" our machine is in, run the associated code, and when we are done with that step
     * move on to the next state.
     * This enum is called the "LaunchState". It reflects the current condition of the shooter
     * motor and we move through the enum when the user asks our code to fire a shot.
     * It starts at idle, when the user requests a launch, we enter SPIN_UP where we get the
     * motor up to speed, once it meets a minimum speed then it starts and then ends the launch process.
     * We can use higher level code to cycle through these states. But this allows us to write
     * functions and autonomous routines in a way that avoids loops within loops, and "waits".
     */

    private enum IntakeState {
        IDLE,
        GET_READY,
        INTAKE,
        INTAKE_IN_PROGRESS,
        COMPLETE;
    }

    private enum AutoIntakeState {
        IDLE,
        GET_READY,
        INTAKE,
        INTAKE_IN_PROGRESS,
        COMPLETE;
    }

    /*
     * Here we create three timers which we use in different parts of our code. Each of these is an
     * "object," so even though they are all an instance of ElapsedTime(), they count independently
     * from each other.
     */
    private ElapsedTime intakeTimer = new ElapsedTime();
    private ElapsedTime autoIntakeFeederTimer = new ElapsedTime();

    private IntakeState intakeState = IntakeState.IDLE;

    public IntakeState getIntakeState() {
        return intakeState;
    }

    public AutoIntakeState getAutoIntakeState() {
        return autoIntakeState;
    }

    private AutoIntakeState autoIntakeState = AutoIntakeState.IDLE;

    public MainBotIntakeMechanism(HardwareMap hardwareMap, Telemetry telemetry) {

        intake = hardwareMap.get(DcMotorEx.class, MainBotConstants.INTAKE_ONE_TO_ONE_RATIO_MOTOR_NAME);

//        cadBotIntakeFeederMechanism = new MainBotIntakeFeederMechanism(hardwareMap, telemetry);

        intakeState = IntakeState.IDLE;

        autoIntakeState = AutoIntakeState.IDLE;


        /*
         * Here we set our intake to the RUN_USING_ENCODER runmode.
         * If you notice that you have no control over the velocity of the motor, it just jumps
         * right to a number much higher than your set point, make sure that your encoders are plugged
         * into the port right beside the motor itself. And that the motors polarity is consistent
         * through any wiring.
         */
        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

//        intake.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));

    }

    public void intakeAction(boolean intakeRequested) {
        switch (intakeState) {
            case IDLE:
                if (intakeRequested) {
                    intakeState = IntakeState.GET_READY;
                }
                break;
            case GET_READY:
                intakeFeedTimer.reset();
//                cadBotIntakeFeederMechanism.startIntakeFeeders();
                intakeState = IntakeState.INTAKE;
                break;
            case INTAKE:
                intake.setVelocity(INTAKE_TARGET_VELOCITY);
                if (intake.getVelocity() > INTAKE_MIN_VELOCITY) {
                    intakeState = IntakeState.INTAKE_IN_PROGRESS;
                }
                break;
            case INTAKE_IN_PROGRESS:
                if (intakeFeedTimer.seconds() > INTAKE_FEED_TIME_SECONDS) {
                    intake.setVelocity(INTAKE_MIN_VELOCITY);
                    intakeState = IntakeState.COMPLETE;
//                    cadBotIntakeFeederMechanism.stopIntakeFeeders();
                }
        }
    }

    /**
     * Launches one ball, when a shot is requested spins up the motor and once it is above a minimum
     * velocity, runs the feeder servos for the right amount of time to feed the next ball.
     * @param autoIntakeRequested "true" if the user would like to fire a new shot, and "false" if a shot
     *                      has already been requested and we need to continue to move through the
     *                      state machine and launch the ball.
     * @return "true" for one cycle after a ball has been successfully launched, "false" otherwise.
     */
    public boolean intakeForAuto(boolean autoIntakeRequested){
        switch (autoIntakeState) {
            case IDLE:
                if (autoIntakeRequested) {
                    autoIntakeState = AutoIntakeState.GET_READY;
//                    shotTimer.reset();
                }
                break;
            case GET_READY:
                autoIntakeFeederTimer.reset();
//                cadBotIntakeFeederMechanism.startIntakeFeeders();
                autoIntakeState = AutoIntakeState.INTAKE;
                break;
            case INTAKE:
                intake.setVelocity(INTAKE_TARGET_VELOCITY);
                if (intake.getVelocity() > INTAKE_MIN_VELOCITY) {
                    autoIntakeState = AutoIntakeState.INTAKE_IN_PROGRESS;
                }
                break;
            case INTAKE_IN_PROGRESS:
                if (autoIntakeFeederTimer.seconds() > INTAKE_FEED_TIME_SECONDS) {
                    intake.setVelocity(INTAKE_MIN_VELOCITY);
                    autoIntakeState = AutoIntakeState.COMPLETE;
//                    cadBotIntakeFeederMechanism.stopIntakeFeeders();
                }

        }
        return false;
    }

    public void startIntake() {

//        mainBotIntakeFeederMechanism.startIntakeFeeders();

        intake.setVelocity(INTAKE_TARGET_VELOCITY);
    }

    public void stopIntake() {

//        mainBotIntakeFeederMechanism.stopIntakeFeeders();
        intake.setVelocity(INTAKE_STOP_SPEED);
    }
}
