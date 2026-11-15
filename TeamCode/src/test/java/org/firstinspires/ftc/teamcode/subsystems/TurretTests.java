package org.firstinspires.ftc.teamcode.subsystems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.Turret;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

/** Tests turret target selection and interactions with the hardware. */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class TurretTests {
    private static final double TICKS_PER_TURRET_REVOLUTION = 4000 * 2.75;
    private static final double EPSILON = 1e-7;

    @Mock private HardwareMap hardwareMap;
    @Mock private Servo servo;
    @Mock private DcMotor encoder;
    @Mock private DigitalChannel homingSensor;
    private final double ROM = 420.0;

    private Turret turret;

    @Before
    public void setUp() {
        when(hardwareMap.get(Servo.class, "servo")).thenReturn(servo);
        when(hardwareMap.get(DcMotor.class, "encoder")).thenReturn(encoder);
        when(hardwareMap.get(DigitalChannel.class, "beam")).thenReturn(homingSensor);
        turret = new Turret(hardwareMap, "servo", "encoder", "beam",
                TICKS_PER_TURRET_REVOLUTION, 0.1, 0, 0, 2, ROM);
    }

    @Test
    public void constructorInitializesEncoderAndSensor() {
        verify(encoder).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        verify(encoder).setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        verify(homingSensor).setMode(DigitalChannel.Mode.INPUT);
        assertEquals(0, turret.getTurretAngle(), EPSILON);
    }

    @Test
    public void updateConvertsEncoderTicksToDegrees() {
        when(encoder.getCurrentPosition()).thenReturn(2750);
        turret.update();
        assertEquals(90, turret.getTurretAngle(), EPSILON);
    }

    @Test
    public void targetAtHalfTurnDoesNotBecomeZero() {
        turret.setTurretAngle(180);
        assertEquals(180, Math.abs(turret.getTarget()), EPSILON);
    }

    @Test
    public void selectsNearestEquivalentTargetOnNegativeSide() {
        updateAtAngle(-160);
        turret.setTurretAngle(190);
        assertEquals(-170, turret.getTarget(), EPSILON);
    }

    @Test
    public void selectsNearestEquivalentTargetOnPositiveSide() {
        updateAtAngle(160);
        turret.setTurretAngle(-190);
        assertEquals(170, turret.getTarget(), EPSILON);
    }

    @Test
    public void outOfRangeTargetSelectsEquivalentWithinBounds() {
        updateAtAngle(200);
        turret.setTurretAngle(220);
        assertEquals(-140, turret.getTarget(), EPSILON);
    }

    @Test
    public void targetAtEitherLimitIsAcceptedWhenNearest() {
        updateAtAngle(200);
        turret.setTurretAngle(210);
        assertEquals(210, turret.getTarget(), EPSILON);
        updateAtAngle(-200);
        turret.setTurretAngle(-210);
        assertEquals(-210, turret.getTarget(), EPSILON);
    }

    @Test
    public void multiTurnTargetsKeepTheirDirection() {
        turret.setTurretAngle(765);
        assertEquals(45, turret.getTarget(), EPSILON);
        turret.setTurretAngle(-765);
        assertEquals(-45, turret.getTarget(), EPSILON);
    }

    @Test
    public void nonFiniteTargetsAreRejectedWithoutChangingTarget() {
        turret.setTurretAngle(45);
        for (double invalid : new double[]{Double.NaN, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY}) {
            try {
                turret.setTurretAngle(invalid);
                fail("Expected an IllegalArgumentException for " + invalid);
            } catch (IllegalArgumentException expected) {
                assertEquals(45, turret.getTarget(), EPSILON);
            }
        }
    }

    @Test
    public void unreachableTargetKeepsPreviousTargetAndLogsWarning() throws Exception {
        turret.setTurretAngle(45);
        Field range = Turret.class.getDeclaredField("turretROM");
        range.setAccessible(true);
        range.setDouble(turret, 180);

        try (MockedStatic<Log> log = Mockito.mockStatic(Log.class)) {
            turret.setTurretAngle(100);
            assertEquals(45, turret.getTarget(), EPSILON);
            log.verify(() -> Log.w("Turret",
                    "Target angle 100.0 is out of range. Returning Previous Target: 45.0"));
        }
    }

    @Test
    public void isAtAngleUsesConfiguredTolerance() {
        turret.setTurretAngle(90);
        updateAtAngle(90);
        assertTrue(turret.isAtAngle());
        updateAtAngle(80);
        assertFalse(turret.isAtAngle());
    }

    @Test
    public void disablingStopsServoAndPreventsFurtherCommands() {
        turret.setDisabled(true);
        verify(servo).setPosition(0.5);
        clearInvocations(servo);
        turret.update();
        verify(servo, never()).setPosition(org.mockito.ArgumentMatchers.anyDouble());
    }

    @Test
    public void constrainedTargetCommandsMotionTowardTheValidPosition() {
        updateAtAngle(200);
        turret.setTurretAngle(220);
        clearInvocations(servo);

        turret.update();

        ArgumentCaptor<Double> position = ArgumentCaptor.forClass(Double.class);
        verify(servo).setPosition(position.capture());
        assertTrue("The turret must move toward -140 degrees, not past the maximum",
                position.getValue() < 0.5);
    }

    @Test
    public void selectsExpectedTargetsForDifferentPositionsAndRotations() {
        double[][] cases = {
                {0, 0, 0}, {0, 45, 45}, {0, -45, -45},
                {0, 359, -1}, {0, -359, 1}, {0, 360, 0},
                {0, -360, 0}, {0, 720, 0}, {0, -720, 0},
                {0, 765, 45}, {0, -765, -45}, {0, 540, -180},
                {0, -540, -180}, {0, 210, -150}, {0, -210, 150},
                {160, 190, 190}, {-160, 190, -170},
                {160, -190, 170}, {-160, -190, -190},
                {200, 220, -140}, {-200, -220, 140},
                {200, 210, 210}, {-200, -210, -210},
                {200, -180, 180}, {-200, 180, -180},
                {160, 550, 190}, {-160, 550, -170},
                {160, -550, 170}, {-160, -550, -190},
                {0, 1081, 1}, {0, -1081, -1},
                {0, 180.25, -179.75}, {0, -180.25, 179.75}
        };

        for (double[] testCase : cases) {
            updateAtAngle(testCase[0]);
            turret.setTurretAngle(testCase[1]);
            assertEquals("current=" + testCase[0] + ", requested=" + testCase[1],
                    testCase[2], turret.getTarget(), EPSILON);
        }
    }

    @Test
    public void sequentialTargetsFollowLatestSampleAndDisabledState() {
        updateAtAngle(-160);
        turret.setTurretAngle(190);
        assertEquals(-170, turret.getTarget(), EPSILON);

        turret.setDisabled(true);
        clearInvocations(servo);
        updateAtAngle(160);
        verify(servo, never()).setPosition(org.mockito.ArgumentMatchers.anyDouble());
        turret.setTurretAngle(-190);
        assertEquals(170, turret.getTarget(), EPSILON);

        turret.setDisabled(false);
        clearInvocations(servo);
        turret.update();
        ArgumentCaptor<Double> position = ArgumentCaptor.forClass(Double.class);
        verify(servo).setPosition(position.capture());
        assertTrue("The turret should move toward the positive target", position.getValue() > 0.5);
    }

    @Test
    public void narrowRangeRejectsUnreachableTargetsThenAcceptsValidTarget() {
        turret = new Turret(hardwareMap, "servo", "encoder", "beam",
                TICKS_PER_TURRET_REVOLUTION, 0.1, 0, 0, 2, 180);
        turret.setTurretAngle(45);

        try (MockedStatic<Log> log = Mockito.mockStatic(Log.class)) {
            for (double target : new double[]{91, -91, 180, 269.99}) {
                turret.setTurretAngle(target);
                assertEquals("unreachable target=" + target, 45, turret.getTarget(), EPSILON);
            }
            log.verify(() -> Log.w(org.mockito.ArgumentMatchers.eq("Turret"),
                    org.mockito.ArgumentMatchers.anyString()), times(4));
        }

        turret.setTurretAngle(270);
        assertEquals(-90, turret.getTarget(), EPSILON);
        turret.setTurretAngle(-270);
        assertEquals(90, turret.getTarget(), EPSILON);
    }

    @Test
    public void homingDrivesUntilSensorTripsThenStopsAndResetsEncoder() {
        turret.startHoming();
        when(homingSensor.getState()).thenReturn(false);
        clearInvocations(servo, encoder);

        turret.update();
        verify(servo).setPosition(0.6);
        verify(encoder, never()).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        when(homingSensor.getState()).thenReturn(true);
        clearInvocations(servo, encoder);
        turret.update();
        verify(servo).setPosition(0.5);
        verify(encoder).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        verify(encoder).setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        assertEquals(0, turret.getTurretAngle(), EPSILON);

        clearInvocations(servo, encoder);
        turret.update();
        verify(encoder, never()).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        verify(servo, never()).setPosition(0.6);
    }

    @Test
    public void disablingDuringHomingStopsAndCancelsHoming() {
        turret.startHoming();
        turret.update();
        verify(servo).setPosition(0.6);

        turret.setDisabled(true);
        verify(servo).setPosition(0.5);
        clearInvocations(servo, encoder);
        turret.update();
        verify(servo, never()).setPosition(org.mockito.ArgumentMatchers.anyDouble());
        verify(encoder, never()).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        turret.setDisabled(false);
        turret.update();
        verify(servo, never()).setPosition(0.6);
    }
    private void updateAtAngle(double degrees) {
        int ticks = (int) Math.round(degrees * TICKS_PER_TURRET_REVOLUTION / 360);
        when(encoder.getCurrentPosition()).thenReturn(ticks);
        turret.update();
    }
}
