package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;

@TeleOp(name="testservo")
public class CRservo extends LinearOpMode{
    private CRServo Servo1;
    @Override
    public void runOpMode() {
        Servo1 =  hardwareMap.get(CRServo.class, "Servo1");
        waitForStart();
        while (opModeIsActive()) {
            double servoPower = gamepad1.right_stick_y;
            Servo1.setPower(servoPower);
        }
    }

}
