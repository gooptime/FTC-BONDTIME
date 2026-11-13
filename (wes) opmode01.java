package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Teleop wheels only", group = "TeleOp")
public class opmode01 extends LinearOpMode {

    // Drive Motors (Cast as DcMotorEx for enhanced encoder support)
    private DcMotorEx frontLeft;
    private DcMotorEx frontRight;
    private DcMotorEx backLeft;
    private DcMotorEx backRight;

    // Mechanism Motors and Servos
    private CRServo leftIntake;
    private CRServo rightIntake;
    private DcMotor Shooter;
    private Servo lshServo;
    private Servo rshServo;
    private DcMotor feedServo;

    @Override
    public void runOpMode() {
        // Hardware Mapping
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");

        leftIntake = hardwareMap.get(CRServo.class, "leftIntake");
        rightIntake = hardwareMap.get(CRServo.class, "rightIntake");
        Shooter = hardwareMap.get(DcMotor.class, "Shooter");
        lshServo = hardwareMap.get(Servo.class, "lshServo");
        rshServo = hardwareMap.get(Servo.class, "rshServo");
        feedServo = hardwareMap.get(DcMotor.class, "feedServo");

        // 1. Motor Directions (Invert Left side so positive power moves robot forward)
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // 2. Set Zero Power Behavior (Brake)
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // 3. Encoder Configuration
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Telemetry Initialization
        telemetry.addLine("Robot Initialized with Encoders");
        telemetry.addLine("Wait for start...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // 4. Mecanum Joystick Control Inputs
            double y = -gamepad1.left_stick_y;  // Forward/Backward (inverted stick Y)
            double x = gamepad1.left_stick_x * 1.1; // Strafe Left/Right (1.1 compensates for lateral friction)
            double rx = gamepad1.right_stick_x; // Turn Left/Right

            // 5. Calculate Mecanum Motor Powers
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);
            double frontLeftPower  = (y + x + rx) / denominator;
            double backLeftPower   = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower  = (y + x - rx) / denominator;

            // Apply Powers to Drive Motors
            frontLeft.setPower(frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);

            // Mechanism Logic
            boolean feeder = gamepad1.left_bumper;
            boolean Nectar = gamepad1.a;
            boolean Pollen = gamepad1.b;

            leftIntake.setPower(1);
            rightIntake.setPower(1);
            Shooter.setPower(0.5);

            if (Nectar) {
                lshServo.setPosition(0.8);
                rshServo.setPosition(0.8);
            }
            if (Pollen) {
                lshServo.setPosition(0.2);
                rshServo.setPosition(0.2);
            }
            if (feeder) {
                feedServo.setPower(1);
                sleep(500);
                feedServo.setPower(0);
            }

            // 6. Telemetry Feedback for Encoders
            telemetry.addData("FL Encoder Ticks", frontLeft.getCurrentPosition());
            telemetry.addData("BL Encoder Ticks", backLeft.getCurrentPosition());
            telemetry.addData("FR Encoder Ticks", frontRight.getCurrentPosition());
            telemetry.addData("RB Encoder Ticks", backRight.getCurrentPosition());
            telemetry.update();
        }
    }
}
