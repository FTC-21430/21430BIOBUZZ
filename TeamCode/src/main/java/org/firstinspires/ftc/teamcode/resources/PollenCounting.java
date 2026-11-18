package org.firstinspires.ftc.teamcode.resources;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.robot.subsystems.BeamBreakSensor;

public class PollenCounting {
    private BeamBreakSensor beamBreakSensor;
    private ElapsedTime runtime;
    public static double timeout = 200; //In milliseconds //TODO:Get an actual number
    private int pollenCount = 0; //Number of pollen in robot

    /**
     * @param hardwareMap gets hardwareMap for the param of BeamBrakeSensor.java
     */
    public PollenCounting(HardwareMap hardwareMap, ElapsedTime runtime) {
        this.runtime=runtime;
        String hardwareAddress = "PollenBeamBreak";
        beamBreakSensor = new BeamBreakSensor(hardwareAddress,hardwareMap);
    }

    /**
     * Checks for if the pollen timeout has passed and then if there's a new pollen
     */
    public void update(){
        if(timeout>runtime.milliseconds()){
            return;
        }
        if(beamBreakSensor.isRisen()){
            changePollen(1);
            runtime.reset();
        }
    }

    /**
     * Sets pollen count, mostly for autos
     * @param newPollenCount the new and correct quantity of pollen in the robot
     */
    public void setPollenCount(int newPollenCount) {
        pollenCount = newPollenCount;
        if(pollenCount<0) {
            pollenCount=0;
        }
    }

    /**
     * Used for launching, or other situations where a change of pollen is reliant on the pollen quantity
     * @param pollenChange the change in pollen
     */
    public void changePollen(int pollenChange) {
        pollenCount+=pollenChange;
        if(pollenCount<0) {
            pollenCount=0;
        }
    }

    /**
     * Is the getter for number of pollen
     * @return returns the number of pollen in the robot
     */
    public int getPollenCount(){return pollenCount;}
}
