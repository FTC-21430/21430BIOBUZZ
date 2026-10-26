package org.firstinspires.ftc.teamcode.robot.tuning;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import android.util.Size;

import java.util.ArrayList;

public abstract class CameraConfiguration {
    private final WebcamName NAME;
    private Size cameraResolution;
    // fx, fy, cx, cy
    private ArrayList<Double> lensIntrinsics;
    private Position cameraPosition;
    private YawPitchRollAngles cameraOrientation;
    public CameraConfiguration(WebcamName name, ArrayList<Double> lensIntrinsics, Size cameraResolution, Position cameraPosition, YawPitchRollAngles cameraOrientation){
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (lensIntrinsics == null) {
            throw new IllegalArgumentException("lensIntrinsics cannot be null");
        }
        if (lensIntrinsics.size() != 4) {
            throw new IllegalArgumentException("lensIntrinsics must contain exactly 4 elements: fx, fy, cx, cy");
        }
        if (cameraResolution == null) {
            throw new IllegalArgumentException("cameraResolution cannot be null");
        }
        if (cameraOrientation == null){
            throw new IllegalArgumentException("cameraOrientation cannot be null.");
        }
        if (cameraPosition == null){
            throw new IllegalArgumentException("cameraPosition cannot be null");
        }

        this.NAME = name;
        this.lensIntrinsics = lensIntrinsics;
        this.cameraResolution = cameraResolution;
        this.cameraPosition = cameraPosition;
        this.cameraOrientation = cameraOrientation;
    }
    public WebcamName getNAME(){
        return NAME;
    }
    public Size getCameraResolution(){
        return cameraResolution;
    }
    public ArrayList<Double> getLensIntrinsics(){
        return lensIntrinsics;
    }
    public Position getCameraPosition(){
        return cameraPosition;
    }
    public YawPitchRollAngles getCameraOrientation(){
        return cameraOrientation;
    }
}
