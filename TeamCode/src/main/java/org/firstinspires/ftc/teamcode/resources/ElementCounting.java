package org.firstinspires.ftc.teamcode.resources;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.robot.subsystems.BeamBreakSensor;
import org.firstinspires.ftc.teamcode.robot.subsystems.ColorSensor;

public class ElementCounting {
    private BeamBreakSensor beamBreakSensor;
    private ElapsedTime runtime;
    private ColorSensor colorSensor;
    private double pollenTimeout = 200; //In milliseconds
    private double nectarTimeout = 250; //In milliseconds
    private int elementCount = 0; //Number of element in robot
    private boolean isPollen;
    private boolean isRedAlliance;

    /**
     * @param hardwareMap gets hardwareMap for the param of BeamBrakeSensor.java
     */
    public ElementCounting(HardwareMap hardwareMap, ColorSensor colorSensor, ElapsedTime runtime, boolean isRedAlliance) {
        this.isRedAlliance = isRedAlliance;
        this.runtime=runtime;
        String hardwareAddress = "beamBreak";
        beamBreakSensor = new BeamBreakSensor(hardwareAddress,hardwareMap);
    }
    
    /**
     * Checks for if the element timeout has passed and then if there's a new element
     */
    public void update(){
        if(isPollen && pollenTimeout>runtime.milliseconds()){
            return;
        }
        if(!isPollen && nectarTimeout>runtime.milliseconds()){
            return;
        }
        if(beamBreakSensor.isBeamBroken()){
            if(colorSensor.getDetectedColor()== ColorSensor.COLORS.NONE){return;}
            changeElement(1);
            if (colorSensor.getDetectedColor() == ColorSensor.COLORS.BLUE && isRedAlliance){
                //out take everything
                setElementCount(0);
                return;
            }
            if (colorSensor.getDetectedColor() == ColorSensor.COLORS.RED && !isRedAlliance){
                //out take everything
                setElementCount(0);
                return;
            }
            isPollen = colorSensor.getDetectedColor() != ColorSensor.COLORS.RED && colorSensor.getDetectedColor() != ColorSensor.COLORS.BLUE;
            runtime.reset();
        }
    }

    /**
     * Sets element count, mostly for autos
     * @param newElementCount the new and correct quantity of element in the robot
     */
    public void setElementCount(int newElementCount) {
        elementCount = newElementCount;
        if(elementCount<0) {
            elementCount=0;
        }
    }
    
    /**
     * Used for launching, or other situations where a change of element is reliant on the element quantity
     * @param elementChange the change in element
     */
    public void changeElement(int elementChange) {
        elementCount+=elementChange;
        if(elementCount<0) {
            elementCount=0;
        }
    }
    
    /**
     * Is the getter for number of element
     * @return returns the number of element in the robot
     */
    public int getElementCount(){
        return elementCount;
    }

}
