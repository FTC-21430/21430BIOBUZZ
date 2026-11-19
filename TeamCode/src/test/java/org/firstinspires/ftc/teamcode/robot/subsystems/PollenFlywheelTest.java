package org.firstinspires.ftc.teamcode.robot.subsystems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.PollenFlywheel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PollenFlywheelTest {
    private static final double DELTA = 1e-6;
    private static final double RPM_TO_TICKS_CONVERSION = 28.0*60.0;
    private static final String HARDWARWE_NAME = "pollen_flywheel";
    private static double EXPECTED_P = 70;
    private static double EXPECTED_I = 65;
    private static double EXPECTED_D = 45;
    private static double EXPECTED_F = 0;
    @Mock private HardwareMap mockHardwareMap;
    @Mock private DcMotorEx mockMotor;
    @Mock private Telemetry mockTelemetry;
    @Mock private ElapsedTime mockElapsedTime;

    private PollenFlywheel pollenFlywheel;

    //This is the setup for the entire test class
    @Before
    public void setup(){
        //When the Hardware Map gets the pollen flywheel then it returns a motor.
        when(mockHardwareMap.get(DcMotorEx.class, "pollen_flywheel")).thenReturn(mockMotor);
        pollenFlywheel = new PollenFlywheel(mockHardwareMap,mockTelemetry,mockElapsedTime);
    }
    @Test
    //This test is meant to see if the hardware motor is initialized correctly(forward/backward)
    public void constructor_initializesMotorHardwareCorrectly(){
        //Verify in this is meant to use the mock hardware map and motor and make
        //sure flywheel call these methods with the correct number of required setups
        verify(mockHardwareMap).get(DcMotorEx.class,HARDWARWE_NAME);
        verify(mockMotor).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        verify(mockMotor).setMotorEnable();
        verify(mockMotor).setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        verify(mockMotor).setDirection(DcMotorSimple.Direction.FORWARD);
    }
    //This test is meant to check if the PID values are actually set to
    @Test
    public void constructor_setPollenPIDFCoefficients(){
        ArgumentCaptor<PIDFCoefficients> pidfCaptor = ArgumentCaptor.forClass(PIDFCoefficients.class);
        verify(mockMotor,atLeastOnce()).setPIDFCoefficients(
                eq(DcMotor.RunMode.RUN_USING_ENCODER),
                pidfCaptor.capture()
        );
        PIDFCoefficients appliedCoefficients = pidfCaptor.getValue();
        assertEquals(EXPECTED_P,appliedCoefficients.p, DELTA);
        assertEquals(EXPECTED_I,appliedCoefficients.i, DELTA);
        assertEquals(EXPECTED_D,appliedCoefficients.d, DELTA);
        assertEquals(EXPECTED_F,appliedCoefficients.f, DELTA);
    }
    @Test
    public void setTargetSpeed_convertsRpmAndUpdatesTargetSpeed(){
        double inputRpm = 100.0;
        double expectedTargetSpeed = inputRpm * RPM_TO_TICKS_CONVERSION;

        pollenFlywheel.setTargetSpeed(inputRpm);

        assertEquals(expectedTargetSpeed,pollenFlywheel.getTargetSpeed(),DELTA);
    }
    @Test
    public void setDisabled_whenFalse_preservesTargetSpeed(){
        double inputRpm = 20.0;
        double expectedTargetSpeed = inputRpm * RPM_TO_TICKS_CONVERSION;
        pollenFlywheel.setTargetSpeed(inputRpm);

        pollenFlywheel.setDisabled(false);

        assertEquals(expectedTargetSpeed,pollenFlywheel.getTargetSpeed(),DELTA);
    }
    @Test
    public void update_commandsTargetVelocityAndReadsCurrentVelocityFromMotor(){
        double inputRpm = 50.0;
        double expectedCommandedVelocity = inputRpm * RPM_TO_TICKS_CONVERSION;
        pollenFlywheel.setTargetSpeed(inputRpm);

        double simulatedEncoderVelocity = 80000.0;
        when(mockMotor.getVelocity()).thenReturn(simulatedEncoderVelocity);

        pollenFlywheel.update();

        verify(mockMotor).setVelocity(expectedCommandedVelocity);
        assertEquals(simulatedEncoderVelocity,pollenFlywheel.getCurrentSpeed(),DELTA);
    }
    @Test
    public void isAtSpeed_whenWithinAccuracyThreshold_returnsTrue(){
        pollenFlywheel.setAccuracyThreshold(50.0);
        pollenFlywheel.setTargetSpeed(10.0);
        double targetSpeed = pollenFlywheel.getTargetSpeed();

        pollenFlywheel.update();

        assertFalse(pollenFlywheel.isAtSpeed());
    }
    @Test
    public void isAtSpeed_whenOutsideAccuracyThreshold_returnsFalse(){
        pollenFlywheel.setAccuracyThreshold(50.0);
        pollenFlywheel.setTargetSpeed(10.0);//target speed = 16800
        double targetSpeed = pollenFlywheel.getTargetSpeed();

        when(mockMotor.getVelocity()).thenReturn(targetSpeed - 100.0);

        pollenFlywheel.update();

        assertFalse(pollenFlywheel.isAtSpeed());
    }
    @Test
    public void postTelemetry_sendsVelocityDataToTelemetry(){
        double simulatedVelocity = 0.0;
        when(mockMotor.getVelocity()).thenReturn(simulatedVelocity);
        pollenFlywheel.update();

        pollenFlywheel.postTelemetry();

        verify(mockTelemetry).addLine("----Flywheel Data");
        verify(mockTelemetry).addData("FLYWHELLvelocity",simulatedVelocity);
    }

}
