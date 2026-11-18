package org.firstinspires.ftc.teamcode.resources;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.robot.subsystems.BeamBreakSensor;

public class NectarCounting {
    private BeamBreakSensor beamBreakSensor;
    private ElapsedTime runtime;
    private double timeout = 250; //In milliseconds
    private int nectarCount = 0; //Number of nectar in robot

    /**
     * @param hardwareMap gets hardwareMap for the param of BeamBrakeSensor.java
     */
    public NectarCounting(HardwareMap hardwareMap, ElapsedTime runtime) {
        this.runtime=runtime;
        String hardwareAddress = "NectarBeamBreak";
        beamBreakSensor = new BeamBreakSensor(hardwareAddress,hardwareMap);
    }
    
    /**
     * Checks for if the Nectar timeout has passed and then if there's a new Nectar
     */
    public void update(){
        if(timeout>runtime.milliseconds()){
            return;
        }
        if(beamBreakSensor.isRisen()){
            changeNectar(1);
            runtime.reset();
        }
    }

    /**
     * Sets Nectar count, mostly for autos
     * @param newNectarCount the new and correct quantity of Nectar in the robot
     */
    public void setNectarCount(int newNectarCount) {
        nectarCount = newNectarCount;
        if(nectarCount<0) {
            nectarCount=0;
        }
    }
    
    /**
     * Used for launching, or other situations where a change of nectar is reliant on the nectar quantity
     * @param nectarChange the change in nectar
     */
    public void changeNectar(int nectarChange) {
        nectarCount+=nectarChange;
        if(nectarCount<0) {
            nectarCount=0;
        }
    }
    
    /**
     * Is the getter for number of Nectar
     * @return returns the number of Nectar in the robot
     */
    public int getNectarCount(){
        return nectarCount;
    }

}
