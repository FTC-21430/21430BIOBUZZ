package org.firstinspires.ftc.teamcode.opmodes.utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.BioBuzzRobot;

/** Tests the pollen and nectar flywheels using the gamepad face buttons. */
@TeleOp
public class FlywheelTesting extends LinearOpMode {
    /** Requested flywheel speed in rotations per minute while a button is held. */
    public static int flywheelSpeedRPM= 250;
    @Override
        public void runOpMode()  {
            BioBuzzRobot robot = new BioBuzzRobot(hardwareMap, telemetry );
           robot.initialize();
            waitForStart();

            while(opModeIsActive()){
                if(gamepad1.circle){
                    robot.getPollenLauncher().setFlywheelSpeed(flywheelSpeedRPM);
                }else {
                    robot.getPollenLauncher().setFlywheelSpeed(0);
                }
                if(gamepad1.square){
                    robot.getNectarLauncher().setFlywheelSpeed(flywheelSpeedRPM);
                }else {
                    robot.getNectarLauncher().setFlywheelSpeed(0);
                }
            }

        }
    }