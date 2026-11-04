package org.firstinspires.ftc.teamcode.opmodes.utility;

import com.broombots.sweep.Classes.LocalizationPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Utility;

import org.firstinspires.ftc.teamcode.robot.BioBuzzRobot;

@Utility
public class AprilTagLocalizationTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException{
        BioBuzzRobot robot = new BioBuzzRobot(hardwareMap,telemetry);
        robot.initialize();
        waitForStart();
        while(opModeIsActive()){
            robot.teleopUpdate();
            LocalizationPacket result = robot.getHiveAprilTagsPortal().getLocalizationPacket();
            telemetry.addLine("--Hive Cell Localization Packet--");
            telemetry.addData("x", result.getX());
            telemetry.addData("y", result.getY());
            telemetry.addData("yaw", result.getYaw());
            telemetry.update();
        }
    }
}
