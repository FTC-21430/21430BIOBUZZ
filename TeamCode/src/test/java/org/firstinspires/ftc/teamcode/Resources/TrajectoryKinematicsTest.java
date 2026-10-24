package org.firstinspires.ftc.teamcode.Resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TrajectoryKinematicsTest {

    private static final double DELTA = 1e-6;

    private TrajectoryKinematics kinematics;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getBearingToTag_red_usesRedGoalCoordinates() {
    }

    @Test
    public void getBearingToTag_redAuto_usesAutoGoalCoordinates() {
    }

    @Test
    public void getBearingToTag_blue_mirrorsGoalY() {
    }

    @Test
    public void getBearingToTag_unknownMode_noFlywheelOffset() {
    }

    @Test
    public void getBearingToTag_appliesVelocityCorrection() {
    }

    @Test
    public void getDistance_red_isHypotToGoal() {
    }

    @Test
    public void getDistance_blue_mirrorsGoalY() {
    }

    @Test
    public void getDistance_atGoal_isZero() {
    }

    @Test
    public void calculateTrajectory_setsInitialAngleAndLaunchMagnitude() {
    }

    @Test
    public void calculateTrajectory_angleIsCappedAt84() {
    }

    @Test
    public void calculateTrajectory_magnitudeIsCappedAt1600() {
    }

    @Test
    public void calculateTrajectory_flywheelErrorAdjustsAngle() {
    }

    @Test
    public void calculateTrajectory_postsTelemetry() {
    }

    @Test
    public void updateVelocities_setsTargetCorrectionsFromScalars() {
    }

    @Test
    public void necessaryRampOffset_isAbsErrorTimesFactor() {
    }

    @Test
    public void necessaryRampOffset_negativeError_sameAsPositive() {
    }
}
