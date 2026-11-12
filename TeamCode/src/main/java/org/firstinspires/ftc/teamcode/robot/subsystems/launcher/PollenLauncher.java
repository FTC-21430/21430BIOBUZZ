package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PollenLauncher extends TurretLauncher{
    public PollenLauncher(HardwareMap hardwareMap, Telemetry telemetry){
        super(new LauncherRamp(hardwareMap),new PollenFlywheel(hardwareMap,telemetry),new Turret(hardwareMap,9,9,9,"turret_servo","turret_encoder"));
    }
}
