package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="testshooter")
public class ShooterTest extends LinearOpMode {

    private DcMotor leftShooter;
    private Servo leftServo;
    private Servo rightServo;

    @Override
    public void runOpMode() {
        leftShooter = hardwareMap.get(DcMotor.class, "leftShooter");

        leftServo = hardwareMap.get(Servo.class, "leftServo");
        rightServo = hardwareMap.get(Servo.class, "rightServo");


        leftServo.setDirection(Servo.Direction.REVERSE);
        rightServo.setDirection(Servo.Direction.FORWARD);

        leftShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftShooter.setDirection(DcMotor.Direction.REVERSE);
        telemetry.addLine("shooter ready... ");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double shooter = gamepad1.left_trigger;
            boolean bigger = gamepad1.a;
            boolean smaller = gamepad1.b;

            leftShooter.setPower(-0.25*shooter);

            if (bigger) {
                leftServo.setPosition(0.8);
                rightServo.setPosition(0.8);
            }

            if (smaller) {
                leftServo.setPosition(0.2);
                rightServo.setPosition(0.2);
            }

            telemetry.addData("Shooter Power", shooter);
            telemetry.addData("Left Servo Pos", leftServo.getPosition());
            telemetry.addData("Right Servo Pos", rightServo.getPosition());
            telemetry.update();
        }
    }
}