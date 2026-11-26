package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PollenLauncher extends TurretLauncher{
    public PollenLauncher(HardwareMap hardwareMap, Telemetry telemetry){
        //TODO still need to tune these PID values once we start testing with actual robot
        super(new LauncherRamp(hardwareMap,"pollenTurretRampServo",270,40,10),new PollenFlywheel(hardwareMap,telemetry,new ElapsedTime()),new Turret(hardwareMap,"pollenTurretServo","pollenTurretEncoder","pollenTurretBeam",4000*2.75,0,0,0,5,420,0.1));
    }
}
