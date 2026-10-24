package org.firstinspires.ftc.teamcode.robot.subsystems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LauncherRampTest {

    private static final double DELTA = 1e-9;

    private LauncherRamp ramp;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void constructor_getsRampServoByName() {
    }

    @Test
    public void setLaunchAngle_inRange_setsServoPosition() {
    }

    @Test
    public void setLaunchAngle_belowMin_isClippedToMin() {
    }

    @Test
    public void setLaunchAngle_aboveMaxMinus6_isClippedToMaxMinus6() {
    }

    @Test
    public void setLaunchAngle_bigMove_resetsMovementTimeout() {
    }

    @Test
    public void setLaunchAngle_smallMove_doesNotResetTimeout() {
    }

    @Test
    public void retract_setsServoToMinAngle() {
    }

    @Test
    public void extendFull_setsServoToMaxAngle() {
    }

    @Test
    public void midAngle_setsServoBetweenLimits() {
    }

    @Test
    public void isReady_beforeTimeout_isFalse() {
    }

    @Test
    public void isReady_afterTimeout_isTrue() {
    }
}
