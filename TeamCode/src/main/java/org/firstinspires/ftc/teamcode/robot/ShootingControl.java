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

public class ShootingControl implements Subsystem {

    public enum ShootingMode  {
        ACTIVE_ALL, // shoot both pollen and nectar
        ACTIVE_POLLEN, // shoot only pollen
        ACTIVE_NECTAR, // shoot only nectar
        INACTIVE // shoot neither and hold in transfer.
    }
    private final PollenTurretLauncher pollenLauncher;
    private final NectarTurretLauncher nectarLauncher;
    private ShootingMode activeMode = ShootingMode.INACTIVE;

    private ServoPlus gateServo;
    //TODO: tune positions
    public static double GATE_BOTH_POSITION = 0.0;
    public static double GATE_CLOSED_POSITION = 1.0;
    public static double GATE_POLLEN_POSITION = 0.5;
    public static double GATE_NECTAR_POSITION = 0.25;
    //ROM == range of motion, MIN == minimum position of servo
    public static double GATE_ROM = 50; // degrees
    public static double GATE_MIN = 0.0; // degrees
    public static double GATE_SERVO_ROM = 270; // degrees

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
    public void setAimPosition(Pos2D pos, double height){

    }
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
