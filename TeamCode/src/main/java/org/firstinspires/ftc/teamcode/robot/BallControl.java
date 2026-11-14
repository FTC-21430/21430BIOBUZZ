package org.firstinspires.ftc.teamcode.robot;

import com.broombots.sweep.Classes.Pos2D;
import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class BallControl implements Subsystem {
    ShootingControl shootingControl;
    // intake
    // transfer
    // ball counter system

    public BallControl(HardwareMap hardwareMap, Telemetry telemetry, ElapsedTime runtime, Alliance alliance) {
        shootingControl = new ShootingControl(hardwareMap,telemetry,runtime);
    }

    public void setShootingMode(ShootingControl.ShootingMode mode){
        shootingControl.setShootingMode(mode);
    }
    @Override
    public void update() {
        shootingControl.update();
    }
    @Override
    public void setDisabled(boolean disable) {
        shootingControl.setDisabled(disable);
    }
    public void setTargetAimPoint(Pos2D pos, double height){
        shootingControl.setAimPosition(pos,height);
    }
    public void initialize() {
        // initialize intake, transfer, and ball counter systems
    }
    public int getBallCount() {
        // return the current ball count from the ball counter system
        return 0; // placeholder
    }
    private void checkForOpposingAllianceNectar() {
        // logic to check for opposing alliance balls in the intake or transfer system
    }
}
