package org.firstinspires.ftc.teamcode.automation.vision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.GeneralMatrixF;
import org.firstinspires.ftc.robotcore.external.matrices.MatrixF;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.robot.tuning.AprilTagCameraConfiguration;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseRaw;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34, application = android.app.Application.class)
public class HiveAprilTagsPortalTest {
    private static final double POS_TOL_IN = 0.05;
    private static final double ANGLE_TOL_DEG = 0.1;

    // Hive geometry from CAD, shared by all four hives. Pitch is limited to +-30 degrees about the pivot.
    private static final double CELL_X = 21.189, CELL_Z = 4.253, PIVOT_H = 43.950, HIVE_Y = 12.750;

    private static final double[] TURRET_ANGLES = {-90, -45, 0, 30, 90};
    private static final double[][] ROBOT_POSES = { // x, y, heading deg; always planar
            {0, 0, 0}, {24, -12, 45}, {-30, 40, 170}, {50, 10, -90}, {-10, -60, -135}};

    private AprilTagCameraConfiguration createConfig() {
        AprilTagCameraConfiguration c = mock(AprilTagCameraConfiguration.class);
        c.cameraToTurretZero = new Pose3D(
                new Position(DistanceUnit.INCH, AprilTagCameraConfiguration.turretToCameraPosX,
                        AprilTagCameraConfiguration.turretToCameraPosY,
                        AprilTagCameraConfiguration.turretToCameraPosZ, 0),
                new YawPitchRollAngles(AngleUnit.DEGREES, AprilTagCameraConfiguration.turretToCameraOrientationYaw,
                        AprilTagCameraConfiguration.turretToCameraOrientationPitch,
                        AprilTagCameraConfiguration.turretToCameraOrientationRoll, 0));
        c.turretCenterToRobot = new Pose3D(
                new Position(DistanceUnit.INCH, AprilTagCameraConfiguration.robotToTurretPosX,
                        AprilTagCameraConfiguration.robotToTurretPosY,
                        AprilTagCameraConfiguration.robotToTurretPosZ, 0),
                new YawPitchRollAngles(AngleUnit.DEGREES, 0, 0, 0, 0));
        return c;
    }

    private static OpenGLMatrix translation(double x, double y, double z) {
        return OpenGLMatrix.translation((float) x, (float) y, (float) z);
    }

    private static OpenGLMatrix rotation(double deg, float x, float y, float z) {
        return OpenGLMatrix.rotation(AngleUnit.DEGREES, (float) deg, x, y, z);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static OpenGLMatrix privateMatrix(Object target, String name) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return (OpenGLMatrix) f.get(target);
    }

