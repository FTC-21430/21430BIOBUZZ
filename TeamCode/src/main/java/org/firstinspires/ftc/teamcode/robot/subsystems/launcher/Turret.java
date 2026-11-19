package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import android.util.Log;

import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.resources.PIDController;
import org.firstinspires.ftc.teamcode.robot.subsystems.BeamBreakSensor;
/** Controls turret aiming and homing using an encoder and beam-break sensor. */
@SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantSuppression"})
public class Turret implements Subsystem {
    private final Servo turretServo;
    private final DcMotor turretEncoder;
    private final BeamBreakSensor homingSensor;
    private final PIDController servoPidController;
    private boolean isDisabled = false;
    private boolean isHoming = false;
    private final double HOMING_DIRECTION = 0.1; //TODO: find a good homing power, this is just a guess https://github.com/FTC-21430/21430BIOBUZZ/issues/37
    private final double STOP_SPEED = 0.5; // Neutral continuous-rotation servo position.
    private final double GOBILDA_TURRET_GEAR_RATIO = 2.75; // 2.75:1 ratio of turret to servo
    private final double ENCODER_TICKS_PER_REV = 4000; // 4000 ticks per revolution of the encoder
    private final double ENCODER_TICKS_TO_DEGREES_RATIO = 360/(ENCODER_TICKS_PER_REV*GOBILDA_TURRET_GEAR_RATIO);
    private final double turretROM; // degrees of rotation of the turret, this is a guess and needs to be measured
    private final double turretZeroOffset = 0; // degrees of offset from the zero position of the turret should be zero but...
    private final double errorTolerance; // degrees
    private double currentAngle; // degrees, sampled during update



    /**
     * Configures the turret hardware and PID controller.
     *
     * @param hardwareMap hardware devices available to the robot
     * @param servoHardwareAddress configured name of the turret servo
     * @param encoderHardwareAddress configured name of the turret encoder
     * @param beamBreakSensorHardwareAddress configured name of the homing sensor
     * @param encoderTicksToRevOfTurret encoder ticks per turret revolution; currently unused
     * @param pConstant proportional PID coefficient
     * @param iConstant integral PID coefficient
     * @param dConstant derivative PID coefficient
     * @param errorTolerance acceptable aiming error in degrees
     * @param ROM turret range of motion in degrees
     */
    public Turret(HardwareMap hardwareMap, String servoHardwareAddress, String encoderHardwareAddress, String beamBreakSensorHardwareAddress, double encoderTicksToRevOfTurret, double pConstant, double iConstant, double dConstant, double errorTolerance, double ROM) {
        this.errorTolerance = errorTolerance;
        this.turretROM = ROM;
        turretServo = hardwareMap.get(Servo.class, servoHardwareAddress);
        turretEncoder = hardwareMap.get(DcMotor.class, encoderHardwareAddress);
        homingSensor = new BeamBreakSensor(beamBreakSensorHardwareAddress, hardwareMap, false);
        turretEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        servoPidController = new PIDController(pConstant, iConstant, dConstant, new ElapsedTime(),false);
        currentAngle = 0;
    }

