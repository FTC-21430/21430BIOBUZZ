package org.firstinspires.ftc.teamcode.robot.subsystems.launcher;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PollenFlywheel extends Flywheel {
    private final int POLLEN_P  = 70;
    private final int POLLEN_I = 65;
    private final int POLLEN_D = 45;
    private final int POLLEN_F = 0;

    public PollenFlywheel(HardwareMap hardwareMap, Telemetry telemetry,ElapsedTime elapsedTime){
        super(hardwareMap,telemetry, elapsedTime, "pollen_flywheel",true);
        setPIDFCoefficients(POLLEN_P,POLLEN_I,POLLEN_D,POLLEN_F);
    }
}
