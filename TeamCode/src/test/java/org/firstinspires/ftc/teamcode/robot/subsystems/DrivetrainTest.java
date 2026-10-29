package org.firstinspires.ftc.teamcode.robot.subsystems;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DrivetrainTest {

    @Mock
    private HardwareMap hardwareMap;
    @Mock
    private DcMotor FLMotor;
    @Mock
    private DcMotor FRMotor;
    @Mock
    private DcMotor BLMotor;
    @Mock
    private DcMotor BRMotor;
    @Mock
    private Telemetry telemetry;
    private Drivetrain drivetrain;
    private DcMotor[] MOTORS;

    @Before
    public void setUp() {
        MOTORS = new DcMotor[]{FLMotor, FRMotor, BLMotor, BRMotor};
        when(hardwareMap.get(DcMotor.class, "FLMotor")).thenReturn(FLMotor);
        when(hardwareMap.get(DcMotor.class, "FRMotor")).thenReturn(FRMotor);
        when(hardwareMap.get(DcMotor.class, "BLMotor")).thenReturn(BLMotor);
        when(hardwareMap.get(DcMotor.class, "BRMotor")).thenReturn(BRMotor);

        drivetrain = new Drivetrain(hardwareMap, telemetry, true);
    }

    @Test
    public void constructorGetsAllFourMotorsByName() {
        verify(hardwareMap).get(DcMotor.class, "FLMotor");
        verify(hardwareMap).get(DcMotor.class, "FRMotor");
        verify(hardwareMap).get(DcMotor.class, "BLMotor");
        verify(hardwareMap).get(DcMotor.class, "BRMotor");
    }

    @Test
    public void constructorSetsZeroPowerBehaviorToBrake() {
        for (DcMotor motor : MOTORS) {
            verify(motor).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    @Test
    public void checkForwardOnlyAllMotorsEqualPower() {
        drivetrain.enableFieldCentricDriving(false);
        drivetrain.updateMovementVector(1, 0, 0);
        drivetrain.update();
        for (DcMotor motor : MOTORS) {
            verify(motor).setPower(1);
        }
        drivetrain.updateMovementVector(-1, 0, 0);
        drivetrain.update();
        for (DcMotor motor : MOTORS) {
            verify(motor).setPower(-1);
        }
    }

    @Test
    public void checkStrafeAppliesMecanumSigns() {
        drivetrain.enableFieldCentricDriving(false);
        drivetrain.updateMovementVector(0, 1, 0);
        drivetrain.update();
        for (DcMotor motor : MOTORS) {
            if (motor == FLMotor || motor == BRMotor) {
                verify(motor).setPower(1);
            } else {
                verify(motor).setPower(-1);

            }
        }
        drivetrain.updateMovementVector(0, -1, 0);
        drivetrain.update();
        for (DcMotor motor : MOTORS) {
            if (motor == FLMotor || motor == BRMotor) {
                verify(motor).setPower(-1);
            } else {
                verify(motor).setPower(1);
            }
        }
    }

    @Test
    public void checkRotateAppliesMecanumSigns() {
        drivetrain.enableFieldCentricDriving(false);
        drivetrain.updateMovementVector(0, 0, 1);
        drivetrain.update();
        for (DcMotor motor : MOTORS) {
            if (motor == FLMotor || motor == BRMotor) {
                verify(motor).setPower(1);
            } else {
                verify(motor).setPower(-1);
            }
        }
        drivetrain.updateMovementVector(0, 0, -1);
        drivetrain.update();
        for (DcMotor motor : MOTORS) {
            if (motor == FLMotor || motor == BRMotor) {
                verify(motor).setPower(-1);
            } else {
                verify(motor).setPower(1);
            }
        }
    }

    @Test
    public void checkCombinedInputNormalization() {
        drivetrain.enableFieldCentricDriving(false);
        drivetrain.updateMovementVector(1, 1, 1);
        drivetrain.update();
        verify(FLMotor).setPower(1);
        verify(FRMotor).setPower(-1 / 3.0);
        verify(BLMotor).setPower(1 / 3.0);
        verify(BRMotor).setPower(1 / 3.0);
        drivetrain.updateMovementVector(-1, -1, -1);
        drivetrain.update();
        verify(FLMotor).setPower(-1);
        verify(FRMotor).setPower(1 / 3.0);
        verify(BLMotor).setPower(-1 / 3.0);
        verify(BRMotor).setPower(-1 / 3.0);
    }

    @Test
    public void checkSmallInputScaling() {
        drivetrain.enableFieldCentricDriving(false);
        drivetrain.updateMovementVector(0.25, 0.25, 0.25);
        drivetrain.update();
        verify(FLMotor).setPower(0.75);
        verify(FRMotor).setPower(-0.25);
        verify(BLMotor).setPower(0.25);
        verify(BRMotor).setPower(0.25);
        drivetrain.updateMovementVector(-0.25, -0.25, -0.25);
        drivetrain.update();
        verify(FLMotor).setPower(-0.75);
        verify(FRMotor).setPower(0.25);
        verify(BLMotor).setPower(-0.25);
        verify(BRMotor).setPower(-0.25);
    }

    @Test
    public void checkZeroInputSetsZeroPower() {
        drivetrain.enableFieldCentricDriving(false);
        drivetrain.updateMovementVector(0, 0, 0);
        drivetrain.update();
        for (DcMotor motor : MOTORS) {
            verify(motor).setPower(0);
        }
    }

    @Test
    public void checkFieldCentricEnabledRotatesVectorByRobotYaw() {
        drivetrain.enableFieldCentricDriving(true);
        drivetrain.updateMovementVector(1, 0, 0, 45);

        drivetrain.update();

        verify(FLMotor).setPower(1);
        verify(FRMotor).setPower(0);
        verify(BLMotor).setPower(0);
        verify(BRMotor).setPower(1);
    }

    @Test
    public void checkDisabledTrueSetsAllMotorsToZero() {
        drivetrain.setDisabled(true);
        drivetrain.updateMovementVector(1, 1, 1);
        drivetrain.update();
        for (DcMotor motor : MOTORS) {
            verify(motor).setPower(0);
        }
    }

    @Test
    public void checkDisabledFalseAfterTrueDriveResumes() {

        drivetrain.setDisabled(true);
        drivetrain.updateMovementVector(1, 1, 1);
        drivetrain.update();
        for (DcMotor motor : MOTORS) {
            verify(motor).setPower(0);
        }
        drivetrain.setDisabled(false);
        drivetrain.updateMovementVector(1, 0, 0);
        drivetrain.update();
        for (DcMotor motor : MOTORS) {
            verify(motor).setPower(1);
        }
    }
}
