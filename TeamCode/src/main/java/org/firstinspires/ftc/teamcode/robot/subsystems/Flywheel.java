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
    int p= 0;

    int i = 0;
    int d= 0;
    private boolean isDisabled = false;

    /**
     * Constructs a Flywheel instance and configures the motor and PIDF coefficients.
     * @param hardwareMap HardwareMap for accessing hardware devices
     * @param telemetry Telemetry for reporting data
     * @param runtime ElapsedTime instance for timing
     */
    public Flywheel(HardwareMap hardwareMap, Telemetry telemetry, ElapsedTime runtime, String hardwareAddress) {
        this.telemetry = telemetry;
        this.runtime = runtime;

        // Initialize the flywheel motor
        flywheel = hardwareMap.get(DcMotorEx.class, hardwareAddress);
        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMotorEnable();
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p, i, d, 0));
    }
    @Override
    public void setDisabled(boolean disabled){
        if(disabled){setTargetSpeed(0);}
        isDisabled = disabled;
    }

    @Override
    public void update(){
        flywheel.setVelocity(targetSpeed);
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
        //This is the convesation factor for angular rate so you can change it from rotations per minute into encoder ticks per minute
        targetSpeed = 28*rotationsPerMinute*60;
    }

    /**
     * Updates the flywheel's speed control and records the current speed.
     */
    public void updateSpeedControl(){
        flywheel.setVelocity(targetSpeed);
        currentSpeed = flywheel.getVelocity();
    }
    public void setPIDFCoefficients(double p, double i, double d, double f){
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p, i, d,f));
    }

    /**
     * Checks if the flywheel is within the accuracy threshold of the target speed.
     * @return True if at speed, false otherwise
     */
    public boolean isAtSpeed(){
        return Math.abs(currentSpeed - targetSpeed)<accuracyThreshold;
    }
    public void postTelemetry(){
        telemetry.addLine("----Flywheel Data");
        telemetry.addData("FLYWHEELvelocity", currentSpeed);

    }

    public void setPIDFCoeffs(int p, int i, int d){
        this.p = p;
        this.i = i;
        this.d = d;
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p, i, d, 0));
    }
    public int getP() {
        return p;
    }
    public int getI(){
        return i;
    }
    public int getD(){
        return d;
    }
}