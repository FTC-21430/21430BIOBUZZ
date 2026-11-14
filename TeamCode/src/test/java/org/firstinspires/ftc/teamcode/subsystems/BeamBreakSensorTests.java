package org.firstinspires.ftc.teamcode.subsystems;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robot.subsystems.BeamBreakSensor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BeamBreakSensorTests {
    @Mock
    HardwareMap hardwareMap;
    @Mock
    DigitalChannel mockSensor;

    BeamBreakSensor beamBreakSensor;
    @Before
    public void setup() {
        when(hardwareMap.get(DigitalChannel.class, "mockSensor")).thenReturn(mockSensor);
        beamBreakSensor = new BeamBreakSensor("mockSensor", hardwareMap,false);
    }
    @After
    public void tearDown() {
        beamBreakSensor = null;
    }

    @Test
    public void testBeamBreakSensor() {
        when(mockSensor.getState()).thenReturn(true); // Simulate beam unbroken
        when(mockSensor.getState()).thenReturn(true); // Simulate beam broken
        beamBreakSensor.update();
        assert beamBreakSensor.isBeamBroken() : "Beam should be broken";
        when(mockSensor.getState()).thenReturn(false); // Simulate beam unbroken
        beamBreakSensor.update();
        assert !beamBreakSensor.isBeamBroken() : "Beam should not be broken";
    }
    @Test
    public void testRisingEdgeDetection() {
        when(mockSensor.getState()).thenReturn(true); // Simulate beam unbroken
        when(mockSensor.getState()).thenReturn(false); // Simulate beam broken
        beamBreakSensor.update();
        when(mockSensor.getState()).thenReturn(true); // Simulate beam unbroken
        beamBreakSensor.update();
        assert beamBreakSensor.isRisen();
        assert !beamBreakSensor.isFallen();
    }
    @Test
    public void testFallingEdgeDetection() {
        when(mockSensor.getState()).thenReturn(false); // Simulate beam broken
        when(mockSensor.getState()).thenReturn(true); // Simulate beam unbroken
        beamBreakSensor.update();
        when(mockSensor.getState()).thenReturn(false); // Simulate beam broken
        beamBreakSensor.update();
        assert beamBreakSensor.isFallen();
        assert !beamBreakSensor.isRisen();
    }

    @Test
    public void initialStateIsUnbrokenWithoutAnEdge() {
        assertFalse(beamBreakSensor.isBeamBroken());
        assertFalse(beamBreakSensor.isRisen());
        assertFalse(beamBreakSensor.isFallen());
        verify(mockSensor).setMode(DigitalChannel.Mode.INPUT);
    }

    @Test
    public void repeatedBrokenSamplesClearTheRisingEdge() {
        when(mockSensor.getState()).thenReturn(true);

        beamBreakSensor.update();
        assertTrue(beamBreakSensor.isRisen());
        beamBreakSensor.update();

        assertTrue(beamBreakSensor.isBeamBroken());
        assertFalse(beamBreakSensor.isRisen());
        assertFalse(beamBreakSensor.isFallen());
    }

    @Test
    public void repeatedUnbrokenSamplesDoNotCreateEdges() {
        when(mockSensor.getState()).thenReturn(false);
        beamBreakSensor.update();
        beamBreakSensor.update();
        assertFalse(beamBreakSensor.isBeamBroken());
        assertFalse(beamBreakSensor.isRisen());
        assertFalse(beamBreakSensor.isFallen());
    }

    @Test
    public void invertedSensorDetectsBothEdges() {
        when(mockSensor.getState()).thenReturn(true, false, true);
        BeamBreakSensor beamBreakSensor = new BeamBreakSensor("mockSensor", hardwareMap, true);
        beamBreakSensor.update();
        assertFalse(beamBreakSensor.isBeamBroken());
        beamBreakSensor.update();
        assertTrue(beamBreakSensor.isBeamBroken());
        assertTrue(beamBreakSensor.isRisen());
        assertFalse(beamBreakSensor.isFallen());
        beamBreakSensor.update();
        assertFalse(beamBreakSensor.isBeamBroken());
        assertFalse(beamBreakSensor.isRisen());
        assertTrue(beamBreakSensor.isFallen());
    }

    @Test
    public void disabledSensorStillSamplesTransitions() {
        when(mockSensor.getState()).thenReturn(true, false);
        beamBreakSensor.setDisabled(true);
        beamBreakSensor.update();
        assertTrue(beamBreakSensor.isBeamBroken());
        assertTrue(beamBreakSensor.isRisen());
        beamBreakSensor.setDisabled(false);
        beamBreakSensor.update();
        assertFalse(beamBreakSensor.isBeamBroken());
        assertTrue(beamBreakSensor.isFallen());
    }

}