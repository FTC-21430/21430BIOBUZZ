package org.firstinspires.ftc.teamcode.Resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PIDFControllerTest {

    private static final double DELTA = 1e-9;

    private PIDFController pidf;

    @Before
    public void setUp() {
    }

    @Test
    public void constructor_storesFConstant() {
    }

    @Test
    public void updatePIDFConstants_updatesAllFourConstants() {
    }

    @Test
    public void getPower_addsFConstantToPidPower() {
    }

    @Test
    public void getPower_canExceedOneBecauseFIsAddedAfterClamp() {
    }
}
