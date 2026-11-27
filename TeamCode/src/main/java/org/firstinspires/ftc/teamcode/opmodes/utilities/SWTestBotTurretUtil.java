package org.firstinspires.ftc.teamcode.opmodes.utilities;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Utility;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.Turret;

@Utility
@Config
public class SWTestBotTurretUtil extends LinearOpMode {
    private Turret turret;
    public static double P = 0.0085;
    public static double I = 0.0009;
    public static double D = 1e-5;
    public static double errorTolerance = 0.5;
    public static double ROM = 420;
    public static double homingPower = -0.05;
    public static double PWMPower = 0.5;
    public static double targetAngle = 0;
    public void initTurret(){
        turret = new Turret(hardwareMap, "pollenTurretServo", "nectarTurretEncoder","turretBeam",4000*2.75, P, I, D, errorTolerance, ROM, homingPower);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        Servo servo = hardwareMap.get(Servo.class, "pollenTurretServo");
        servo.setPosition(PWMPower);
        initTurret();
        waitForStart();
        turret.startHoming();
        while (opModeIsActive()){
            if (gamepad1.circleWasPressed()){
                turret.setTurretAngle(0);
            }
            if (gamepad1.crossWasPressed()){
                turret.setTurretAngle(90);
            }
            if (gamepad1.squareWasPressed()){
                turret.setTurretAngle(-90);
            }
            if (gamepad1.triangleWasPressed()){
                turret.setTurretAngle(targetAngle);
            }
            turret.setPIDCoefficients(P,I,D);
            turret.update();
            telemetry.addData("isHoming", turret.isHoming());
            telemetry.addData("Turret Position", turret.getTurretAngle());
            telemetry.addData("turret target", turret.getTarget());
            telemetry.update();

        }
    }
}
