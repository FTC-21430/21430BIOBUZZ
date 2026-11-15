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
}
class Pair {
    public double key1;
    public double key2;
    public Pair(double key1, double key2){
        this.key1 = key1;
        this.key2 = key2;
    }
    public boolean equals(Pair other){
        double EPSILON = 1e-7;
        return this.key1 - other.key1 < EPSILON && this.key2 - other.key2 < EPSILON;
    }
}
