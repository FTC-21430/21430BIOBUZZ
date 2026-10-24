package org.firstinspires.ftc.teamcode.robot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.broombots.sweep.Classes.Pos2D;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.actions.ExampleAction;
import org.firstinspires.ftc.teamcode.robot.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.robot.subsystems.Flywheel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BioBuzzRobotTest {

    private BioBuzzRobot robot;

    @Before
    public void setUp() {
    }

    @Test
    public void constructor_createsDrivetrain() {
    }

    @Test
    public void constructor_registersDrivetrainAsSubsystem() {
    }

    @Test
    public void initialize_disablesFieldCentricDriving() {
    }

    @Test
    public void teleopUpdate_updatesActions() {
    }

    @Test
    public void autonomousUpdate_updatesActions() {
    }

    @Test
    public void exampleAction_withArgs_returnsActionForThisRobot() {
    }

    @Test
    public void exampleAction_noArgs_usesDefaultTimeAndRange() {
    }
}
