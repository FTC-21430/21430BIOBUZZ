package org.firstinspires.ftc.teamcode.robot.subsystems;

import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TransferSystem implements Subsystem {
    private HardwareMap hardwareMap = null;
    private Telemetry telemetry = null;
    private DcMotorEx transferSystem;
    private double constantMotorSpeed = 1;
//    private boolean motorDirection = true;
    private DcMotorSimple.Direction direction = DcMotorSimple.Direction.FORWARD;
    private boolean isDisabled  = false;
    


    public TransferSystem(HardwareMap hardwareMap, Telemetry telemetry){
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
    }

    @Override
    public void update() {
        transferSystem.setDirection(direction);
        transferSystem.setVelocity(20);
    }

    @Override
    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
        if(isDisabled){constantMotorSpeed = 0;}
    }

    public void spinningDirectionForward(){
      direction = DcMotorSimple.Direction.FORWARD;
    }
    public void spinningDirectionReverse(){
        direction = DcMotorSimple.Direction.REVERSE;
    }
    public void directionStop(){
        transferSystem.setPower(0);
    }
    public void postTelemetry(){
        telemetry.addLine("----DIRECTION");
        telemetry.addData("Direction",1);
    }
}
