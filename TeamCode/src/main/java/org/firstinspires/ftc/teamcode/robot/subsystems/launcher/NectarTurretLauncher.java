package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class NectarTurretLauncher extends GenericTurretLauncher {
    public static double servoROM = 1;
    public static double rampROM = 1;
    public static double minRampAngle = 37.5;
    public static double encoderTicksToRevOfTurret = 4000*2.75;

    /**
     * @param hardwareMap given to Turret.java, Flywheel.java, and Turret.java to access motors, servos, and encoders
     * @param telemetry given ot Flywheel.java for use of telemetry
     * @param runtime given to Flywheel.java for delta time calculations
     */
    public NectarTurretLauncher(HardwareMap hardwareMap, Telemetry telemetry, ElapsedTime runtime) {
        super(new LauncherRamp(hardwareMap, "nectarLauncherRamp",servoROM,rampROM,minRampAngle),
                new Flywheel(hardwareMap, telemetry, runtime, "nectarFlywheel"),
                new Turret(hardwareMap, "nectarTurretServo", "nectarTurretEncoder",encoderTicksToRevOfTurret)
        );
    }
}