package org.firstinspires.ftc.teamcode.opmodes.utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Utility;

import org.firstinspires.ftc.teamcode.robot.subsystems.BeamBreakSensor;

/** Displays the turret beam-break state and detected edges for sensor testing. */
@Utility
public class TurretBeamBreakUtil extends LinearOpMode {
    /** Samples the inverted sensor and reports its state every half-second while active. */
    @Override
    public void runOpMode() throws InterruptedException {
        BeamBreakSensor turretBeamBreak = new BeamBreakSensor("turretBeam", hardwareMap, true);
        waitForStart();
        while(opModeIsActive()){
            turretBeamBreak.update();
            telemetry.addData("Turret Beam Break State", turretBeamBreak.isBeamBroken());
            telemetry.addData("Turret Rise Beam Break State", turretBeamBreak.isRisen());
            telemetry.addData("Turret Fall Beam Break State", turretBeamBreak.isFallen());
            sleep(500);
            telemetry.update();
        }
    }
}
