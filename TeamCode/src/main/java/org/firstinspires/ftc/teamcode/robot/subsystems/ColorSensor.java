package org.firstinspires.ftc.teamcode.robot.subsystems;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import java.util.ArrayList;

public class ColorSensor {

    //    ColorSensor class used in example op modes from the FIRST SDK
    private final NormalizedColorSensor colorSensor;

    // constants for color ranges. At the moment the difference is calculated based on the Hue value in the HSV color space. One tuning set for each potential color we are searching for
    public static float[] noneValues = new float[]{
            120,
            0,
            0
    };
    public static float[] blueValues = new float[]{
            200,
            0,
            0
    };
    public static float[] redValues = new float[]{
            150,
            0,
            0
    };
    public static float[] yellowValues = new float[]{
            150,
            0,
            0
    };

    // Enum return value for all colors we are detecting
    public enum COLORS{
        NONE,
        BLUE,
        RED,
        YELLOW,
    }

    /**
     * Constructor for this class, uses hardwareMap to get sensor object and sets the gain of the sensor
     * @param hardwareMap hardwareMap instance from the opMode that we are currently running
     * @param configName the name that the sensor is saved under in the robot controller app configuration file - CASE SENSITIVE!
     */
    public ColorSensor(HardwareMap hardwareMap, String configName){
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, configName);

//        constant value for gain of the sensor. for gain: higher the number, higher the values / the difference between different colors.
        final float sensorGain = 10;
        colorSensor.setGain(sensorGain);
    }

    /**
     * reads sensor values, identifies the difference between the colors we are searching for and the current readings.
     * @return Color ENUM of the color that is the closest match to readings.
     */
    public COLORS getDetectedColor(){
//        Read color values and get the distances between configuration values and raw readings
        ArrayList<Double> distances = getColorDistances();

//        search for which of the colors has the lowest value
        int lowestIndex = 0;
        double lowestDistance = 0;
        for (int i = 0; i < 4; i++){
            if (i==0){
                lowestDistance = distances.get(i);
            }else{
                if (Math.abs(distances.get(i)) < lowestDistance){
                    lowestIndex = i;
                    lowestDistance = Math.abs(distances.get(i));
                }
            }
        }
//          match the index of the lowest color error to one of the colors we are searching for
        switch (lowestIndex){
            case 0:
                return COLORS.NONE;
            case 1:
                return COLORS.BLUE;
            case 2:
                return COLORS.RED;
            case 3:
                return COLORS.YELLOW;
        }
//        If index is out of range
        return COLORS.NONE;
    }

    /**
     * Setter for the sensors gain, must be within values of 0-15
     * @param gain the amount the sensor takes in: higher the value, larger the difference between different colors
     */
    public void setGain(float gain){
        if (gain < 0 || gain > 15){
            return;
        }
        colorSensor.setGain(gain);
    }

    /**
     * gets value from the sensor and converts from RGBA to HSV
     * @return returns color in HSV
     */
    public float[] getRawData(){
        float[] hsvValues = new float[3];
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        Color.colorToHSV(colors.toColor(), hsvValues);

                return hsvValues;
    }

    /**
     * gets the raw data and compares it to the calibrated constants at the top of this file.
     * @return all three color errors in order, None, Purple, Green
     */
    private ArrayList<Double> getColorDistances(){
        ArrayList<Double> returnValues = new ArrayList<>();
        float[] hsvValues = getRawData();

        double noneDistance = hsvValues[0] - noneValues[0];
        returnValues.add(noneDistance);

        double purpleDistance = hsvValues[0] - blueValues[0];
        returnValues.add(purpleDistance);

        double greenDistance = hsvValues[0] - redValues[0];
        returnValues.add(greenDistance);

        return returnValues;
    }
}