package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "FieldMapAuto_Basic", group = "Autonomous")
public class BasicCoordinateAuto extends LinearOpMode {

    private DcMotorEx leftDrive, rightDrive;

    // Robot Position State (inches and degrees)
    private double robotX = -60.0;
    private double robotY = -36.0;
    private double robotHeading = 0.0;

    // Hardware Constants
    private static final double TICKS_PER_INCH = 45.2; // Calibrate for your drivetrain
    private static final double DRIVE_KP = 0.03;
    private static final double TURN_KP = 0.015;

    @Override
    public void runOpMode() {
        leftDrive = hardwareMap.get(DcMotorEx.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotorEx.class, "right_drive");

        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized at (-60, -36)");
        telemetry.update();

        waitForStart();

        // Sequential Waypoint Navigation
        driveToTarget(0.0, -36.0);   // Move to center line
        driveToTarget(36.0, 36.0);   // Move to target area
        driveToTarget(60.0, 60.0);   // Move to corner park
    }

    private void driveToTarget(double targetX, double targetY) {
        while (opModeIsActive()) {
            double dx = targetX - robotX;
            double dy = targetY - robotY;
            double distanceError = Math.hypot(dx, dy);

            if (distanceError < 1.5) { // 1.5 inch tolerance threshold
                break;
            }

            double targetAngle = Math.toDegrees(Math.atan2(dy, dx));
            double angleError = targetAngle - robotHeading;

            // Normalize angle error to [-180, 180]
            while (angleError > 180) angleError -= 360;
            while (angleError < -180) angleError += 360;

            double drivePower = Math.min(0.6, distanceError * DRIVE_KP);
            double turnPower = angleError * TURN_KP;

            leftDrive.setPower(drivePower - turnPower);
            rightDrive.setPower(drivePower + turnPower);

            // Update tracked position mock (Replace with dead-reckoning encoder updates)
            robotX += (dx / distanceError) * 0.5;
            robotY += (dy / distanceError) * 0.5;

            telemetry.addData("Current Pos", "(%.1f, %.1f)", robotX, robotY);
            telemetry.addData("Distance to Target", "%.1f in", distanceError);
            telemetry.update();
        }

        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}
