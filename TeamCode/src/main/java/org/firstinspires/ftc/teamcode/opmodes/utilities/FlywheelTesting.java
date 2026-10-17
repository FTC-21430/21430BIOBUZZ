package org.firstinspires.ftc.teamcode.opmodes.utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.opmodes.telop.FlywheelTeleop;
import org.firstinspires.ftc.teamcode.robot.BioBuzzRobot;
import org.firstinspires.ftc.teamcode.robot.subsystems.Flywheel;
@TeleOp
public class FlywheelTesting extends LinearOpMode {
        @Override
        public void runOpMode()  {
            BioBuzzRobot robot = new BioBuzzRobot(hardwareMap, telemetry );
           robot.initialize();
            waitForStart();
            while(opModeIsActive()){
                if(gamepad1.b){
                    robot.getFlywheel().setTargetSpeed(1);
                }else {
                    robot.getFlywheel().setTargetSpeed(0);
                }
            }

        }
    }