package org.firstinspires.ftc.teamcode.automation.vision;

import com.broombots.sweep.Classes.LocalizationPacket;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.robot.tuning.AprilTagCameraConfiguration;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;

/**
 * Detects the Bio Buzz hive AprilTag clusters with a webcam on the turret and uses them to localize the robot on the field.
 * Call {@link #initProcessor()} once, {@link #updateProcess(double, double, boolean)} every loop, then read the result with {@link #getLocalizationPacket()}.
 * All distances are in inches and all angles are in degrees.
 */
public class HiveAprilTagsPortal {
    /** Which hive cell a tag cluster belongs to; positive or negative is the sign of the hive's x coordinate on the field. */
    public enum CELLS{
        RED_NEGATIVE,
        RED_POSITIVE,
        BLUE_NEGATIVE,
        BLUE_POSITIVE
    }

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private final Telemetry telemetry;
    private final AprilTagCameraConfiguration cameraConfiguration;
    // Camera to turret pivot with the turret at 0 degrees, in the optical-to-body corrected form HiveCellDetection expects.
    private final OpenGLMatrix cameraToTurretAtZeroTransformation;
    // Turret pivot to the robot center.
    private final OpenGLMatrix turretToRobotTransformation;
    // Latest averaged robot pose on the field; stays at the origin until the first detection.
    private Pose3D resultingLocalization = new Pose3D(new Position(DistanceUnit.INCH,0,0,0,0),new YawPitchRollAngles(AngleUnit.DEGREES, 0,0,0,0));


    /**
     * Builds the fixed camera-to-turret and turret-to-robot transformations from the tuned camera configuration.
     * @param telemetry used to post debug information when requested in {@link #updateProcess(double, double, boolean)}
     * @param cameraConfiguration camera name, lens intrinsics, resolution, and mounting transforms
     */
    public HiveAprilTagsPortal(Telemetry telemetry, AprilTagCameraConfiguration cameraConfiguration){
        this.telemetry = telemetry;
        this.cameraConfiguration = cameraConfiguration;

        turretToRobotTransformation = OpenGLMatrix.identityMatrix()
                .translated(
                        (float) cameraConfiguration.turretCenterToRobot.getPosition().x,
                        (float) cameraConfiguration.turretCenterToRobot.getPosition().y,
                        (float) cameraConfiguration.turretCenterToRobot.getPosition().z);

        // Maps the AprilTag optical frame (x right, y down, z forward) onto the camera body frame (x forward, y left, z up).
        OpenGLMatrix opticalToBody = OpenGLMatrix.identityMatrix();
        opticalToBody.put(0, 0, 0); opticalToBody.put(0, 1, 0); opticalToBody.put(0, 2, 1);
        opticalToBody.put(1, 0, -1); opticalToBody.put(1, 1, 0); opticalToBody.put(1, 2, 0);
        opticalToBody.put(2, 0, 0); opticalToBody.put(2, 1, -1); opticalToBody.put(2, 2, 0);

        cameraToTurretAtZeroTransformation = OpenGLMatrix.identityMatrix()
                .translated(
                        (float) cameraConfiguration.cameraToTurretZero.getPosition().x,
                        (float) cameraConfiguration.cameraToTurretZero.getPosition().y,
                        (float) cameraConfiguration.cameraToTurretZero.getPosition().z)
                .rotated(AngleUnit.DEGREES, (float) cameraConfiguration.cameraToTurretZero.getOrientation().getYaw(AngleUnit.DEGREES), 0, 0, 1)
                .rotated(AngleUnit.DEGREES, (float) cameraConfiguration.cameraToTurretZero.getOrientation().getPitch(AngleUnit.DEGREES), 0, 1, 0)
                .rotated(AngleUnit.DEGREES, (float) cameraConfiguration.cameraToTurretZero.getOrientation().getRoll(AngleUnit.DEGREES), 1, 0, 0)
                .multiplied(opticalToBody);
    }

