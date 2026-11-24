package org.firstinspires.ftc.teamcode.resources;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class BiLinearInterpolation {
    private ArrayList<Pair> keys;
    private ArrayList<Double> valueSet1;
    private ArrayList<Double> valueSet2;
    public BiLinearInterpolation(ArrayList<BiLinearInterpolationMeasurement> measurements){
        this.keys = new ArrayList<>();
        this.valueSet1 = new ArrayList<>();
        this.valueSet2 = new ArrayList<>();
        for(BiLinearInterpolationMeasurement measurement: measurements){
            for (Pair key: keys){
                if (key.equals(new Pair(measurement.key1,measurement.key2))){
                    throw new IllegalArgumentException("Duplicate key pair found in measurements");
                }
            }
            keys.add(new Pair(measurement.key1,measurement.key2));
            valueSet1.add(measurement.value1);
            valueSet2.add(measurement.value2);
        }
    }
    public Pair GetInterpolatedPairAlongX(Pair tunedPair1, Pair tunedPair2, double targetX){
        double scalar1 = (tunedPair2.x - targetX)/(tunedPair2.x - tunedPair1.x);
        double scalar2 = (targetX - tunedPair1.x)/(tunedPair2.x-tunedPair1.x);

        return new Pair(scalar1 * tunedPair1.x + scalar2 * tunedPair2.x, scalar1 * tunedPair1.y + scalar2 * tunedPair2.y);
    }
    public Pair GetInterpolationPairAlongY(Pair pair1, Pair pair2, double targetY){
        double scalar1 = (pair2.y - targetY)/(pair2.y-pair1.y);
        double scalar2 = (targetY - pair1.y)/(pair2.y-pair1.y);

        return new Pair(pair1.x, scalar1 * pair1.y + scalar2 * pair2.y);
    }
}

class Pair {
    public double x;
    public double y;
    public Pair(double key1, double key2){
        this.x = key1;
        this.y = key2;
    }
    public boolean equals(Pair other){
        double EPSILON = 1e-7;
        return this.x - other.x < EPSILON && this.y - other.y < EPSILON;
    }
}
