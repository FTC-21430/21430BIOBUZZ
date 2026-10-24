package org.firstinspires.ftc.teamcode.Resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PIDControllerTest {

    private static final double DELTA = 1e-9;

    private PIDController pid;

    @Before
    public void setUp() {
    }

    @Test
    public void constructor_storesConstants() {
    }

    @Test
    public void constructor_readsInitialTimeFromRuntime() {
    }

    @Test
    public void updatePIDConstants_replacesConstants() {
    }

    @Test
    public void setTarget_thenGetTarget_returnsSameValue() {
    }

    @Test
    public void update_proportionalOnly_powerIsNegativeErrorTimesP() {
    }

    @Test
    public void update_atTarget_powerIsZero() {
    }

    @Test
    public void update_largeError_powerIsClampedToPlusMinusOne() {
    }

    @Test
    public void update_integralAccumulatesOverMultipleLoops() {
    }

    @Test
    public void update_saturated_integralIsNotWoundUp() {
    }

    @Test
    public void update_derivativeUsesErrorChangeOverDeltaTime() {
    }

    @Test
    public void update_wrapEnabled_errorIsWrappedTo180() {
    }

    @Test
    public void update_wrapDisabled_errorIsNotWrapped() {
    }

    @Test
    public void setIntegralMode_false_zeroesIConstant() {
    }

    @Test
    public void setIntegralMode_trueAfterFalse_restoresIConstantAndResetsISum() {
    }

    @Test
    public void setWrap_storesMinAndMax() {
    }
}
