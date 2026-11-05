package org.firstinspires.ftc.teamcode.robot.tuning;

import android.util.Size;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.ArrayList;
import java.util.Arrays;

@Config
public class AprilTagCameraConfiguration extends CameraConfiguration{
    public static String webcameHardwareMapString = "AprilTag";
    public static int cameraResolutionX = 800;
    public static int cameraResolutionY = 600;

    public Pose3D turretCenterToRobot = new Pose3D(new Position(DistanceUnit.INCH, robotToTurretPosX, robotToTurretPosY, robotToTurretPosZ, 0), new YawPitchRollAngles(AngleUnit.DEGREES, 0, 0, 0, 0));

    // Lens intrinsics in pixels, placeholders until calibrated
    public static double lensFx = 686.56755093;
    public static double lensFy = 686.56755093;
    public static double lensCx = 405.302635756;
    public static double lensCy = 306.702380071;

    // NOTE THAT APRILTAG REFERENCE FRAME HAS X+ = angle 0, Make sure that X+ is toward intakes and Y+ is facing the hubs.

    //TODO - currently configured for the software testing bot, remember to update values during integration with target hardware. See https://github.com/FTC-21430/21430BIOBUZZ/issues/20
    public static double turretToCameraPosX = -2.0;
    public static double turretToCameraPosY = 0.0;
    public static double turretToCameraPosZ = 3.0;
    public static double robotToTurretPosX = 0.0;
    public static double robotToTurretPosY = -2.0;
    public static double robotToTurretPosZ = 10.5;
    public static double turretToCameraOrientationPitch = 55.0;
    public static double turretToCameraOrientationYaw = 0.0; // yaw will be updated from the subsystem during runtime
    public static double turretToCameraOrientationRoll = 0.0;
    public Pose3D cameraToTurretZero = new Pose3D(new Position(DistanceUnit.INCH, turretToCameraPosX, turretToCameraPosY, turretToCameraPosZ, 0), new YawPitchRollAngles(AngleUnit.DEGREES, turretToCameraOrientationYaw, turretToCameraOrientationPitch, turretToCameraOrientationRoll, 0));
    // Hear ye hear ye, I declare that angular zero of the turrets shall henceforth be aligned toward the forward axis of the robot.


    public AprilTagCameraConfiguration(HardwareMap hardwareMap){
        super(
                hardwareMap.get(WebcamName.class, webcameHardwareMapString),
                new ArrayList<>(Arrays.asList(lensFx, lensFy, lensCx, lensCy)),
                new Size(cameraResolutionX,cameraResolutionY),
                new Position(DistanceUnit.INCH,turretToCameraPosX,turretToCameraPosY,turretToCameraPosZ,0),
                new YawPitchRollAngles(AngleUnit.DEGREES, turretToCameraOrientationYaw, turretToCameraOrientationPitch, turretToCameraOrientationRoll,0));
    }
}
