package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.broombots.sweep.Classes.Subsystem;

public class GenericTurretLauncher implements Subsystem{
        LauncherRamp launcherRamp;
        Flywheel flywheel;
        Turret turret;

    /**
     * @param ramp gets the ramp
     * @param flywheel gets the flywheel
     * @param turret gets the turret
     */
    public GenericTurretLauncher(LauncherRamp ramp, Flywheel flywheel, Turret turret) {
            this.launcherRamp = ramp;
            this.flywheel = flywheel;
            this.turret = turret;
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
