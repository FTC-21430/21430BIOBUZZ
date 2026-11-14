package org.firstinspires.ftc.teamcode.robot;

import com.broombots.sweep.Classes.Pos2D;
import com.broombots.sweep.Classes.Subsystem;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Resources.ServoPlus;
import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.NectarTurretLauncher;
import org.firstinspires.ftc.teamcode.robot.subsystems.launcher.PollenTurretLauncher;

/** Coordinates the pollen and nectar launchers and the shooting gate. */
public class ShootingControl implements Subsystem {

    /** Selects which types of balls can pass through the shooting gate. */
    public enum ShootingMode  {
        /** Allows both pollen and nectar. */
        ACTIVE_ALL,
        /** Allows pollen only. */
        ACTIVE_POLLEN,
        /** Allows nectar only. */
        ACTIVE_NECTAR,
        /** Closes the gate. */
        INACTIVE
    }
    private final PollenTurretLauncher pollenLauncher;
    private final NectarTurretLauncher nectarLauncher;
    private ShootingMode activeMode = ShootingMode.INACTIVE;

    private ServoPlus gateServo;
    //TODO: tune positions
    /** Gate servo position for both ball types. */
    public static double GATE_BOTH_POSITION = 0.0;
    /** Gate servo position when closed. */
    public static double GATE_CLOSED_POSITION = 1.0;
    /** Gate servo position for pollen. */
    public static double GATE_POLLEN_POSITION = 0.5;
    /** Gate servo position for nectar. */
    public static double GATE_NECTAR_POSITION = 0.25;
    //ROM == range of motion, MIN == minimum position of servo
    /** Gate range of motion in degrees. */
    public static double GATE_ROM = 50; // degrees
    /** Minimum gate position in degrees. */
    public static double GATE_MIN = 0.0; // degrees
    /** Gate servo range of motion in degrees. */
    public static double GATE_SERVO_ROM = 270; // degrees

    /**
     * Creates the launchers and gate servo.
     *
     * @param hardwareMap hardware devices used by the launchers and gate
     * @param telemetry telemetry supplied to the launchers
     * @param runtime timer supplied to the launchers
     */
    public ShootingControl(HardwareMap hardwareMap, Telemetry telemetry, ElapsedTime runtime) {
        pollenLauncher = new PollenTurretLauncher(hardwareMap, telemetry, runtime);
        nectarLauncher = new NectarTurretLauncher(hardwareMap, telemetry, runtime);
        gateServo = new ServoPlus(hardwareMap.get(Servo.class, "gate"), GATE_SERVO_ROM, GATE_MIN,GATE_MIN+GATE_ROM);
    }
    @Override
    public void update(){
        pollenLauncher.update();
        nectarLauncher.update();
    }
    @Override
    public void setDisabled(boolean disable) {
        pollenLauncher.setDisabled(disable);
        nectarLauncher.setDisabled(disable);
    }
    /**
     * Reserves an aiming target; aiming is not yet implemented.
     *
     * @param pos target position
     * @param height target height
     */
    public void setAimPosition(Pos2D pos, double height){

    }
    /**
     * Selects the shooting mode and sets the corresponding gate position.
     *
     * @param mode desired shooting mode
     */
    public void setShootingMode(ShootingMode mode){
        this.activeMode = mode;
        switch (mode){
            case ACTIVE_ALL:
                gateServo.setServoPos(GATE_BOTH_POSITION);
                break;
            case ACTIVE_POLLEN:
                gateServo.setServoPos(GATE_POLLEN_POSITION);
                break;
            case ACTIVE_NECTAR:
                gateServo.setServoPos(GATE_NECTAR_POSITION);
                break;
            default:
                gateServo.setServoPos(GATE_CLOSED_POSITION);
                break;
        }
    }
}