    /**
     * Creates the AprilTag processor and vision portal, which starts the camera stream.
     * Must be called once before {@link #updateProcess(double, double, boolean)}.
     */
    public void initProcessor(){
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
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);
        visionPortal = builder.build();
    }

    /**
     * Same as {@link #updateProcess(double, double, boolean)} with telemetry turned off.
     * @param turretAngle turret angle, degrees, 0 is facing the robot x-axis, counterclockwise positive
     * @param currentTime current runtime, seconds
     */
    public void updateProcess(double turretAngle, double currentTime){
        updateProcess(turretAngle, currentTime,false);
    }

    /**
     * Primary method for apriltag calculations during runtime.
     * Will process where the hive currently is and where the robot is relative to the field.
     * Use {@link #getLocalizationPacket()} to get robot position.
     * If no hive cluster is visible, the previous localization is kept.
     * @param turretAngle turret angle, degrees, 0 is facing the robot x-axis, counterclockwise positive
     * @param currentTime current runtime, seconds
     * @param withTelemetry true to post the cluster count and raw camera-to-tag pose for each detection
     */
    public void updateProcess(double turretAngle, double currentTime, boolean withTelemetry){

        long acquisitionTime = (long)(currentTime*1e9); // convert double seconds into long nanoseconds to match SDK

        ArrayList<HiveCellDetection> hiveCellDetections = new ArrayList<>();
        ArrayList<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        for (AprilTagDetection detection : detections){
            if (detection instanceof AprilTagClusterDetection){
                hiveCellDetections.add(new HiveCellDetection((AprilTagClusterDetection) detection, cameraToTurretAtZeroTransformation, turretToRobotTransformation, acquisitionTime));
            }
        }
        if (hiveCellDetections.isEmpty()){
            return;
        }

        if (withTelemetry){
            telemetry.addLine("--- Hive Detections ---");
            telemetry.addData("Count", hiveCellDetections.size());
        }

        ArrayList<Pose3D> poses = new ArrayList<>();
        for (HiveCellDetection detection : hiveCellDetections){
            // Post debug telemetry log of the raw cluster position relative to the camera.
            if (withTelemetry) {
                telemetry.addLine("Tag: " + detection.clusterDetection.metadata.shortName);
                telemetry.addLine(String.format("xyz %.2f, %.2f, %.2f", detection.getRawPose().x, detection.getRawPose().y, detection.getRawPose().z));
                Orientation rawOrientation = Orientation.getOrientation(detection.getRawPose().R, AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
                telemetry.addLine(String.format("pyr (degrees): %.2f, %.2f, %.2f", rawOrientation.firstAngle, rawOrientation.secondAngle, rawOrientation.thirdAngle));
            }

            poses.add(detection.getFieldPose(turretAngle));
        }
        // sum every component of every pose, then divide by the count below to average them
        double i = 0;
        double x = 0;
        double y = 0;
        double z = 0;
        double pitch = 0;
        double yawSin = 0; // yaw is averaged as a unit vector so headings near +-180 do not cancel out
        double yawCos = 0;
        double roll = 0;
        for (Pose3D pose : poses){
            i++;
            x += pose.getPosition().x;
            y += pose.getPosition().y;
            z += pose.getPosition().z;
            pitch += pose.getOrientation().getPitch(AngleUnit.DEGREES);
            double poseYawRad = pose.getOrientation().getYaw(AngleUnit.RADIANS);
            yawSin += Math.sin(poseYawRad);
            yawCos += Math.cos(poseYawRad);
            roll += pose.getOrientation().getRoll(AngleUnit.DEGREES);
        }

        x /= i;
        y /= i;
        z /= i;
        pitch /= i;
        double yaw = Math.toDegrees(Math.atan2(yawSin, yawCos));
        roll /= i;

        resultingLocalization = new Pose3D(new Position(DistanceUnit.INCH, x, y, z, acquisitionTime), new YawPitchRollAngles(AngleUnit.DEGREES, yaw, pitch, roll, acquisitionTime));
    }

    /**
     * Get the robot's most recent field position.
     * @return packet with x and y (inches) and yaw (degrees); the remaining two values are unused and zero
     */
    public LocalizationPacket getLocalizationPacket(){
        return new LocalizationPacket(resultingLocalization.getPosition().x,resultingLocalization.getPosition().y,resultingLocalization.getOrientation().getYaw(AngleUnit.DEGREES),0,0);
    }
}
