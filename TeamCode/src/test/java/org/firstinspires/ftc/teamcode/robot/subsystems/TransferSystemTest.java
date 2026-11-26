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

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.subsystems.TransferSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TransferSystemTest {
    @Mock private HardwareMap mockHardwareMap;
    @Mock private DcMotorEx mockMotor;
    @Mock private Telemetry mockTelemetry;
    private TransferSystem transferSystem;
    private static final double constantMotorSpeed = 1;
    private DcMotorSimple.Direction direction = DcMotorSimple.Direction.FORWARD;

    //This is the setup for the entire test class
    @Before
    public void setup(){
        when(mockHardwareMap.get(DcMotorEx.class, "transfer_system")).thenReturn(mockMotor);
        transferSystem = new TransferSystem(mockHardwareMap,mockTelemetry);
    }
    @Test
    public void spinningDirectionForward_isDirectionForward(){
        transferSystem.spinningDirectionForward();
        transferSystem.update();
        assertFalse(transferSystem.spinningDirectionForward());
    }
}
