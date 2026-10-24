package org.firstinspires.ftc.teamcode.Resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import com.qualcomm.robotcore.hardware.Servo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServoPlusTest {

    private static final double DELTA = 1e-9;

    private ServoPlus servoPlus;

    @Before
    public void setUp() {
    }

    @Test
    public void setServoPos_inRange_setsPositionAsAngleOverRangeOfMotion() {
    }

    @Test
    public void setServoPos_belowMin_isClippedToMin() {
    }

    @Test
    public void setServoPos_aboveMax_isClippedToMax() {
    }

    @Test
    public void setServoPos_updatesStoredAngle() {
    }

    @Test
    public void getServoPos_beforeAnySet_isZero() {
    }

    @Test
    public void moveServoBy_addsToCurrentAngle() {
    }

    @Test
    public void moveServoBy_pastMax_isClipped() {
    }

    @Test
    public void moveServoBy_negative_movesBackward() {
    }
}
