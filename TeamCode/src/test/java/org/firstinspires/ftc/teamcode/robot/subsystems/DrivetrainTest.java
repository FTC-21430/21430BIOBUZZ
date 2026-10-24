package org.firstinspires.ftc.teamcode.robot.subsystems;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DrivetrainTest {

    private static final double DELTA = 1e-9;

    private Drivetrain drivetrain;

    @Before
    public void setUp() {
    }

    @Test
    public void constructor_getsAllFourMotorsByName() {
    }

    @Test
    public void constructor_resetsEncodersThenRunsWithoutEncoders() {
    }

    @Test
    public void constructor_setsZeroPowerBehaviorToBrake() {
    }

    @Test
    public void update_forwardOnly_allMotorsEqualPower() {
    }

    @Test
    public void update_strafe_appliesMecanumSigns() {
    }

    @Test
    public void update_rotate_appliesMecanumSigns() {
    }

    @Test
    public void update_combinedInputOverOne_isNormalizedToMaxOne() {
    }

    @Test
    public void update_smallInput_isNotScaledUpToFullPower() {
    }

    @Test
    public void update_allNegativeInput_isHandledCorrectly() {
    }

    @Test
    public void update_zeroInput_setsZeroPowerNotNaN() {
    }

    @Test
    public void update_fieldCentricDisabled_ignoresRobotYaw() {
    }

    @Test
    public void update_fieldCentricEnabled_rotatesVectorByRobotYaw() {
    }

    @Test
    public void setDisabled_true_setsAllMotorsToZero() {
    }

    @Test
    public void setDisabled_true_updateDoesNotDriveMotors() {
    }

    @Test
    public void setDisabled_falseAfterTrue_driveResumes() {
    }

    @Test
    public void update_telemetryDisabled_postsNothing() {
    }

    @Test
    public void update_telemetryEnabled_postsDrivetrainData() {
    }
}
