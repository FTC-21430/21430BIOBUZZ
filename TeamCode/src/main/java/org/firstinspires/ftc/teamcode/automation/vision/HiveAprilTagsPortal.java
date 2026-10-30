package org.firstinspires.ftc.teamcode.automation.vision;

import com.broombots.sweep.Classes.LocalizationPacket;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.robot.tuning.AprilTagCameraConfiguration;
import org.firstinspires.ftc.teamcode.robot.tuning.CameraConfiguration;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseRaw;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;

public class HiveAprilTagsPortal {
    // as defined by the x coordinate of the field.
    public enum CELLS{
        RED_NEGATIVE,
        RED_POSITIVE,
        BLUE_NEGATIVE,
        BLUE_POSITIVE
    }

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private final Telemetry TELEMETRY;
    private AprilTagCameraConfiguration cameraConfiguration;
    private final OpenGLMatrix cameraToTurretAtZeroTransformation;
    private final OpenGLMatrix turretToRobotTransformation;

    public HiveAprilTagsPortal(Telemetry telemetry, AprilTagCameraConfiguration cameraConfiguration){
        this.TELEMETRY = telemetry;
        this.cameraConfiguration = cameraConfiguration;

        turretToRobotTransformation = OpenGLMatrix.identityMatrix()
                .translated(
                        (float) cameraConfiguration.turretCenterToRobot.getPosition().x,
                        (float) cameraConfiguration.turretCenterToRobot.getPosition().y,
                        (float) cameraConfiguration.turretCenterToRobot.getPosition().z);
        cameraToTurretAtZeroTransformation = OpenGLMatrix.identityMatrix()
                .translated(
                        (float) cameraConfiguration.cameraToTurretZero.getPosition().x,
                        (float) cameraConfiguration.cameraToTurretZero.getPosition().y,
                        (float) cameraConfiguration.cameraToTurretZero.getPosition().z);
    }
    public void InitProcessor(){
        if (cameraConfiguration == null) {
            throw new RuntimeException("Camera configuration is not set");
        }
        double fx = cameraConfiguration.getLensIntrinsics().get(0);
        double fy = cameraConfiguration.getLensIntrinsics().get(1);
        double cx = cameraConfiguration.getLensIntrinsics().get(2);
        double cy = cameraConfiguration.getLensIntrinsics().get(3);

        // NOTE we are not using any of the FTC SDK localization and translation code. We are doing it from scratch!
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setTagLibrary(AprilTagGameDatabase.getBioBuzzTagLibrary())
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .setLensIntrinsics(fx, fy, cx, cy)
                .build();

        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(cameraConfiguration.getNAME());
        builder.setCameraResolution(cameraConfiguration.getCameraResolution());
        builder.addProcessor(aprilTagProcessor);
        visionPortal = builder.build();
    }
    public void updateProcess(){
        ArrayList<HiveCellDetection> hiveCellDetections = new ArrayList<>();
        ArrayList<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        for (AprilTagDetection detection : detections){
            if (detection instanceof AprilTagClusterDetection){
                hiveCellDetections.add(new HiveCellDetection((AprilTagClusterDetection) detection, cameraToTurretAtZeroTransformation, turretToRobotTransformation));
            }
        }
        TELEMETRY.addLine("--- Hive Detections ---");
        TELEMETRY.addData("Count", hiveCellDetections.size());
        for (HiveCellDetection detection : hiveCellDetections){
            AprilTagPoseRaw rawPose = detection.getRawPose();
            TELEMETRY.addLine("&& new detection &&");
            TELEMETRY.addLine(String.format("XYZ X: %.2f, Y: %.2f, Z: %.2f", rawPose.x, rawPose.y, rawPose.z));
            TELEMETRY.addData("Tipping", detection.isTipping());
        }
    }

    public LocalizationPacket getLocalizationPacket(){
        return null;
    }
}


