package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.broombots.sweep.Classes.Subsystem;

/** Combines a turret, adjustable ramp, and flywheel into a launcher subsystem. */
public class GenericTurretLauncher implements Subsystem{
        LauncherRamp launcherRamp;
        Flywheel flywheel;
        Turret turret;

    /**
     * Creates a launcher from its component subsystems.
     *
     * @param ramp launch-angle adjustment subsystem
     * @param flywheel flywheel subsystem
     * @param turret turret positioning subsystem
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

        /**
         * Sets the turret's target angle.
         *
         * @param angle target turret angle
         */
        public void setTurretAngle(double angle) {
            turret.setTurretAngle(angle);
        }
        /**
         * Sets the launch hood's target angle.
         *
         * @param angle target angle in degrees above horizontal
         */
        public void setHoodAngle(double angle) {
            launcherRamp.setLaunchAngle(angle);
        }

        /**
         * Sets the flywheel's target speed.
         *
         * @param speed requested speed in rotations per minute
         */
        public void setFlywheelSpeed(double speed) {
            flywheel.setTargetSpeed(speed);
        }

        /**
         * Returns the angle reported by the turret.
         *
         * @return current turret angle
         */
        public double getTurretAngle() {
            return turret.getTurretAngle();
        }
        /**
         * Returns the angle reported by the launch ramp.
         *
         * @return current hood angle in degrees
         */
        public double getHoodAngle() {
            return launcherRamp.getCurrentAngle();
        }

        /**
         * Returns the last speed recorded by the flywheel subsystem.
         *
         * @return recorded flywheel speed
         */
        public double getFlywheelSpeed() {
            return flywheel.getCurrentSpeed();
        }

        /**
         * Checks the readiness of all three launcher components.
         *
         * @return whether the flywheel, ramp, and turret are at their targets
         */
        public boolean isReadyToFire(){
            return flywheel.isAtSpeed() && launcherRamp.isAtTargetAngle() && turret.isAtAngle();
        }
}
