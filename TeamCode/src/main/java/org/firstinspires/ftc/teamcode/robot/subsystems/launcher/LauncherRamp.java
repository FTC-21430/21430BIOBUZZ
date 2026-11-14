package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.acmerobotics.dashboard.config.Config;
import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.Resources.ServoPlus;

@Config
public class LauncherRamp implements Subsystem {

    // servo instance
    private ServoPlus rampServo = null;
    private double rampROM = 1;
    private double minRampAngle = 37.5;
    public static double servoToRampRatio = (double) 1 / 6;
    private double maxRampAngle = minRampAngle + rampROM;
    public static double timeout = 0.2;
    public static double errorTolerance; //TODO: Find good error tolerance
    private ElapsedTime movementTimeout;
    private boolean disabled = false;
    private double targetAngle;

    /**
     * Construct function for this class, gets the servo reference
     * @param hardwareMap used to get the hardware reference for the ramp servo
     * @param hardwareAddress gives String used to call the ramp servo
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
     * Sets the launch angle of the servo. will ensure correct range of input and convert angle to servo position.
     * @param angleUpFromHorizontal degrees above Horizontal the tangent line of the hood should be.
     */
    public void setLaunchAngle(double angleUpFromHorizontal){
        angleUpFromHorizontal = Range.clip(angleUpFromHorizontal, minRampAngle, maxRampAngle - 6);
        targetAngle = angleUpFromHorizontal;
    }

    /**
     * lowest launch angle, resting position
     */
    public void retract(){
        targetAngle = minRampAngle;
    }

    /**
     * Highest launch angle
     */
    public void extendFull(){
            targetAngle = maxRampAngle;
    }

    /**
     * right in between the limits of launch angles
     */
    public void midAngle(){
            targetAngle = (maxRampAngle - minRampAngle)/2;
    }
    public double getCurrentAngle(){
            return  rampServo.getServoPos() + minRampAngle;
    }
    public boolean isAtTargetAngle(){
        return Math.abs(getCurrentAngle() - rampServo.getServoPos()) < errorTolerance;
    }

    /**
     * converts degrees over horizontal to something the servo can use by subtracting the offset that is the ramp's min angle.
     * @param angle degrees over horizontal
     * @return 1/6 the angle the servo needs to be at to align hood to angle.
     */
    private double rampAngleToServo(double angle){
            return  angle - minRampAngle;
    }
    public boolean isReady(){
        return movementTimeout.seconds() >= timeout;
    }
}