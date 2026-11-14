package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Resources.PIDController;

/** Controls the launcher turret using a servo, encoder, and PID controller. */
public class Turret implements Subsystem {
    private Servo turretServo;
    private DcMotor turretEncoder;
    private PIDController servoPidController;
    private double servoP = 0;
    private double servoI = 0;
    private double servoD = 0;
    private boolean isDisabled = false;

    /**
     * Creates a turret controller and initializes its hardware.
     *
     * @param hardwareMap used for servo and encoder access
     * @param servoHardwareAddress used to access servo on HardwareMap
     * @param encoderHardwareAddress used to access encoder on HardwareMap
     * @param encoderTicksToRevOfTurret encoder ticks per turret revolution; not yet used
     */
    public Turret(HardwareMap hardwareMap, String servoHardwareAddress, String encoderHardwareAddress, double encoderTicksToRevOfTurret){
        turretServo = hardwareMap.get(Servo.class, servoHardwareAddress);
        turretEncoder = hardwareMap.get(DcMotor.class, encoderHardwareAddress);
        turretEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        servoPidController = new PIDController(servoP, servoI, servoD, new ElapsedTime());
    }
    public void setDisabled(boolean disabled){
;       if(disabled){turretServo.setPosition(0.5);}
        isDisabled = disabled;

    }
    public void update(){
        servoPidController.update(getTurretAngle());
        if(isDisabled){return;}
        turretServo.setPosition(((servoPidController.getPower())/2)+0.5);
    }
    /**
     * Sets the turret PID controller's target angle.
     *
     * @param angle target angle
     */
    public void setTurretAngle(double angle){servoPidController.setTarget(angle);
    }
    /**
     * Calculates a turret angle from the encoder position.
     *
     * @return calculated turret angle
     */
    public double getTurretAngle(){
        //Calculation is 4000 encoder ticks to a rotation of the turret, a rotation of the turret is 2.75 rotations of the servo, and dividing by 360 gets it into degrees
        final double ECODER_TICKS_TO_DEGREES_RATIO = (4000*2.75)/360; //TODO: double check that this is accurate, any inconsistency is important
        return turretEncoder.getCurrentPosition()* ECODER_TICKS_TO_DEGREES_RATIO;
    }
    /**
     * Updates the turret servo PID coefficients.
     *
     * @param p proportional coefficient
     * @param i integral coefficient
     * @param d derivative coefficient
     */
    public void setServoCoefficients(double p, double i, double d){
        servoPidController.updatePIDConstants(p,i,d);
    }
    /**
     * Checks the calculated angle against the target using the current tolerance.
     *
     * @return whether the turret is within the tolerance
     */
    public boolean isAtAngle(){
        double errorTolerance = 2; //TODO: find a good error tolerance, this is just a guess
        return Math.abs(getTurretAngle() - servoPidController.getTarget()) <= errorTolerance;
    }
    /**
     * Returns the turret PID controller's target.
     *
     * @return target turret angle
     */
    public double getTarget(){
        return servoPidController.getTarget();
    }
}