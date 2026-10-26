package org.firstinspires.ftc.teamcode.robot.tuning;

import android.util.Size;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.ArrayList;
import java.util.Arrays;

@Config
public class AprilTagCameraConfiguration extends CameraConfiguration{
    public static String webcameHardwareMapString = "webcam1";
    public static int cameraResolutionX = 800;
    public static int cameraResolutionY = 800;

    // Lens intrinsics in pixels, placeholders until calibrated
    public static double lensFx = 1.0;
    public static double lensFy = 1.0;
    public static double lensCx = 1.0;
    public static double lensCy = 1.0;

    //TODO - currently configured for the software testing bot, remember to update values during integration with target hardware
    public static double cameraPosX = 0.0;
    public static double cameraPosY = 0.0;
    public static double cameraPosZ = 0.0;
    public static double cameraOrientationPitch = 50.0;
    public static double cameraOrientationYaw = 0.0; // yaw will be updated from the subsystem during runtime
    public static double cameraOrientationRoll = 0.0;

    public AprilTagCameraConfiguration(HardwareMap hardwareMap){
        super(hardwareMap.get(WebcamName.class, webcameHardwareMapString), new ArrayList<>(Arrays.asList(lensFx, lensFy, lensCx, lensCy)), new Size(cameraResolutionX,cameraResolutionY), new Position(DistanceUnit.INCH,cameraPosX,cameraPosY,cameraPosZ,0), new YawPitchRollAngles(AngleUnit.DEGREES, cameraOrientationYaw, cameraOrientationPitch, cameraOrientationRoll,0));
    }
}
