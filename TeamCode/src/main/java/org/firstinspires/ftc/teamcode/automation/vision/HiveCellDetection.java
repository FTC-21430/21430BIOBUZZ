package org.firstinspires.ftc.teamcode.automation.vision;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.teamcode.automation.vision.HiveAprilTagsPortal.CELLS;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseRaw;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class HiveCellDetection {
    // The cluster detection found by the AprilTag Processor
    AprilTagClusterDetection clusterDetection;
    private final double TIPPING_THRESHOLD = 5.0; // degrees, how far does the hive have to be from rest to be considered mid-tip
    // ## Cell to field constants measured from BIO BUZZ official CAD model.
    private final double CELL_PIVOT_TO_OPEN_X = 21.189; //Inches
    private final double CELL_PIVOT_TO_OPEN_Z = 4.253; //Inches
    private final double CELL_PIVOT_TO_OPEN_HYPOT = Math.hypot(CELL_PIVOT_TO_OPEN_X, CELL_PIVOT_TO_OPEN_Z);
    private final double CELL_ANGLE_TO_OPEN = Math.atan(CELL_PIVOT_TO_OPEN_Z / CELL_PIVOT_TO_OPEN_X); // approx 11.349 degrees
    private final double FRAME_PIVOT_HEIGHT_FROM_GROUND = 43.950; //Inches
    private final double HIVE_Y_OFFSET = 12.750; //Inches

    // Which cell are you looking at? (e.g. RedAudience, BlueScoring, etc...)
    private final CELLS CELL_TYPE;
    private final int X_SIGN; // is it in Q1/Q4 or in Q2/Q3?
    private final int Y_SIGN; // is it in Q1/Q2 or in Q3/Q4?

    // Hives on opposite sides of the field are mirror images, so positive pitch rotates about Y in opposite directions.
    private final int PITCH_SIGN;
    // Rotation from the field-aligned tag frame to the real AprilTag frame (x right, y down, z into the tag).
    private final OpenGLMatrix tagAxesFromFieldAxes;
    //tuned valued from camera configuration class.
    private final OpenGLMatrix cameraToTurretZeroTransformation;
    private final OpenGLMatrix turretToRobotTransformation;
    private final long acquisitionTime; // nanoseconds, when was this data collected during runtime?
    private Pose3D hivePosition;

    /**
     * Constructor for one instance of a Hive cell detection
     * must call {@link getFieldPose(double turretAngle)} to calculate position, then use class getters.
     * @param clusterDetection the detection from AprilTagProcessor
     * @param cameraToTurretAtZeroTransformation Where is the pivot of the turret to camera? (X+ is forward, Y+ is to the left, counterclockwise rotation degrees)
     * @param turretToRobotTransformation where is the pivot of the turret to the camera? NOTE: must be the same pivot point as the previous transformer.
     * @param acquisitionTime time (nanoseconds) when this sample was collected.
     */
    public HiveCellDetection(AprilTagClusterDetection clusterDetection, OpenGLMatrix cameraToTurretAtZeroTransformation, OpenGLMatrix turretToRobotTransformation, long acquisitionTime){
        this.clusterDetection = clusterDetection;
        this.cameraToTurretZeroTransformation = cameraToTurretAtZeroTransformation;
        this.turretToRobotTransformation = turretToRobotTransformation;
        this.acquisitionTime = acquisitionTime;
        // convert from FTC string labels to enum and set quadrant.
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
                Y_SIGN = 1;
                break;
            default:
                CELL_TYPE = CELLS.RED_POSITIVE;
                X_SIGN = 1;
                Y_SIGN = -1;
        }
        PITCH_SIGN = -X_SIGN; // ensure that the pitch of the cell is correct for the trig functions later
        // The tag normal points away from the pivot (field X*X_SIGN), so the tag z axis (into the tag) is field -X*X_SIGN, y is down, x is image-right.
        tagAxesFromFieldAxes = OpenGLMatrix.identityMatrix(); // define the wacky axis of the april tag detections to the common field system.
        tagAxesFromFieldAxes.put(0, 0, 0); tagAxesFromFieldAxes.put(0, 1, 0); tagAxesFromFieldAxes.put(0, 2, -X_SIGN);
        tagAxesFromFieldAxes.put(1, 0, X_SIGN); tagAxesFromFieldAxes.put(1, 1, 0); tagAxesFromFieldAxes.put(1, 2, 0);
        tagAxesFromFieldAxes.put(2, 0, 0); tagAxesFromFieldAxes.put(2, 1, -1); tagAxesFromFieldAxes.put(2, 2, 0);
    }

    /**
     * Returns the raw pos of the cluster pos to the optical sensor of the camera.
     * @return direct FTC SDK tag pose.
     */
    public AprilTagPoseRaw getRawPose(){
        return clusterDetection.rawPose;
    }

    /**
     * Calculate the cell's position and the robot's position.
     * @param turretAngle the angle of the turret which the camera is mounted to. 0 is facing robot x-axis (AprilTag system), counterclockwise +, degrees.
     * @return the robot's 3D position on the field
     */
    public Pose3D getFieldPose(double turretAngle){
        OpenGLMatrix robotToTag = turretToRobotTransformation
                .multiplied(OpenGLMatrix.rotation(AngleUnit.DEGREES, (float) turretAngle, 0, 0, 1))
                .multiplied(cameraToTurretZeroTransformation)
                .multiplied(rawPoseMatrix())
                .multiplied(tagAxesFromFieldAxes.inverted());
        //TODO make the matrix multiplication labels better, and check signs and exact math.
        // The robot is planar with the floor, so the tag's tilt relative to the robot is the hive pitch.
        double hivePitchRad = PITCH_SIGN * Math.asin(clamp(-robotToTag.get(2, 0)));
        double hivePitchDeg = Math.toDegrees(hivePitchRad);

        double hiveX = CELL_PIVOT_TO_OPEN_HYPOT * Math.cos(hivePitchRad + CELL_ANGLE_TO_OPEN) * X_SIGN;
        double hiveY = HIVE_Y_OFFSET * Y_SIGN;
        double hiveZ = CELL_PIVOT_TO_OPEN_HYPOT * Math.sin(hivePitchRad + CELL_ANGLE_TO_OPEN) + FRAME_PIVOT_HEIGHT_FROM_GROUND;

        hivePosition = new Pose3D(new Position(DistanceUnit.INCH, hiveX, hiveY,hiveZ,0), new YawPitchRollAngles(AngleUnit.DEGREES,0,hivePitchDeg,0,0));

        OpenGLMatrix fieldToRobot = OpenGLMatrix.translation((float) hiveX, (float) hiveY, (float) hiveZ)
                .multiplied(OpenGLMatrix.rotation(AngleUnit.DEGREES, (float) (PITCH_SIGN * hivePitchDeg), 0, 1, 0))
                .multiplied(robotToTag.inverted());

        double yaw = Math.toDegrees(Math.atan2(fieldToRobot.get(1, 0), fieldToRobot.get(0, 0)));
        double pitch = Math.toDegrees(Math.asin(clamp(-fieldToRobot.get(2, 0))));
        double roll = Math.toDegrees(Math.atan2(fieldToRobot.get(2, 1), fieldToRobot.get(2, 2)));

        return new Pose3D(
                new Position(DistanceUnit.INCH, fieldToRobot.get(0, 3), fieldToRobot.get(1, 3), fieldToRobot.get(2, 3),getAcquisitionTimeNanoSeconds()),
                new YawPitchRollAngles(AngleUnit.DEGREES, yaw, pitch, roll, 0));
    }

    /**
     * get the Hive cell position relative to the field origin.
     * Must be called after {@link getFieldPose(double turretAngle)}
     * @return Pose3D of hive cell position
     */
    public Pose3D getHivePosition(){
        if (hivePosition == null) throw new RuntimeException("Must call getFieldPose First to calculate the hive position");
        return hivePosition;
    }

    /**
     * If the reported hive pitch is between its resting points, its tipping!
     * Must be called after {@link getFieldPose(double turretAngle)}
     * @return boolean true if mid-tip
     */
    public boolean isTipping(){
        return hivePosition.getOrientation().getPitch() < 30-TIPPING_THRESHOLD && hivePosition.getOrientation().getPitch() > -30+TIPPING_THRESHOLD;
    }

    /**
     * Get which cell this is
     * @return Cell type
     */
    public CELLS getCellType(){
        return CELL_TYPE;
    }

    /**
     * private helper method, used to ensure any double v is within range of -1-1
     * @param v target value
     * @return v clamped to correct range
     */
    private static double clamp(double v) {
        return Math.max(-1, Math.min(1, v));
    }

    /**
     * Private helper class
     * Get the raw Position of the Tag to Camera relative in OpenGLMatrix form
     * @return OpenGL Matrix
     */
    private OpenGLMatrix rawPoseMatrix() {
        AprilTagPoseRaw p = clusterDetection.rawPose;
        OpenGLMatrix m = OpenGLMatrix.identityMatrix();
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                m.put(r, c, p.R.get(r, c));
        m.put(0, 3, (float) p.x);
        m.put(1, 3, (float) p.y);
        m.put(2, 3, (float) p.z);
        return m;
    }

    /**
     * Getter for the time at which this cell detection was collected
     * Not particularly useful unless needing or comparing exact timestamps
     * @return time in nanoseconds
     */
    public long getAcquisitionTimeNanoSeconds() {
        return acquisitionTime;
    }
}

