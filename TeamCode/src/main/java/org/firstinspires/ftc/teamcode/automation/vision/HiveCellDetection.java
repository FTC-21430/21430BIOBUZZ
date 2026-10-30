package org.firstinspires.ftc.teamcode.automation.vision;

import org.firstinspires.ftc.robotcore.external.matrices.MatrixF;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.teamcode.automation.vision.HiveAprilTagsPortal.CELLS;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseRaw;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

public class HiveCellDetection {
    AprilTagClusterDetection clusterDetection;
    private final double CELL_PIVOT_TO_OPEN_X = 21.189; //Inches
    private final double CELL_PIVOT_TO_OPEN_Z = 4.253; //Inches
    private final double CELL_PIVOT_TO_OPEN_HYPOT = Math.hypot(CELL_PIVOT_TO_OPEN_X, CELL_PIVOT_TO_OPEN_Z);
    private final double CELL_ANGLE_TO_OPEN = Math.atan(CELL_PIVOT_TO_OPEN_Z / CELL_PIVOT_TO_OPEN_X); // approx 11.349 degrees
    private final double FRAME_PIVOT_HEIGHT_FROM_GROUND = 43.950; //Inches
    private final double HIVE_Y_OFFSET = 12.750; //Inches
    private final CELLS CELL_TYPE;
    private final int X_SIGN;
    private final int Y_SIGN;
    private final OpenGLMatrix cameraToTurretZeroTransformation;
    private final OpenGLMatrix turretToRobotTransformation;

    public HiveCellDetection(AprilTagClusterDetection clusterDetection, OpenGLMatrix cameraToTurretAtZeroTransformation, OpenGLMatrix turretToRobotTransformation){
        this.clusterDetection = clusterDetection;
        this.cameraToTurretZeroTransformation = cameraToTurretAtZeroTransformation;
        this.turretToRobotTransformation = turretToRobotTransformation;
        switch (clusterDetection.metadata.shortName){
            case ("RED SCORING"):
                CELL_TYPE = CELLS.RED_NEGATIVE;
                X_SIGN = -1;
                Y_SIGN = -1;
                break;
            case ("BLUE SCORING"):
                CELL_TYPE = CELLS.BLUE_NEGATIVE;
                X_SIGN = -1;
                Y_SIGN = 1;
                break;
            case ("BLUE AUDIENCE"):
                CELL_TYPE = CELLS.BLUE_POSITIVE;
                X_SIGN = 1;
                Y_SIGN = -1;
                break;
            default:
                CELL_TYPE = CELLS.RED_POSITIVE;
                X_SIGN = 1;
                Y_SIGN = -1;
        }
    }
    public AprilTagPoseRaw getRawPose(){
        return clusterDetection.rawPose;
    }
    public Pose3D getFieldPose(double turretAngle){
        Orientation tagOrientationToCamera = Orientation.getOrientation(clusterDetection.rawPose.R, AxesReference.INTRINSIC, AxesOrder.YXZ, AngleUnit.DEGREES);
        OpenGLMatrix cameraToTag = OpenGLMatrix.identityMatrix()
                .translated(
                        (float)clusterDetection.rawPose.x,
                        (float)clusterDetection.rawPose.y,
                        (float)clusterDetection.rawPose.z)
                .multiplied(tagOrientationToCamera.getRotationMatrix());
        OpenGLMatrix tagToRobot = cameraToTag
                .multiplied(cameraToTurretZeroTransformation.rotated(AngleUnit.DEGREES,(float)turretAngle,0.0F,0.0F,1F))
                .multiplied(turretToRobotTransformation);

        Orientation HiveToRobot = Orientation.getOrientation(
                tagToRobot.inverted(),
                AxesReference.EXTRINSIC,
                AxesOrder.XYZ,
                AngleUnit.DEGREES
        );
        double HivePitch = HiveToRobot.secondAngle;

        double hiveX = CELL_PIVOT_TO_OPEN_HYPOT * Math.cos(HivePitch + CELL_ANGLE_TO_OPEN) * X_SIGN;
        double hiveY = HIVE_Y_OFFSET * Y_SIGN;
        double hiveZ = CELL_PIVOT_TO_OPEN_HYPOT * Math.sin(HivePitch + CELL_ANGLE_TO_OPEN) + FRAME_PIVOT_HEIGHT_FROM_GROUND;

        Orientation CompositeHiveOrientation = new Orientation(AxesReference.INTRINSIC, AxesOrder.XYZ,AngleUnit.DEGREES,0,(float)HivePitch,0,0);

        OpenGLMatrix worldToHive = OpenGLMatrix.identityMatrix()
                .translated((float)hiveX, (float)hiveY, (float)hiveZ)
                .multiplied(CompositeHiveOrientation.getRotationMatrix());

        OpenGLMatrix worldToRobot = worldToHive
                .multiplied(tagToRobot);
        VectorF translation = worldToRobot.getTranslation();
        Orientation orientation = Orientation.getOrientation(worldToRobot, AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

        Pose3D result = new Pose3D(
                new Position(
                        DistanceUnit.INCH,
                        translation.get(0),
                        translation.get(1),
                        translation.get(2),
                        0),
                new YawPitchRollAngles(AngleUnit.DEGREES,
                        orientation.firstAngle,
                        orientation.secondAngle,
                        orientation.thirdAngle,
                        0)
        );
        return result;
    }
    public boolean isTipping(){
        // TODO
        return false;
    }
}

