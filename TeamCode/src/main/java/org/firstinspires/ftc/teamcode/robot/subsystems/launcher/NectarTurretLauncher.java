package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class NectarTurretLauncher implements Subsystem {
    private LauncherRamp launcherRamp;
    private Flywheel flywheel;
    private Turret turret;
    public NectarTurretLauncher(HardwareMap hardwareMap, Telemetry telemetry, ElapsedTime runtime) {
        this.launcherRamp = new LauncherRamp(hardwareMap, "nectarLauncherRamp");
        this.flywheel = new Flywheel(hardwareMap, telemetry, runtime, "nectarFlywheel");
        this.turret = new Turret(hardwareMap, "nectarTurretServo", "nectarTurretEncoder");
    }
    @Override
    public void setDisabled(boolean disable) {
        turret.setDisabled(disable);
        launcherRamp.setDisabled(disable);
        flywheel.setDisabled(disable);
    }

    @Override
    public void update() {
        turret.update();
        launcherRamp.update();
        flywheel.update();
    }

    public void setTurretAngle(double angle) {
        turret.setTurretAngle(angle);
    }
    public void setHoodAngle(double angle) {
        launcherRamp.setLaunchAngle(angle);
    }

    public void setFlywheelSpeed(double speed) {
        flywheel.setTargetSpeed(speed);
    }

    public double getTurretAngle() {
        return turret.getTurretAngle();
    }
    public double getHoodAngle() {
        return launcherRamp.getCurrentAngle();
    }

    public double getFlywheelSpeed() {
        return flywheel.getCurrentSpeed();
    }

    public boolean isReadyToFire(){
        return flywheel.isAtSpeed() && launcherRamp.isAtTargetAngle() && turret.isAtAngle();
    }


}