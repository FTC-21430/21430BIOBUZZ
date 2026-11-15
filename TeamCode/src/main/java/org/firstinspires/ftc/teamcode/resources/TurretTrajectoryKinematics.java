package org.firstinspires.ftc.teamcode.resources;

import com.broombots.sweep.Classes.LerpFunction;

@SuppressWarnings("unused")
public class TurretTrajectoryKinematics {
    BiLinearInterpolation solutionMap;
    public TurretTrajectoryKinematics(LerpFunction distanceToSpeed, LerpFunction distanceToAngle, LerpFunction heightToSpeed, LerpFunction heightToAngle){
        solutionMap = new BiLinearInterpolation(distanceToSpeed, distanceToAngle, heightToSpeed, heightToAngle);
    }
    public double[] getSolution(double distance, double height){
        return solutionMap.getValues(distance,height);
    }
}
