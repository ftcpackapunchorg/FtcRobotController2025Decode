package org.firstinspires.ftc.teamcode.mainbot.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mainbot.utils.MainBotConstants;

public final class MainBotLaunchMechanism {

    public final DcMotorEx launcher;

    final double FEED_TIME_SECONDS = 0.20; //The feeder servos run this long when a shot is requested.
    final double STOP_SPEED = 0.0; //We send this power to the servos when we want them to stop.
    final double FULL_SPEED = 1.0;

    /*
     * When we control our launcher motor, we are using encoders. These allow the control system
     * to read the current speed of the motor and apply more or less power to keep it at a constant
     * velocity. Here we are setting the target, and minimum velocity that the launcher should run
     * at. The minimum velocity is a threshold for determining when to fire.
     */
    final double LAUNCHER_TARGET_VELOCITY = 1125;
    final double LAUNCHER_MIN_VELOCITY = 1075;
    final double LAUNCHER_REVERSE_VELOCITY = 300;

    ElapsedTime feederTimer = new ElapsedTime();

    /** Auto related **/

    final double FEED_TIME = 0.20;
    final double TIME_BETWEEN_SHOTS = 2;
    final double REVERSE_ROTATION_TIME = 0.1;

    /*
     * TECH TIP: State Machines
     * We use a "state machine" to control our launcher motor and feeder servos in this program.
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

    private enum LaunchState {
        IDLE,
        SPIN_UP,
        LAUNCH,
        LAUNCHING,
    }

    /*
     * Here we create three timers which we use in different parts of our code. Each of these is an
     * "object," so even though they are all an instance of ElapsedTime(), they count independently
     * from each other.
     */
    private ElapsedTime shotTimer = new ElapsedTime();
    private ElapsedTime autoFeederTimer = new ElapsedTime();
    private ElapsedTime reverseLaunchTimer = new ElapsedTime();

    private enum AutoLaunchState { IDLE, PREPARE, LAUNCH }

    private LaunchState launchState;

    public LaunchState getLaunchState() {
        return launchState;
    }

    public AutoLaunchState getAutoLaunchState() {
        return autoLaunchState;
    }

    private AutoLaunchState autoLaunchState;

    public MainBotLaunchMechanism(HardwareMap hardwareMap, Telemetry telemetry) {

        launcher = hardwareMap.get(DcMotorEx.class, MainBotConstants.LAUNCHER_ONE_TO_ONE_RATIO_MOTOR_NAME);

        launchState = LaunchState.IDLE;

        autoLaunchState = AutoLaunchState.IDLE;

        /*
         * Here we set our launcher to the RUN_USING_ENCODER runmode.
         * If you notice that you have no control over the velocity of the motor, it just jumps
         * right to a number much higher than your set point, make sure that your encoders are plugged
         * into the port right beside the motor itself. And that the motors polarity is consistent
         * through any wiring.
         */
        launcher.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        launcher.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        launcher.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));

//        launcher.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(0, 0, 0, 13));
    }

    public void launch(boolean shotRequested) {
        switch (launchState) {
            case IDLE:
                if (shotRequested) {
                    launchState = LaunchState.SPIN_UP;
                }
                break;
            case SPIN_UP:
                launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {
                    launchState = LaunchState.LAUNCH;
                }
                break;
            case LAUNCH:
                //         drive.leftFeeder.setPower(FULL_SPEED);
                //         drive.rightFeeder.setPower(FULL_SPEED);
//                feederTimer.reset();
                launchState = LaunchState.LAUNCHING;
                break;
            case LAUNCHING:
                if (feederTimer.seconds() > FEED_TIME_SECONDS) {
                    launchState = LaunchState.IDLE;
                    //         drive.leftFeeder.setPower(STOP_SPEED);
                    //        drive.rightFeeder.setPower(STOP_SPEED);
                }
                break;
        }
    }

    /**
     * Launches one ball, when a shot is requested spins up the motor and once it is above a minimum
     * velocity, runs the feeder servos for the right amount of time to feed the next ball.
     * @param shotRequested "true" if the user would like to fire a new shot, and "false" if a shot
     *                      has already been requested and we need to continue to move through the
     *                      state machine and launch the ball.
     * @return "true" for one cycle after a ball has been successfully launched, "false" otherwise.
     */
    public boolean launchForAuto(boolean shotRequested){
        switch (autoLaunchState) {
            case IDLE:
                if (shotRequested) {
                    autoLaunchState = AutoLaunchState.PREPARE;
                    shotTimer.reset();
                }
                break;
            case PREPARE:
                launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_MIN_VELOCITY){
                    autoLaunchState = AutoLaunchState.LAUNCH;
//                    feederMechanism.leftFeeder.setPower(1);
//                    feederMechanism.rightFeeder.setPower(1);
                    autoFeederTimer.reset();
                }
                break;
            case LAUNCH:
                if (autoFeederTimer.seconds() > FEED_TIME) {
//                    feederMechanism.leftFeeder.setPower(0);
//                    feederMechanism.rightFeeder.setPower(0);

                    if(shotTimer.seconds() > TIME_BETWEEN_SHOTS){
                        stopLauncher();
                        autoLaunchState = AutoLaunchState.IDLE;
                        return true;
                    }
                }
        }
        return false;
    }

    public void startLauncher() {

        launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
//        launcher.setPower(.7);
    }

    public void stopLauncher() {

        launcher.setVelocity(STOP_SPEED);
//        launcher.setPower(0);
    }

    public void reverseLauncher() {

        launcher.setVelocity(-1 * LAUNCHER_REVERSE_VELOCITY);
//        launcher.setPower(-1 * 0.1);
    }
}
