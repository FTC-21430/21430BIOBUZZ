package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/** Launcher configured for the pollen hardware devices. */
public class PollenTurretLauncher extends GenericTurretLauncher {
    /** Servo range of motion used to configure the ramp. */
    public static double servoROM = 420;
    /** Ramp range of motion. */
    public static double rampROM = 1;
    /** Minimum ramp angle. */
    public static double minRampAngle = 37.5;
    /** Encoder ticks per turret revolution supplied to the turret constructor. */
    public static double encoderTicksToRevOfTurret = 4000*2.75;
    public static double turretP = 8.5e-3;
    public static double turretI = 9e-4;
    public static double turretD = 1e-5;
    public static double errorTolerance = 3.0; // degrees
    public static double turretROM = 420; //degrees of rotation for the turret
    public static double homingPower = -0.05;
    /**
     * Creates the pollen launcher using its configured hardware names.
     *
     * @param hardwareMap hardware devices available to the robot
     * @param telemetry telemetry supplied to the flywheel
     * @param runtime timer supplied to the flywheel
     */
    public PollenTurretLauncher(HardwareMap hardwareMap, Telemetry telemetry, ElapsedTime runtime) {
        super(
            new LauncherRamp(hardwareMap, "pollenLauncherRamp",servoROM,rampROM,minRampAngle),
            new Flywheel(hardwareMap, telemetry, runtime, "pollenFlywheel",true),
            new Turret(hardwareMap, "pollenTurretServo", "pollenTurretEncoder","pollenTurretBeam",encoderTicksToRevOfTurret,turretP,turretI,turretD,errorTolerance,turretROM,homingPower)
        );
    }
}