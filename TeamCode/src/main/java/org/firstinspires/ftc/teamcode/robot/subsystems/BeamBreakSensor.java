package org.firstinspires.ftc.teamcode.robot.subsystems;

import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

/** Tracks a digital beam-break sensor and detects changes between updates. */
@SuppressWarnings("unused")
public class BeamBreakSensor implements Subsystem {
    private final DigitalChannel sensor;
    private final boolean isInverted;
    private boolean wasBeamBroken = false;
    private boolean isBeamBroken = false;
    /**
     * Configures a sensor whose high digital state indicates a broken beam.
     *
     * @param hardwareAddress configured name of the digital channel
     * @param hardwareMap hardware devices available to the robot
     */
    public BeamBreakSensor(String hardwareAddress, HardwareMap hardwareMap) {
        sensor = hardwareMap.get(DigitalChannel.class, hardwareAddress);
        sensor.setMode(DigitalChannel.Mode.INPUT);
        isInverted = false;
    }
    /**
     * Configures a sensor with the specified digital-state polarity.
     *
     * @param hardwareAddress configured name of the digital channel
     * @param hardwareMap hardware devices available to the robot
     * @param isInverted whether a low digital state indicates a broken beam
     */
    public BeamBreakSensor(String hardwareAddress, HardwareMap hardwareMap, boolean isInverted) {
        sensor = hardwareMap.get(DigitalChannel.class, hardwareAddress);
        sensor.setMode(DigitalChannel.Mode.INPUT);
        this.isInverted = isInverted;
    }
    /** Samples the sensor and saves the previous sample for edge detection. */
    @Override
    public void update() {
        wasBeamBroken = isBeamBroken;
        isBeamBroken = isInverted != sensor.getState();
    }
    /**
     * Leaves the sensor enabled regardless of the requested state.
     *
     * @param disable requested disabled state
     */
    @Override
    public void setDisabled(boolean disable) {
        // do nothing, beam break sensor is always enabled
    }
    /**
     * Returns the most recently sampled beam state, initially unbroken.
     *
     * @return whether the beam was broken at the last update
     */
    public boolean isBeamBroken() {
        return isBeamBroken;
    }
    /**
     * Checks whether the beam changed from unbroken to broken at the last update.
     *
     * @return whether the last update detected a rising edge
     */
    public boolean isRisen() {
        return !wasBeamBroken && isBeamBroken;
    }
    /**
     * Checks whether the beam changed from broken to unbroken at the last update.
     *
     * @return whether the last update detected a falling edge
     */
    public boolean isFallen() {
        return wasBeamBroken && !isBeamBroken;
    }

}
