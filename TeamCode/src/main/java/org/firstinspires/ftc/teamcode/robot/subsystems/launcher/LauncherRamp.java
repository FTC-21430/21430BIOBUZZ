package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.acmerobotics.dashboard.config.Config;
import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.resources.ServoPlus;

/** Controls the launcher ramp servo and its target launch angle. */
@Config
public class LauncherRamp implements Subsystem {

    // servo instance
    private ServoPlus rampServo = null;
    private double rampROM = 1;
    private double minRampAngle = 37.5;
    /** Ratio used when configuring the servo's range of motion. */
    public static double servoToRampRatio = (double) 1 / 6;
    private double maxRampAngle = minRampAngle + rampROM;
    /** Time in seconds used by {@link #isReady()}. */
    public static double timeout = 0.2;
    /** Position error tolerance used by the ramp's readiness checks. */
    public static double errorTolerance; //TODO: Find good error tolerance
    private ElapsedTime movementTimeout;
    private boolean disabled = false;
    private double targetAngle;

    /**
     * Creates the ramp controller and obtains its servo.
     *
     * @param hardwareMap hardware devices available to the robot
     * @param hardwareAddress configured name of the ramp servo
     * @param servoROM servo range of motion
     * @param rampROM ramp range of motion
     * @param minRampAngle minimum ramp angle
     */
    public LauncherRamp(HardwareMap hardwareMap, String hardwareAddress, double servoROM, double rampROM, double minRampAngle) {
        rampServo = new ServoPlus(hardwareMap.get(Servo.class, hardwareAddress), servoROM * servoToRampRatio, rampAngleToServo(minRampAngle), rampAngleToServo(maxRampAngle));
        movementTimeout = new ElapsedTime();
        this.rampROM = rampROM;
        this.minRampAngle = minRampAngle;
    }
    @Override
    public void setDisabled(boolean disable) {
        disabled = disable;
    }
    @Override
    public void update() {
        if (disabled) {return;}
        if (Math.abs(rampServo.getServoPos() - rampAngleToServo(targetAngle)) > errorTolerance){
            movementTimeout.reset();
        }
        rampServo.setServoPos(rampAngleToServo(targetAngle));
        servoToRampRatio = (double) 1 / 6;
        maxRampAngle = minRampAngle + rampROM;
    }

    /**
     * Sets the target launch angle, clipped to the configured range.
     *
     * @param angleUpFromHorizontal requested angle in degrees above horizontal
     */
    public void setLaunchAngle(double angleUpFromHorizontal){
        angleUpFromHorizontal = Range.clip(angleUpFromHorizontal, minRampAngle, maxRampAngle - 6);
        targetAngle = angleUpFromHorizontal;
    }

    /**
     * Sets the target to the minimum launch angle.
     */
    public void retract(){
        targetAngle = minRampAngle;
    }

    /**
     * Sets the target to the maximum launch angle.
     */
    public void extendFull(){
            targetAngle = maxRampAngle;
    }

    /**
     * Sets the target to half the difference between the maximum and minimum angles.
     */
    public void midAngle(){
            targetAngle = (maxRampAngle - minRampAngle)/2;
    }
    /**
     * Returns the angle calculated from the servo position and minimum ramp angle.
     *
     * @return calculated ramp angle
     */
    public double getCurrentAngle(){
            return  rampServo.getServoPos() + minRampAngle;
    }
    /**
     * Checks the current angle against the servo position using the error tolerance.
     *
     * @return whether the difference is less than the configured tolerance
     */
    public boolean isAtTargetAngle(){
        return Math.abs(getCurrentAngle() - rampServo.getServoPos()) < errorTolerance;
    }

    /**
     * Converts a launch angle to a servo position by subtracting the minimum angle.
     *
     * @param angle launch angle in degrees above horizontal
     * @return angle relative to the minimum ramp angle
     */
    private double rampAngleToServo(double angle){
            return  angle - minRampAngle;
    }
    /**
     * Checks whether the movement timeout has elapsed.
     *
     * @return whether the timer has reached {@link #timeout}
     */
    public boolean isReady(){
        return movementTimeout.seconds() >= timeout;
    }
}