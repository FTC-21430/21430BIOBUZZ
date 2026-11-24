package org.firstinspires.ftc.teamcode.opmodes.utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.NectarTurretLauncher;
import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.PollenTurretLauncher;

//This class is the Test OpMode for teh Turret Launcher
@TeleOp
public class PollenTurretLauncherTesting extends LinearOpMode {
    @Override
    public void runOpMode()  {
        PollenTurretLauncher pollenTurretLauncher;
        pollenTurretLauncher = new PollenTurretLauncher(hardwareMap, telemetry, new ElapsedTime());
        NectarTurretLauncher nectarTurretLauncher;
        nectarTurretLauncher = new NectarTurretLauncher(hardwareMap, telemetry, new ElapsedTime());
        final double ROM = 420.0;

        waitForStart();

        while(opModeIsActive()){
            pollenTurretLauncher.update();
            if(gamepad1.square){
                pollenTurretLauncher.setTurretAngle(210);
            }else {
                pollenTurretLauncher.setTurretAngle(0);
            }
            telemetry.addData("Turret Angel",pollenTurretLauncher.getTurretAngle());
            telemetry.update();
        }
    }

}
