package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.resources.PIDController;

public class Turret implements Subsystem {
    private Servo turretServo;
    private DcMotor turretEncoder;
    private PIDController servoPidController;
    private double servoP = 0;
    private double servoI = 0;
    private double servoD = 0;
    private boolean isDisabled = false;

    /**
     * @param hardwareMap used for servo and encoder access
     * @param servoHardwareAddress used to access servo on HardwareMap
     * @param encoderHardwareAddress used to access encoder on HardwareMap
     * @param encoderTicksToRevOfTurret used to convert encoder to useful rev data
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
    public void setTurretAngle(double angle){servoPidController.setTarget(angle);
    }
    public double getTurretAngle(){
        //Calculation is 4000 encoder ticks to a rotation of the turret, a rotation of the turret is 2.75 rotations of the servo, and dividing by 360 gets it into degrees
        final double ECODER_TICKS_TO_DEGREES_RATIO = (4000*2.75)/360; //TODO: double check that this is accurate, any inconsistency is important
        return turretEncoder.getCurrentPosition()* ECODER_TICKS_TO_DEGREES_RATIO;
    }
    public void setServoCoefficients(double p, double i, double d){
        servoPidController.updatePIDConstants(p,i,d);
    }
    public boolean isAtAngle(){
        double errorTolerance = 2; //TODO: find a good error tolerance, this is just a guess
        return Math.abs(getTurretAngle() - servoPidController.getTarget()) <= errorTolerance;
    }
    public double getTarget(){
        return servoPidController.getTarget();
    }
}