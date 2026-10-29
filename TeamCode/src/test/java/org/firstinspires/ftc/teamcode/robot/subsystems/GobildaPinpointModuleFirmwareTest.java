package org.firstinspires.ftc.teamcode.robot.subsystems;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.broombots.sweep.Classes.LocalizationPacket;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GobildaPinpointModuleFirmwareTest {

    private static final double DELTA = 1e-9;

    private GobildaPinpointModuleFirmware odometry;

    @Before
    public void setUp() {
    }

    @Test
    public void constructor_configuresOffsetsInInches() {
    }

    @Test
    public void constructor_configuresEncoderResolutionAndDirections() {
    }

    @Test
    public void constructor_resetTrue_resetsThenRecalibrates() {
    }

    @Test
    public void constructor_resetFalse_doesNotReset() {
    }

    @Test
    public void updateOdometry_callsPinpointUpdate() {
    }

    @Test
    public void updateOdometry_storesPositionAndHeading() {
    }

    @Test
    public void updateOdometry_computesVelocityFromDeltaPositionOverDeltaTime() {
    }

    @Test
    public void updateOdometry_resetsTimerEachLoop() {
    }

    @Test
    public void updateOdometry_postsVelocityTelemetry() {
    }

    @Test
    public void overridePosition_sendsPoseInInchesAndDegrees() {
    }

    @Test
    public void overridePosition_thenUpdatesOdometry() {
    }

    @Test
    public void resetIMU_keepsXYAndZeroesHeading() {
    }

    @Test
    public void resetPositionAndIMU_delegatesToPinpoint() {
    }

    @Test
    public void recalibrateIMU_delegatesToPinpoint() {
    }

    @Test
    public void getLocalizationPacket_containsCurrentPoseAndVelocity() {
    }
}
