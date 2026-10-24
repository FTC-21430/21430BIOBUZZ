package org.firstinspires.ftc.teamcode.Resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RotationControlTest {

    private static final double DELTA = 1e-9;

    private RotationControl rotationControl;

    @Before
    public void setUp() {
    }

    @Test
    public void constructor_setsInitialTargetAngle() {
    }

    @Test
    public void getOutputPower_returnsPidPower() {
    }

    @Test
    public void getOutputPower_postsTelemetry() {
    }

    @Test
    public void setPIDController_updatesUnderlyingPidConstants() {
    }

    @Test
    public void getPIDController_returnsPowerTermsNotConstants() {
    }

    @Test
    public void setTargetAngle_thenGetTargetAngle_returnsSameValue() {
    }

    @Test
    public void changeTargetByJoystick_joystickHeld_movesTargetByTurnRateTimesDeltaTime() {
    }

    @Test
    public void changeTargetByJoystick_joystickReleased_snapsTargetToCurrentAngle() {
    }

    @Test
    public void changeTargetByJoystick_joystickAtRest_targetUnchanged() {
    }

    @Test
    public void changeTargetByJoystick_resetsTimerEachCall() {
    }
}
