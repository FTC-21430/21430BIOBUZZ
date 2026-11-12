package org.firstinspires.ftc.teamcode.robot.subsystems;

// Written by Tobin

import com.acmerobotics.dashboard.config.Config;
import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.Resources.ServoPlus;

/**
 * Firmware to control the ramp of the launcher. Uses the ServoPlus resource to ensure easy logic.
 * TODO: Test and double check all of the constants, they were guessed based on the CAD and the specs of a Gobilda Torque Servo as found here https://www.gobilda.com/2000-series-dual-mode-servo-25-2-torque on 11/6/2025 - Tobin
 */
@Config
public class LauncherRamp implements Subsystem {

    // servo instance
    private ServoPlus rampServo = null;


    //TODO: as said on line 13, All of these values have been guesstimated by Tobin based on the CAD models.

    // the full range of motion of the servo - note that this is usually a little different than what it is on the spec sheet, double check values.
    public static double pollenServoROM = 1;
    public static double nectarServoROM = 1;
    // The range of motion the ramp can move, from completely retracted to farthest mechanical limit
//    Took radius from pivot point to end of ramp and the circumference segment of the movable ramp segment. Took that ratio of full circumference and part to find ROM
    public static double pollenRampROM = 1;
    public static double nectarRampROM = 1;
    // The gear ratio between the launcher hood and the servo gear - Provided by the chief engineer in training who designed this part on hardware.
    public static double pollenServoToRampRatio = (double) 1 / 6;
    public static double nectarServoToRampRatio = (double) 1 / 6;

    // The minimum angle of the ramp up from Horizontal
    // measured from CAD - eyeballed
    public static double pollenMinRampAngle = 37.5;
    public static double nectarMinRampAngle = 37.5;

    // The maximum angle of the ramp up from Horizontal used previous values
    private final double POLLEN_MAX_RAMP_ANGLE = pollenMinRampAngle + pollenRampROM;
    private final double NECTAR_MAX_RAMP_ANGLE = nectarMinRampAngle + nectarRampROM;
    public static double timeout = 0.2;
    private double errorTolerance; //TODO: Find good error tolerance
    private ElapsedTime movementTimeout;
    private boolean disabled = false;
    private double targetAngle = pollenMinRampAngle;
    private boolean isPollen;

    /**
     * Construct function for this class, gets the servo reference
     * @param hardwareMap used to get the hardware reference for the ramp servo
     */
    public LauncherRamp(HardwareMap hardwareMap, String ramp) {
        // All values are passed to the servo assuming that the servo's zero is the launcher's min.
        if (ramp.equals("pollenRamp")) {
            rampServo = new ServoPlus(hardwareMap.get(Servo.class, ramp), pollenServoROM * pollenServoToRampRatio, rampAngleToServo(pollenMinRampAngle), rampAngleToServo(POLLEN_MAX_RAMP_ANGLE));
            isPollen=true;
        } else {
            rampServo = new ServoPlus(hardwareMap.get(Servo.class, ramp), nectarServoROM * nectarServoToRampRatio, rampAngleToServo(nectarMinRampAngle), rampAngleToServo(NECTAR_MAX_RAMP_ANGLE));
            isPollen=false;
        }
        movementTimeout = new ElapsedTime();
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

    }

    /**
     * Sets the launch angle of the servo. will ensure correct range of input and convert angle to servo position.
     * @param angleUpFromHorizontal degrees above Horizontal the tangent line of the hood should be.
     */
    public void setLaunchAngle(double angleUpFromHorizontal){
        if (isPollen) {
            angleUpFromHorizontal = Range.clip(angleUpFromHorizontal, pollenMinRampAngle, POLLEN_MAX_RAMP_ANGLE - 6);
            targetAngle = angleUpFromHorizontal;
        } else {
            angleUpFromHorizontal = Range.clip(angleUpFromHorizontal, nectarMinRampAngle, NECTAR_MAX_RAMP_ANGLE - 6);
            targetAngle = angleUpFromHorizontal;
        }
    }

    /**
     * lowest launch angle, resting position
     */
    public void retract(){
        if (isPollen){
        targetAngle = pollenMinRampAngle;
        } else {
            targetAngle = nectarMinRampAngle;
        }
    }

    /**
     * Highest launch angle
     */
    public void extendFull(){
        if (isPollen) {
            targetAngle = POLLEN_MAX_RAMP_ANGLE;
        } else {
            targetAngle = NECTAR_MAX_RAMP_ANGLE;
        }
    }

    /**
     * right in between the limits of launch angles
     */
    public void midAngle(){
        if (isPollen){
            targetAngle = (POLLEN_MAX_RAMP_ANGLE - pollenMinRampAngle)/2;
        } else {
            targetAngle = (NECTAR_MAX_RAMP_ANGLE - pollenMinRampAngle)/2;
        }
    }
    public double getCurrentAngle(){
        if (isPollen) {
            return rampServo.getServoPos() + pollenMinRampAngle;
        } else {
            return  rampServo.getServoPos() + nectarMinRampAngle;
        }
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
        if (isPollen){
            return angle - pollenMinRampAngle;
        } else {
            return  angle - pollenMinRampAngle;
        }
    }
    public boolean isReady(){
        return movementTimeout.seconds() >= timeout;
    }
}