package org.firstinspires.ftc.teamcode.robot.subsystems;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.Flywheel;
import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.PollenFlywheel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
@RunWith(MockitoJUnitRunner.class)
public class PollenFlywheelTest {
    private PollenFlywheel pollenFlywheel;
    @Mock private HardwareMap hardwareMap;
    @Mock private DcMotorEx motor;
    @Mock private Telemetry telemetry;
    @Mock private ElapsedTime elapsedTime;
//    private static double p = 70;
//    private static double i = 0;
//    private static double d = 30;
//    private static double f = 13.5;
    @Before
    public void setup(){
        when(hardwareMap.get(DcMotorEx.class, "pollen_flywheel")).thenReturn(motor);
        pollenFlywheel = new PollenFlywheel(hardwareMap,telemetry,elapsedTime);
    }
    @Test
    public void setDisabled_True(){
        pollenFlywheel.setTargetSpeed(1000.0);
        pollenFlywheel.setDisabled(true);
        assertTrue(pollenFlywheel.getTargetSpeed()== 0);
    }
    @Test
    public void setDisabled_False(){
        pollenFlywheel.setTargetSpeed(20);
        pollenFlywheel.setDisabled(false);
        assertEquals(20,20);
    }

}