    private static AprilTagClusterDetection createDetection(String cellName, OpenGLMatrix cameraToTag) throws Exception {
        MatrixF r = new GeneralMatrixF(3, 3);
        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) r.put(i, j, cameraToTag.get(i, j));
        AprilTagPoseRaw raw = new AprilTagPoseRaw(
                cameraToTag.get(0, 3), cameraToTag.get(1, 3), cameraToTag.get(2, 3), r);
        AprilTagClusterMetadata metadata = mock(AprilTagClusterMetadata.class);
        setField(metadata, "shortName", cellName);
        AprilTagClusterDetection d = mock(AprilTagClusterDetection.class);
        setField(d, "rawPose", raw);
        setField(d, "metadata", metadata);
        return d;
    }

    /**
     * Cell opening pose in the field frame. Hives at +X are mirror images of hives at -X, so
     * positive pitch always raises the opening and the rotation about Y flips with the side.
     */
    private static OpenGLMatrix fieldToTag(int xSign, int ySign, double hivePitchDeg) {
        double p = Math.toRadians(hivePitchDeg);
        double open = Math.atan2(CELL_Z, CELL_X);
        double h = Math.hypot(CELL_X, CELL_Z);
        return translation(h * Math.cos(p + open) * xSign, HIVE_Y * ySign,
                h * Math.sin(p + open) + PIVOT_H).multiplied(rotation(-xSign * hivePitchDeg, 0, 1, 0));
    }

    /** Real AprilTag frame: x image-right, y down, z into the tag, whose normal points away from the pivot. */
    private static OpenGLMatrix tagAxesFromFieldAxes(int xSign) {
        OpenGLMatrix m = OpenGLMatrix.identityMatrix();
        m.put(0, 0, 0); m.put(0, 1, 0); m.put(0, 2, -xSign);
        m.put(1, 0, xSign); m.put(1, 1, 0); m.put(1, 2, 0);
        m.put(2, 0, 0); m.put(2, 1, -1); m.put(2, 2, 0);
        return m;
    }

    private static OpenGLMatrix fieldToCamera(HiveAprilTagsPortal portal, double[] pose, double turretAngle) throws Exception {
        OpenGLMatrix camToTurretZero = privateMatrix(portal, "cameraToTurretAtZeroTransformation");
        OpenGLMatrix turretToRobot = privateMatrix(portal, "turretToRobotTransformation");
        return translation(pose[0], pose[1], 0)
                .multiplied(rotation(pose[2], 0, 0, 1))
                .multiplied(turretToRobot)
                .multiplied(rotation(turretAngle, 0, 0, 1))
                .multiplied(camToTurretZero);
    }

    private static Pose3D localize(HiveAprilTagsPortal portal, String cellName, int xSign, int ySign,
                                   double hivePitch, double[] pose, double turretAngle) throws Exception {
        OpenGLMatrix cameraToTag = fieldToCamera(portal, pose, turretAngle).inverted()
                .multiplied(fieldToTag(xSign, ySign, hivePitch))
                .multiplied(tagAxesFromFieldAxes(xSign));
        HiveCellDetection detection = new HiveCellDetection(
                createDetection(cellName, cameraToTag),
                privateMatrix(portal, "cameraToTurretAtZeroTransformation"),
                privateMatrix(portal, "turretToRobotTransformation"),0);
        return detection.getFieldPose(turretAngle);
    }

    private static void assertPose(String label, double[] pose, Pose3D result) {
        assertEquals(label + " x", pose[0], result.getPosition().x, POS_TOL_IN);
        assertEquals(label + " y", pose[1], result.getPosition().y, POS_TOL_IN);
        assertEquals(label + " z", 0.0, result.getPosition().z, POS_TOL_IN);
        assertEquals(label + " pitch", 0.0, result.getOrientation().getPitch(AngleUnit.DEGREES), ANGLE_TOL_DEG);
        assertEquals(label + " roll", 0.0, result.getOrientation().getRoll(AngleUnit.DEGREES), ANGLE_TOL_DEG);
        assertEquals(label + " yaw", 0.0,
                AngleUnit.normalizeDegrees(result.getOrientation().getYaw(AngleUnit.DEGREES) - pose[2]), ANGLE_TOL_DEG);
    }

    private void sweepRobotAndTurret(String cellName, int xSign, int ySign, double hivePitch) throws Exception {
        HiveAprilTagsPortal portal = new HiveAprilTagsPortal(mock(Telemetry.class), createConfig());
        for (double[] pose : ROBOT_POSES) {
            for (double turretAngle : TURRET_ANGLES) {
                String label = String.format("%s robot=(%.0f, %.0f, %.0f) turret=%.0f hivePitch=%.0f",
                        cellName, pose[0], pose[1], pose[2], turretAngle, hivePitch);
                assertPose(label, pose, localize(portal, cellName, xSign, ySign, hivePitch, pose, turretAngle));
            }
        }
    }

    @Test
    public void ensureConfigurationBuildsTurretAndCameraTransforms() throws Exception {
        HiveAprilTagsPortal portal = new HiveAprilTagsPortal(mock(Telemetry.class), createConfig());
        OpenGLMatrix cam = privateMatrix(portal, "cameraToTurretAtZeroTransformation");
        OpenGLMatrix turret = privateMatrix(portal, "turretToRobotTransformation");
        assertEquals(AprilTagCameraConfiguration.turretToCameraPosX, cam.get(0, 3), 1e-4);
        assertEquals(AprilTagCameraConfiguration.turretToCameraPosY, cam.get(1, 3), 1e-4);
        assertEquals(AprilTagCameraConfiguration.turretToCameraPosZ, cam.get(2, 3), 1e-4);
        assertEquals(AprilTagCameraConfiguration.robotToTurretPosX, turret.get(0, 3), 1e-4);
        assertEquals(AprilTagCameraConfiguration.robotToTurretPosY, turret.get(1, 3), 1e-4);
        assertEquals(AprilTagCameraConfiguration.robotToTurretPosZ, turret.get(2, 3), 1e-4);
    }

    @Test
    public void ensureCameraOpticalAxisPointsForwardAndDownByMountPitch() throws Exception {
        HiveAprilTagsPortal portal = new HiveAprilTagsPortal(mock(Telemetry.class), createConfig());
        OpenGLMatrix cam = privateMatrix(portal, "cameraToTurretAtZeroTransformation");
        double pitch = Math.toRadians(AprilTagCameraConfiguration.turretToCameraOrientationPitch);

        // Optical z (viewing direction) in the turret frame: forward and tilted down.
        assertEquals(Math.cos(pitch), cam.get(0, 2), 1e-4);
        assertEquals(0.0, cam.get(1, 2), 1e-4);
        assertEquals(-Math.sin(pitch), cam.get(2, 2), 1e-4);
        // Optical x (image right) points to the robot's right, parallel to the floor.
        assertEquals(0.0, cam.get(0, 0), 1e-4);
        assertEquals(-1.0, cam.get(1, 0), 1e-4);
        assertEquals(0.0, cam.get(2, 0), 1e-4);
    }

    @Test
    public void red_scoring_localizesAtHivePitch_m30() throws Exception {
        sweepRobotAndTurret("RED SCORING", -1, -1, -30);
    }

    @Test
    public void red_scoring_localizesAtHivePitch_m20() throws Exception {
        sweepRobotAndTurret("RED SCORING", -1, -1, -20);
    }

    @Test
    public void red_scoring_localizesAtHivePitch_m10() throws Exception {
        sweepRobotAndTurret("RED SCORING", -1, -1, -10);
    }

    @Test
    public void red_scoring_localizesAtHivePitch_p0() throws Exception {
        sweepRobotAndTurret("RED SCORING", -1, -1, 0);
    }

    @Test
    public void red_scoring_localizesAtHivePitch_p10() throws Exception {
        sweepRobotAndTurret("RED SCORING", -1, -1, 10);
    }

    @Test
    public void red_scoring_localizesAtHivePitch_p20() throws Exception {
        sweepRobotAndTurret("RED SCORING", -1, -1, 20);
    }

    @Test
    public void red_scoring_localizesAtHivePitch_p30() throws Exception {
        sweepRobotAndTurret("RED SCORING", -1, -1, 30);
    }

    @Test
    public void blue_scoring_localizesAtHivePitch_m30() throws Exception {
        sweepRobotAndTurret("BLUE SCORING", -1, 1, -30);
    }

    @Test
    public void blue_scoring_localizesAtHivePitch_m20() throws Exception {
        sweepRobotAndTurret("BLUE SCORING", -1, 1, -20);
    }

    @Test
    public void blue_scoring_localizesAtHivePitch_m10() throws Exception {
        sweepRobotAndTurret("BLUE SCORING", -1, 1, -10);
    }

    @Test
    public void blue_scoring_localizesAtHivePitch_p0() throws Exception {
        sweepRobotAndTurret("BLUE SCORING", -1, 1, 0);
    }

    @Test
    public void blue_scoring_localizesAtHivePitch_p10() throws Exception {
        sweepRobotAndTurret("BLUE SCORING", -1, 1, 10);
    }

    @Test
    public void blue_scoring_localizesAtHivePitch_p20() throws Exception {
        sweepRobotAndTurret("BLUE SCORING", -1, 1, 20);
    }

    @Test
    public void blue_scoring_localizesAtHivePitch_p30() throws Exception {
        sweepRobotAndTurret("BLUE SCORING", -1, 1, 30);
    }

    @Test
    public void red_audience_localizesAtHivePitch_m30() throws Exception {
        sweepRobotAndTurret("RED AUDIENCE", 1, -1, -30);
    }

    @Test
    public void red_audience_localizesAtHivePitch_m20() throws Exception {
        sweepRobotAndTurret("RED AUDIENCE", 1, -1, -20);
    }

    @Test
    public void red_audience_localizesAtHivePitch_m10() throws Exception {
        sweepRobotAndTurret("RED AUDIENCE", 1, -1, -10);
    }

    @Test
    public void red_audience_localizesAtHivePitch_p0() throws Exception {
        sweepRobotAndTurret("RED AUDIENCE", 1, -1, 0);
    }

    @Test
    public void red_audience_localizesAtHivePitch_p10() throws Exception {
        sweepRobotAndTurret("RED AUDIENCE", 1, -1, 10);
    }

    @Test
    public void red_audience_localizesAtHivePitch_p20() throws Exception {
        sweepRobotAndTurret("RED AUDIENCE", 1, -1, 20);
    }

    @Test
    public void red_audience_localizesAtHivePitch_p30() throws Exception {
        sweepRobotAndTurret("RED AUDIENCE", 1, -1, 30);
    }

    @Test
    public void blue_audience_localizesAtHivePitch_m30() throws Exception {
        sweepRobotAndTurret("BLUE AUDIENCE", 1, 1, -30);
    }

    @Test
    public void blue_audience_localizesAtHivePitch_m20() throws Exception {
        sweepRobotAndTurret("BLUE AUDIENCE", 1, 1, -20);
    }

    @Test
    public void blue_audience_localizesAtHivePitch_m10() throws Exception {
        sweepRobotAndTurret("BLUE AUDIENCE", 1, 1, -10);
    }

    @Test
    public void blue_audience_localizesAtHivePitch_p0() throws Exception {
        sweepRobotAndTurret("BLUE AUDIENCE", 1, 1, 0);
    }

    @Test
    public void blue_audience_localizesAtHivePitch_p10() throws Exception {
        sweepRobotAndTurret("BLUE AUDIENCE", 1, 1, 10);
    }

    @Test
    public void blue_audience_localizesAtHivePitch_p20() throws Exception {
        sweepRobotAndTurret("BLUE AUDIENCE", 1, 1, 20);
    }

    @Test
    public void blue_audience_localizesAtHivePitch_p30() throws Exception {
        sweepRobotAndTurret("BLUE AUDIENCE", 1, 1, 30);
    }

    @Test
    public void red_scoring_localizesWithFineTurretSweepAtPitchLimits() throws Exception {
        HiveAprilTagsPortal portal = new HiveAprilTagsPortal(mock(Telemetry.class), createConfig());
        double[] pose = {12, -18, 30};
        for (double hivePitch : new double[]{-30, 30}) {
            for (double turretAngle = -180; turretAngle <= 180; turretAngle += 15) {
                String label = String.format("RED SCORING turret=%.0f hivePitch=%.0f", turretAngle, hivePitch);
                assertPose(label, pose, localize(portal, "RED SCORING", -1, -1, hivePitch, pose, turretAngle));
            }
        }
    }

    @Test
    public void red_scoring_localizationIsIndependentOfRobotHeadingAtPitchLimits() throws Exception {
        HiveAprilTagsPortal portal = new HiveAprilTagsPortal(mock(Telemetry.class), createConfig());
        for (double hivePitch : new double[]{-30, 30}) {
            for (double heading = -180; heading <= 180; heading += 30) {
                double[] pose = {5, 5, heading};
                String label = String.format("RED SCORING heading=%.0f hivePitch=%.0f", heading, hivePitch);
                assertPose(label, pose, localize(portal, "RED SCORING", -1, -1, hivePitch, pose, 20));
            }
        }
    }

    @Test
    public void red_scoring_openingRisesWithPositivePitch() throws Exception {
        double previousZ = -Double.MAX_VALUE;
        for (double hivePitch = -30; hivePitch <= 30; hivePitch += 10) {
            double z = fieldToTag(-1, -1, hivePitch).get(2, 3);
            assertTrue("RED SCORING opening should rise at pitch " + hivePitch, z > previousZ);
            previousZ = z;
        }
    }

    @Test
    public void blue_scoring_localizesWithFineTurretSweepAtPitchLimits() throws Exception {
        HiveAprilTagsPortal portal = new HiveAprilTagsPortal(mock(Telemetry.class), createConfig());
        double[] pose = {12, -18, 30};
        for (double hivePitch : new double[]{-30, 30}) {
            for (double turretAngle = -180; turretAngle <= 180; turretAngle += 15) {
                String label = String.format("BLUE SCORING turret=%.0f hivePitch=%.0f", turretAngle, hivePitch);
                assertPose(label, pose, localize(portal, "BLUE SCORING", -1, 1, hivePitch, pose, turretAngle));
            }
        }
    }

    @Test
    public void blue_scoring_localizationIsIndependentOfRobotHeadingAtPitchLimits() throws Exception {
        HiveAprilTagsPortal portal = new HiveAprilTagsPortal(mock(Telemetry.class), createConfig());
        for (double hivePitch : new double[]{-30, 30}) {
            for (double heading = -180; heading <= 180; heading += 30) {
                double[] pose = {5, 5, heading};
                String label = String.format("BLUE SCORING heading=%.0f hivePitch=%.0f", heading, hivePitch);
                assertPose(label, pose, localize(portal, "BLUE SCORING", -1, 1, hivePitch, pose, 20));
            }
        }
    }

    @Test
    public void blue_scoring_openingRisesWithPositivePitch() throws Exception {
        double previousZ = -Double.MAX_VALUE;
        for (double hivePitch = -30; hivePitch <= 30; hivePitch += 10) {
            double z = fieldToTag(-1, 1, hivePitch).get(2, 3);
            assertTrue("BLUE SCORING opening should rise at pitch " + hivePitch, z > previousZ);
            previousZ = z;
        }
    }

    @Test
    public void red_audience_localizesWithFineTurretSweepAtPitchLimits() throws Exception {
        HiveAprilTagsPortal portal = new HiveAprilTagsPortal(mock(Telemetry.class), createConfig());
        double[] pose = {12, -18, 30};
        for (double hivePitch : new double[]{-30, 30}) {
            for (double turretAngle = -180; turretAngle <= 180; turretAngle += 15) {
                String label = String.format("RED AUDIENCE turret=%.0f hivePitch=%.0f", turretAngle, hivePitch);
                assertPose(label, pose, localize(portal, "RED AUDIENCE", 1, -1, hivePitch, pose, turretAngle));
            }
        }
    }

    @Test
    public void red_audience_localizationIsIndependentOfRobotHeadingAtPitchLimits() throws Exception {
        HiveAprilTagsPortal portal = new HiveAprilTagsPortal(mock(Telemetry.class), createConfig());
        for (double hivePitch : new double[]{-30, 30}) {
            for (double heading = -180; heading <= 180; heading += 30) {
                double[] pose = {5, 5, heading};
                String label = String.format("RED AUDIENCE heading=%.0f hivePitch=%.0f", heading, hivePitch);
                assertPose(label, pose, localize(portal, "RED AUDIENCE", 1, -1, hivePitch, pose, 20));
            }
        }
    }

    @Test
    public void red_audience_openingRisesWithPositivePitch() throws Exception {
        double previousZ = -Double.MAX_VALUE;
        for (double hivePitch = -30; hivePitch <= 30; hivePitch += 10) {
            double z = fieldToTag(1, -1, hivePitch).get(2, 3);
            assertTrue("RED AUDIENCE opening should rise at pitch " + hivePitch, z > previousZ);
            previousZ = z;
        }
    }

    @Test
    public void blue_audience_localizesWithFineTurretSweepAtPitchLimits() throws Exception {
        HiveAprilTagsPortal portal = new HiveAprilTagsPortal(mock(Telemetry.class), createConfig());
        double[] pose = {12, -18, 30};
        for (double hivePitch : new double[]{-30, 30}) {
            for (double turretAngle = -180; turretAngle <= 180; turretAngle += 15) {
                String label = String.format("BLUE AUDIENCE turret=%.0f hivePitch=%.0f", turretAngle, hivePitch);
                assertPose(label, pose, localize(portal, "BLUE AUDIENCE", 1, 1, hivePitch, pose, turretAngle));
            }
        }
    }

    @Test
    public void blue_audience_localizationIsIndependentOfRobotHeadingAtPitchLimits() throws Exception {
        HiveAprilTagsPortal portal = new HiveAprilTagsPortal(mock(Telemetry.class), createConfig());
        for (double hivePitch : new double[]{-30, 30}) {
            for (double heading = -180; heading <= 180; heading += 30) {
                double[] pose = {5, 5, heading};
                String label = String.format("BLUE AUDIENCE heading=%.0f hivePitch=%.0f", heading, hivePitch);
                assertPose(label, pose, localize(portal, "BLUE AUDIENCE", 1, 1, hivePitch, pose, 20));
            }
        }
    }

    @Test
    public void blue_audience_openingRisesWithPositivePitch() throws Exception {
        double previousZ = -Double.MAX_VALUE;
        for (double hivePitch = -30; hivePitch <= 30; hivePitch += 10) {
            double z = fieldToTag(1, 1, hivePitch).get(2, 3);
            assertTrue("BLUE AUDIENCE opening should rise at pitch " + hivePitch, z > previousZ);
            previousZ = z;
        }
    }
}
