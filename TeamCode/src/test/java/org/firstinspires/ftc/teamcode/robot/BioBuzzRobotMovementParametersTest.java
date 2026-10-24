package org.firstinspires.ftc.teamcode.robot;

import static org.junit.Assert.assertEquals;

import com.broombots.sweep.Defaults.MecanumDriveAcceleration;

import org.ejml.simple.SimpleMatrix;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BioBuzzRobotMovementParametersTest {

    private static final double DELTA = 1e-9;

    private BioBuzzRobotMovementParameters params;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getMaxVelocity_straight_matchesTopSpeedFormula() {
    }

    @Test
    public void getMaxAngleVelocity_is360OverTimeForFullTurn() {
    }

    @Test
    public void getMaxStableAcceleration_scalesWithRobotMass() {
    }

    @Test
    public void getMaxStableAngularAcceleration_isConstant200() {
    }

    @Test
    public void getAngleFullPowerToErrorThreshold_is40() {
    }

    @Test
    public void getPIDCoefficients_is3x3WithExpectedValues() {
    }
}
