package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.broombots.sweep.Classes.Subsystem;

public class TurretLauncher implements Subsystem {
    private LauncherRamp launcherRamp;
    private Flywheel flywheel;
    private Turret turret;
    private boolean isDisabled = false;
        public TurretLauncher(LauncherRamp launcherRamp, Flywheel flywheel, Turret turret) {
                this.launcherRamp = launcherRamp;
                this.flywheel = flywheel;
                this.turret = turret;
        }
        public void update(){
            this.launcherRamp.update();
            this.flywheel.update();
            this.turret.update();
        }
    @Override
    public void setDisabled(boolean disabled){
        isDisabled = disabled;
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