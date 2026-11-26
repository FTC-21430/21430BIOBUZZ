package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PollenLauncher extends TurretLauncher{
    public PollenLauncher(HardwareMap hardwareMap, Telemetry telemetry){
        //TODO still need to tune these PID values once we start testing with actual robot
        super(new LauncherRamp(hardwareMap,"launcher_ramp",20,30,40),new PollenFlywheel(hardwareMap,telemetry,new ElapsedTime()),new Turret(hardwareMap,"turret_servo","turret_encoder","beambreaker_servo",20,30,5,8,12,14));
    }
}
