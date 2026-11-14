package org.firstinspires.ftc.teamcode.opmodes.utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.NectarTurretLauncher;
import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.PollenTurretLauncher;

/** Tests the pollen and nectar flywheels using the gamepad face buttons. */
@TeleOp
@SuppressWarnings("unused")
public class FlywheelTesting extends LinearOpMode {
    /** Requested flywheel speed in rotations per minute while a button is held. */
    public static int flywheelSpeedRPM= 250;
    @Override
        public void runOpMode()  {
        PollenTurretLauncher pollenTurretLauncher;
        pollenTurretLauncher = new PollenTurretLauncher(hardwareMap, telemetry, new ElapsedTime());

        NectarTurretLauncher nectarTurretLauncher;
        nectarTurretLauncher = new NectarTurretLauncher(hardwareMap, telemetry, new ElapsedTime());

        waitForStart();

        while(opModeIsActive()){
            if(gamepad1.circle){
                pollenTurretLauncher.setFlywheelSpeed(flywheelSpeedRPM);
            }else {
                pollenTurretLauncher.setFlywheelSpeed(0);
            }
            if(gamepad1.square){
                nectarTurretLauncher.setFlywheelSpeed(flywheelSpeedRPM);
            }else {
                nectarTurretLauncher.setFlywheelSpeed(0);
            }
        }

    }
    }