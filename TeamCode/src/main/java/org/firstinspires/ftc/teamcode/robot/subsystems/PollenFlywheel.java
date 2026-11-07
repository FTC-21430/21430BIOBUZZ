package org.firstinspires.ftc.teamcode.robot.subsystems;

import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PollenFlywheel extends Flywheel {
    private final int POLLEN_P  = 70;
    private final int POLLEN_I = 65;
    private final int POLLEN_D = 45;

    public PollenFlywheel(HardwareMap hardwareMap, Telemetry telemetry){
        super(hardwareMap,telemetry,new ElapsedTime(), "pollen_flywheel");
        setPIDFCoeffs(POLLEN_P,POLLEN_I,POLLEN_D);
    }
}
