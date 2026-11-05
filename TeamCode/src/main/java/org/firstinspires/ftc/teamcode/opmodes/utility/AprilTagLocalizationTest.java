package org.firstinspires.ftc.teamcode.opmodes.utility;

import com.broombots.sweep.Classes.LocalizationPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Utility;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.robot.BioBuzzRobot;

import java.util.ArrayList;

@Utility
public class AprilTagLocalizationTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException{
        BioBuzzRobot robot = new BioBuzzRobot(hardwareMap,telemetry);
        robot.initialize();
        waitForStart();
        while(opModeIsActive()){
            robot.teleopUpdate();
            ArrayList<Pose3D> hivePositions = robot.getHiveAprilTagsPortal().getHivePositions();
            for (Pose3D pos : hivePositions){
                telemetry.addLine("---Hive Cell Position");
                telemetry.addLine(String.format("Cell xyz: %.2f, %.2f, %.2f",pos.getPosition().x,pos.getPosition().y,pos.getPosition().z));
                telemetry.addLine(String.format("Cell pry: %.2f, %.2f, %.2f",pos.getOrientation().getPitch(AngleUnit.DEGREES),pos.getOrientation().getYaw(AngleUnit.DEGREES),pos.getOrientation().getRoll(AngleUnit.DEGREES)));
            }
            LocalizationPacket result = robot.getHiveAprilTagsPortal().getLocalizationPacket();
            telemetry.addLine("--Hive Cell Localization Packet--");
            telemetry.addData("x", result.getX());
            telemetry.addData("y", result.getY());
            telemetry.addData("yaw", result.getYaw());

            telemetry.update();
        }
    }
}
