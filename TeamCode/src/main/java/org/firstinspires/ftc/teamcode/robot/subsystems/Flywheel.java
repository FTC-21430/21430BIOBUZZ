// Flywheel.java
// This class controls the flywheel motor, including PIDF configuration and speed control.
// It provides methods to set and get the flywheel's speed, and to check if the flywheel is at the target speed.

package org.firstinspires.ftc.teamcode.robot.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Flywheel subsystem for controlling a shooter or similar mechanism.
 * Handles PIDF configuration and velocity control using a DcMotorEx.
 */
@Config
public class Flywheel implements Subsystem {

    public static double  p = 70;
    public static double i = 0;
    public static double d = 30;
    public static double f = 13.5;

    // Hardware map for accessing robot hardware
    private HardwareMap hardwareMap = null;
    // Telemetry for reporting data to the driver station
    private Telemetry telemetry = null;
    // Timer for elapsed time tracking
    private ElapsedTime runtime = null;
    // Target speed for the flywheel (rotations per minute)
    private double targetSpeed = 0.0;
    // Current speed of the flywheel (rotations per minute)
    private double currentSpeed = 0.0;
    // The flywheel motor (DcMotorEx)
    private DcMotorEx flywheel = null;
    // Acceptable error threshold for speed (rotations per minute)
    private double accuracyThreshold;

    /**
     * Constructs a Flywheel instance and configures the motor and PIDF coefficients.
     * @param hardwareMap HardwareMap for accessing hardware devices
     * @param telemetry Telemetry for reporting data
     * @param runtime ElapsedTime instance for timing
     * @param P Proportional constant for PIDF
     * @param I Integral constant for PIDF
     * @param D Derivative constant for PIDF
     */
    public Flywheel(HardwareMap hardwareMap, Telemetry telemetry, ElapsedTime runtime, double P, double I, double D){
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.runtime = runtime;

        // Initialize the flywheel motor
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMotorEnable();
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        // Set PIDF coefficients for velocity control
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p,i,d,f));
    }
    @Override
    public void update(){
    }
    @Override
    public void setDisabled(boolean disabled){
        flywheel.setPower(0);
        return;
    }

    /**
     * Sets the accuracy threshold for determining if the flywheel is at speed.
     * @param threshold Acceptable error in rotations per minute
     */
    public void setAccuracyThreshold(double threshold){
        accuracyThreshold = threshold;
    }

    /**
     * Gets the current speed of the flywheel.
     * @return Current speed in rotations per minute
     */
    public double getCurrentSpeed(){
        return currentSpeed;
    }

    /**
     * Gets the target speed of the flywheel.
     * @return Target speed in rotations per minute
     */
    public double getTargetSpeed()
    {
        return targetSpeed;
    }

    /**
     * Sets the target speed for the flywheel and updates the motor velocity.
     * @param rotationsPerMinute Target speed in rotations per minute
     */
    public void setTargetSpeed(double rotationsPerMinute){
        //This is the convesation factor for angular rate so you can get it in rotations per minute
        targetSpeed = 28*rotationsPerMinute*60;
        flywheel.setVelocity(targetSpeed);
    }

    /**
     * Updates the flywheel's speed control and records the current speed.
     */
    public void updateSpeedControl(){
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p,i,d,f));

        flywheel.setVelocity(targetSpeed);
        currentSpeed = flywheel.getVelocity();
        telemetry.addData("FLYWHEELvelocity", currentSpeed);
    }

    /**
     * Checks if the flywheel is within the accuracy threshold of the target speed.
     * @return True if at speed, false otherwise
     */
    public boolean isAtSpeed(){
        return Math.abs(currentSpeed - targetSpeed)<accuracyThreshold;
    }

}