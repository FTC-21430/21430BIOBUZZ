package org.firstinspires.ftc.teamcode.opmodes.utilities;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Utility;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@Utility
public class ServoTestUtil extends LinearOpMode {
    public static String hardwareAddress = "";
    public static double PWM = 0.5;
    @Override
    public void runOpMode() throws InterruptedException {
        Servo servo = hardwareMap.get(Servo.class, hardwareAddress);
        waitForStart();
        while (opModeIsActive()){
            servo.setPosition(PWM);
        }
    }
}
