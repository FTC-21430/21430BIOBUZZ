package org.firstinspires.ftc.teamcode.robot;

import com.broombots.sweep.Classes.Pos2D;
import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import static com.broombots.sweep.Classes.Robot.Alliance;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/** Coordinates ball handling and delegates shooting commands to {@link ShootingControl}. */
public class BallControl implements Subsystem {
    ShootingControl shootingControl;
    // intake
    // transfer
    // ball counter system

    /**
     * Creates the shooting controller for the ball-handling subsystem.
     *
     * @param hardwareMap hardware devices used by the shooting controller
     * @param telemetry telemetry supplied to the launchers
     * @param runtime timer supplied to the launchers
     * @param alliance robot alliance; not yet used
     */
    public BallControl(HardwareMap hardwareMap, Telemetry telemetry, ElapsedTime runtime, Alliance alliance) {
        shootingControl = new ShootingControl(hardwareMap,telemetry,runtime);
    }

    /**
     * Selects which types of balls may be shot.
     *
     * @param mode desired shooting mode
     */
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
    /**
     * Passes an aiming target to the shooting controller.
     *
     * @param pos target position
     * @param height target height
     */
    public void setTargetAimPoint(Pos2D pos, double height){
        shootingControl.setAimPosition(pos,height);
    }
    /** Initialization of the ball-handling components. and set launchers to their starting position, calibrate turret encoders */
    public void initialize() {
        // initialize intake, transfer, and ball counter systems
    }
    /**
     * Returns the placeholder ball count until the counter is implemented.
     *
     * @return zero
     */
    public int getBallCount() {
        // return the current ball count from the ball counter system
        return 0; // placeholder
    }
    private void checkForOpposingAllianceNectar() {
        // logic to check for opposing alliance balls in the intake or transfer system
    }
}
