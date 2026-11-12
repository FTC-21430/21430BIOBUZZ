package org.firstinspires.ftc.teamcode.robot.subsystems;

import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.Flywheel;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
@RunWith(MockitoJUnitRunner.class)
public class FlywheelTest {
    private Flywheel flywheel;
    @Mock
    private HardwareMap hardwareMap;
    @Mock
    private DcMotor motor;
    @Mock
    private Telemetry telemetry;
    @Mock
    private ElapsedTime elapsedTime;
    private static double p = 70;
    private static double i = 0;
    private static double d = 30;
    private static double f = 13.5;
    @BeforeClass
    public void setup(){
        flywheel = new Flywheel(hardwareMap,telemetry,p,i,d,f);
    }

}