    /**
     * Sets whether the turret is disabled and cancels homing.
     *
     * @param disabled whether to stop and disable servo commands
     */
    public void setDisabled(boolean disabled){
        if(disabled){turretServo.setPosition(0.5);}
        isDisabled = disabled;
        isHoming = false;
    }
    /** Samples the encoder and homing sensor, then controls the servo if enabled. */
    public void update(){
        // Convert encoder ticks to degrees and apply the zero offset.
        currentAngle = (turretEncoder.getCurrentPosition() * ENCODER_TICKS_TO_DEGREES_RATIO)-turretZeroOffset;
        servoPidController.update(getTurretAngle());
        homingSensor.update();
        if(isDisabled){return;}
        if(isHoming){
            if(homingSensor.isBeamBroken()){
                // The beam marks the homing position; stop and reset the encoder there.
                turretServo.setPosition(STOP_SPEED);
                turretEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                turretEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                isHoming = false;
                currentAngle = 0;
            }else{
                turretServo.setPosition(STOP_SPEED + HOMING_DIRECTION);
            }
            return;
        }
        turretServo.setPosition(scaleToPWMPower(servoPidController.getPower()));
    }
    /**
     * Sets the nearest equivalent target angle within the turret range.
     *
     * @param angle requested direction in degrees
     * @throws IllegalArgumentException if the angle is not finite
     */
    public void setTurretAngle(double angle){
        servoPidController.setTarget(forceToTurretRange(angle));
    }
    /**
     * Returns the angle sampled during the most recent update.
     *
     * @return turret angle in degrees
     */
    public double getTurretAngle(){
        return currentAngle;
    }
    /**
     * Updates the turret PID coefficients.
     *
     * @param p proportional coefficient
     * @param i integral coefficient
     * @param d derivative coefficient
     */
    public void setPIDCoefficients(double p, double i, double d){
        servoPidController.updatePIDConstants(p,i,d);
    }
    /**
     * Checks the sampled angle against the target and configured tolerance.
     *
     * @return whether the turret is within the tolerance
     */
    public boolean isAtAngle(){
        return Math.abs(getTurretAngle() - servoPidController.getTarget()) <= errorTolerance;
    }
    /**
     * Returns the current PID target.
     *
     * @return target angle in degrees
     */
    public double getTarget(){
        return servoPidController.getTarget();
    }
    /** Requests homing on subsequent enabled updates. */
    public void startHoming(){
        isHoming = true;
    }
    /**
     * Converts PID output from the range -1 to 1 to a servo position.
     *
     * @param power PID output
     * @return scaled servo position
     */
    private double scaleToPWMPower(double power){
        return (power/2)+0.5;
    }
    /**
     * Finds the nearest in-range equivalent of a requested direction.
     *
     * @param targetAngle requested direction in degrees
     * @return equivalent angle in degrees, or the previous target if none is found
     * @throws IllegalArgumentException if the angle is not finite
     */
    private double forceToTurretRange(double targetAngle){
        if (Double.isNaN(targetAngle)) throw new IllegalArgumentException("Target angle cannot be NaN");
        if (Double.isInfinite(targetAngle)) throw new IllegalArgumentException("Target angle cannot be infinite");

        final double MAX_TURRET_ANGLE = (turretROM/2) + turretZeroOffset;
        final double MIN_TURRET_ANGLE = -(turretROM/2) + turretZeroOffset;
        final int allowedRotations = 1; // number of full rotations allowed beyond the turret range

        double normalizedTarget = targetAngle % 360; // Remainder is between -360 and 360 degrees.

        double bestTarget = Double.NaN;
        double bestDistance = Double.MAX_VALUE;
        // Compare equivalent directions one revolution either side of the remainder.
        for (int i = -allowedRotations; i <= allowedRotations; i++) {
            double adjustedTarget = normalizedTarget + (i * 360);
            if (adjustedTarget >= MIN_TURRET_ANGLE && adjustedTarget <= MAX_TURRET_ANGLE && getDistanceToTarget(adjustedTarget) < bestDistance) {
                bestTarget = adjustedTarget;
                bestDistance = getDistanceToTarget(adjustedTarget);
            }
        }
        if (Double.isNaN(bestTarget)) {
            Log.w("Turret", "Target angle " + targetAngle + " is out of range. Returning Previous Target: " + servoPidController.getTarget());
            return servoPidController.getTarget();
        }

        return bestTarget;
    }
    /**
     * Calculates distance from the last sampled angle to a candidate target.
     *
     * @param targetAngle candidate angle in degrees
     * @return absolute difference in degrees
     */
    private double getDistanceToTarget(double targetAngle){
        return Math.abs(getTurretAngle() - targetAngle);
    }
}