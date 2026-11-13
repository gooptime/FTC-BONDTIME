package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Figure 8")
public class figure_8 extends LinearOpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private CRServo leftIntake;
    private CRServo rightIntake;
    private DcMotor Shooter;
    private Servo lshServo;
    private Servo rshServo;
    private DcMotor feedServo;

    @Override
    public void runOpMode() {
        // Motor Hardware Mapping
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // Servo Hardware Mapping
        leftIntake = hardwareMap.get(CRServo.class, "leftIntake");
        rightIntake = hardwareMap.get(CRServo.class, "rightIntake");
        Shooter = hardwareMap.get(DcMotor.class, "Shooter");
        lshServo = hardwareMap.get(Servo.class, "lshServo");
        rshServo = hardwareMap.get(Servo.class, "rshServo");
        feedServo = hardwareMap.get(DcMotor.class, "feedServo");

        // Reverse left motors so positive power moves forward on both sides
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        // Set motors to brake when zero power is applied
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Telemetry Init
        telemetry.addLine("Robot Initialized");
        telemetry.addLine("Press START to begin");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            boolean ninetyr = gamepad1.right_bumper;
            double oneightyr = gamepad1.right_trigger;
            boolean ninetyl = gamepad1.left_bumper;
            double oneightyl = gamepad1.left_trigger;
            boolean figure8 = gamepad1.a;

            if (figure8) {
                // Execute Full Figure 8 Routine
                drivetrain(1, 1, 1000);
                drivetrain(-0.2, 1, 2700);
                drivetrain(1, 1, 1000);
                drivetrain(1, -0.2, 2700);
                drivetrain(1, 1, 1000);
                drivetrain(1, -0.2, 2700);
                drivetrain(1, -0.2, 2700);
                drivetrain(1, 1, 500);
                drivetrain(1, -0.2, 2700);
                drivetrain(1, 1, 500);
                drivetrain(-0.2, 1, 2700);
                drivetrain(1, 1, 500);
                drivetrain(-0.2, 1, 2700);
                drivetrain(1, 1, 500);
                drivetrain(-0.2, 1, 2700);
                drivetrain(-1, -1, 1000);

                // Stop drivetrain after completing sequence
                drivetrain(0, 0, 0);
            } else if (ninetyr) {
                drivetrain(1, 0.1, 2330);
                drivetrain(0, 0, 0);
            } else if (oneightyr > 0.1) {
                drivetrain(1, 0.1, 2330 * 2);
                drivetrain(0, 0, 0);
            } else if (ninetyl) {
                drivetrain(0.1, 1, 2330);
                drivetrain(0, 0, 0);
            } else if (oneightyl > 0.1) {
                drivetrain(0.1, 1, 2330 * 2);
                drivetrain(0, 0, 0);
            }
        }
    }

    /**
     * Set drivetrain motor powers for a specific duration.
     * @param leftPower Power for left side (-1.0 to 1.0)
     * @param rightPower Power for right side (-1.0 to 1.0)
     * @param durationMs Milliseconds to hold power state
     */
    private void drivetrain(double leftPower, double rightPower, long durationMs) {
        if (!opModeIsActive()) return;

        backLeft.setPower(leftPower);
        frontLeft.setPower(leftPower);
        backRight.setPower(rightPower);
        frontRight.setPower(rightPower);

        if (durationMs > 0) {
            sleep(durationMs);
        }
    }
}