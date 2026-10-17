package org.firstinspires.ftc.teamcode.opmodes.telop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.BioBuzzRobot;

@TeleOp
public class FlywheelTeleop extends LinearOpMode {
    @Override
    public void runOpMode(){
        BioBuzzRobot robot = new BioBuzzRobot(hardwareMap,telemetry);
        robot.initialize();

    }
}
