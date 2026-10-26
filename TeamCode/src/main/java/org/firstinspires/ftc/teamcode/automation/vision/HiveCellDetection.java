package org.firstinspires.ftc.teamcode.automation.vision;

import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.teamcode.automation.vision.HiveAprilTagsPortal.CELLS;

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

    public HiveCellDetection(AprilTagClusterDetection clusterDetection){
        this.clusterDetection = clusterDetection;
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
    public AprilTagPoseFtc getRawPos(){
        return clusterDetection.ftcPose;
    }
    public AprilTagPoseFtc getFieldPose(){
        double x = CELL_PIVOT_TO_OPEN_HYPOT * Math.cos(clusterDetection.ftcPose.pitch + CELL_ANGLE_TO_OPEN * -X_SIGN) * X_SIGN;
        double y = HIVE_Y_OFFSET * Y_SIGN;
        double z = CELL_PIVOT_TO_OPEN_HYPOT * Math.sin(clusterDetection.ftcPose.pitch + CELL_ANGLE_TO_OPEN * -X_SIGN) * Y_SIGN + FRAME_PIVOT_HEIGHT_FROM_GROUND;

        return new AprilTagPoseFtc(x, y, z, clusterDetection.ftcPose.roll, clusterDetection.ftcPose.pitch, clusterDetection.ftcPose.yaw, clusterDetection.ftcPose.range,clusterDetection.ftcPose.elevation,clusterDetection.ftcPose.range);
    }
    public boolean isTipping(){
        // TODO
        return false;
    }
}

