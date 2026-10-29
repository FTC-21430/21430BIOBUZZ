package org.firstinspires.ftc.teamcode.robot.subsystems;

import  static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FlywheelTest {

    private static final double DELTA = 1e-9;

    @Mock private HardwareMap hardwareMap;
    @Mock private DcMotorEx flywheelMotor;
    @Mock private Telemetry telemetry;
    @Mock private ElapsedTime runtime;
    @Captor private ArgumentCaptor<PIDFCoefficients> pidfCaptor;

    private double savedP, savedI, savedD, savedF;
    private Flywheel flywheel;

    @Before
    public void setUp() {
        savedP = Flywheel.p;
        savedI = Flywheel.i;
        savedD = Flywheel.d;
        savedF = Flywheel.f;

        when(hardwareMap.get(DcMotorEx.class, "flywheel")).thenReturn(flywheelMotor);

        flywheel = new Flywheel(hardwareMap, telemetry, runtime, savedP, savedI, savedD);
    }

    @After
    public void tearDown() {
        Flywheel.p = savedP;
        Flywheel.i = savedI;
        Flywheel.d = savedD;
        Flywheel.f = savedF;
    }

    private void runLoopWithMeasuredVelocity(double velocity) {
        when(flywheelMotor.getVelocity()).thenReturn(velocity);
        flywheel.updateSpeedControl();
    }

    @Test
    public void constructorGetsFlywheelMotorByName() {
        verify(hardwareMap).get(DcMotorEx.class, "flywheel");
    }

    @Test
    public void constructorConfiguresMotorInCorrectOrder() {
        InOrder order = inOrder(flywheelMotor);
        order.verify(flywheelMotor).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        order.verify(flywheelMotor).setMotorEnable();
        order.verify(flywheelMotor).setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        order.verify(flywheelMotor).setDirection(DcMotorSimple.Direction.REVERSE);
        order.verify(flywheelMotor).setPIDFCoefficients(eq(DcMotor.RunMode.RUN_USING_ENCODER), any(PIDFCoefficients.class));
    }

    @Test
    public void constructorAppliesStaticDashboardPIDF() {
        verify(flywheelMotor).setPIDFCoefficients(eq(DcMotor.RunMode.RUN_USING_ENCODER), pidfCaptor.capture());
        PIDFCoefficients applied = pidfCaptor.getValue();

        assertEquals(Flywheel.p, applied.p, DELTA);
        assertEquals(Flywheel.i, applied.i, DELTA);
        assertEquals(Flywheel.d, applied.d, DELTA);
        assertEquals(Flywheel.f, applied.f, DELTA);
    }

    @Test
    public void constructorIgnoresPidArgumentsAndUsesStaticFields() {
        Flywheel.p = 1;
        Flywheel.i = 2;
        Flywheel.d = 3;
        Flywheel.f = 4;

        new Flywheel(hardwareMap, telemetry, runtime, 70, 65, 45);

        verify(flywheelMotor, times(2)).setPIDFCoefficients(eq(DcMotor.RunMode.RUN_USING_ENCODER), pidfCaptor.capture());
        PIDFCoefficients applied = pidfCaptor.getValue();
        assertEquals(1, applied.p, DELTA);
        assertEquals(2, applied.i, DELTA);
        assertEquals(3, applied.d, DELTA);
        assertEquals(4, applied.f, DELTA);
    }

    @Test
    public void constructorStartsWithZeroTargetAndCurrentSpeed() {
        assertEquals(0.0, flywheel.getTargetSpeed(), DELTA);
        assertEquals(0.0, flywheel.getCurrentSpeed(), DELTA);
    }

    @Test
    public void constructorDoesNotPostTelemetryOrUseRuntime() {
        verifyNoInteractions(telemetry, runtime);
    }

    @Test
    public void setTargetSpeedStoresTarget() {
        flywheel.setTargetSpeed(1200);

        assertEquals(1200, flywheel.getTargetSpeed(), DELTA);
    }

    @Test
    public void setTargetSpeedCommandsMotorVelocityImmediately() {
        flywheel.setTargetSpeed(1200);

        verify(flywheelMotor).setVelocity(1200.0);
    }

    @Test
    public void setTargetSpeedZeroCommandsStop() {
        flywheel.setTargetSpeed(1200);
        flywheel.setTargetSpeed(0);

        verify(flywheelMotor).setVelocity(0.0);
        assertEquals(0.0, flywheel.getTargetSpeed(), DELTA);
    }

    @Test
    public void setTargetSpeedDoesNotChangeMeasuredSpeed() {
        flywheel.setTargetSpeed(1200);

        assertEquals(0.0, flywheel.getCurrentSpeed(), DELTA);
    }

    @Test
    public void updateSpeedControlReadsMotorVelocityIntoCurrentSpeed() {
        runLoopWithMeasuredVelocity(1150);

        assertEquals(1150, flywheel.getCurrentSpeed(), DELTA);
    }

    @Test
    public void updateSpeedControlReappliesTargetVelocity() {
        flywheel.setTargetSpeed(1200);
        runLoopWithMeasuredVelocity(1150);

        verify(flywheelMotor, times(2)).setVelocity(1200.0);
    }

    @Test
    public void updateSpeedControlAppliesLatestDashboardPIDF() {
        Flywheel.p = 80;
        Flywheel.i = 1;
        Flywheel.d = 25;
        Flywheel.f = 14;

        runLoopWithMeasuredVelocity(0);

        verify(flywheelMotor, times(2)).setPIDFCoefficients(eq(DcMotor.RunMode.RUN_USING_ENCODER), pidfCaptor.capture());
        PIDFCoefficients applied = pidfCaptor.getValue();
        assertEquals(80, applied.p, DELTA);
        assertEquals(1, applied.i, DELTA);
        assertEquals(25, applied.d, DELTA);
        assertEquals(14, applied.f, DELTA);
    }

    @Test
    public void updateSpeedControlPostsVelocityTelemetry() {
        runLoopWithMeasuredVelocity(1150);

        verify(telemetry).addData("FLYWHEELvelocity", 1150.0);
    }

    @Test
    public void isAtSpeedBelowTargetWithinThresholdIsTrue() {
        flywheel.setAccuracyThreshold(50);
        flywheel.setTargetSpeed(1200);
        runLoopWithMeasuredVelocity(1180);

        assertTrue(flywheel.isAtSpeed());
    }

    @Test
    public void isAtSpeedAboveTargetWithinThresholdIsTrue() {
        flywheel.setAccuracyThreshold(50);
        flywheel.setTargetSpeed(1200);
        runLoopWithMeasuredVelocity(1220);

        assertTrue(flywheel.isAtSpeed());
    }

    @Test
    public void isAtSpeedOutsideThresholdIsFalse() {
        flywheel.setAccuracyThreshold(50);
        flywheel.setTargetSpeed(1200);
        runLoopWithMeasuredVelocity(1100);

        assertFalse(flywheel.isAtSpeed());
    }

    @Test
    public void isAtSpeedExactlyOnThresholdIsFalse() {
        flywheel.setAccuracyThreshold(50);
        flywheel.setTargetSpeed(1200);
        runLoopWithMeasuredVelocity(1150);

        assertFalse(flywheel.isAtSpeed());
    }

    @Test
    public void isAtSpeedThresholdNeverSetIsAlwaysFalse() {
        flywheel.setTargetSpeed(1200);
        runLoopWithMeasuredVelocity(1200);

        assertFalse(flywheel.isAtSpeed());
    }

    @Test
    public void isAtSpeedUsesLastMeasuredSpeedRequiresUpdateSpeedControl() {
        flywheel.setAccuracyThreshold(50);
        flywheel.setTargetSpeed(1200);

        assertFalse(flywheel.isAtSpeed());
    }

    @Test
    public void updateCurrentlyDoesNothing() {
        clearInvocations(flywheelMotor, telemetry);

        flywheel.update();

        verifyNoInteractions(flywheelMotor, telemetry);
    }

    @Test
    public void setDisabledCurrentlyDoesNotStopMotor() {
        flywheel.setTargetSpeed(1200);
        clearInvocations(flywheelMotor);

        flywheel.setDisabled(true);

        verifyNoInteractions(flywheelMotor);
    }
}
