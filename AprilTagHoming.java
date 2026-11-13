package org.firstinspires.ftc.teamcode;

import android.util.Size;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name="AprilTag Homing Auto")
public class AprilTagHoming extends LinearOpMode {

    // Target position relative to the AprilTag
    private static final int DESIRED_TAG_ID = 31;     // Set to your target tag ID
    private static final double DESIRED_DISTANCE = 24.0; // Inches from tag to park

    // Controller Gains (Tuning coefficients)
    private static final double SPEED_GAIN  = 0.04;   // Forward/Back power gain
    private static final double STRAFE_GAIN = 0.03;   // Left/Right strafe gain
    private static final double TURN_GAIN   = 0.02;   // Rotation power gain

    private static final double MAX_AUTO_SPEED  = 0.5; // Cap max speeds for safety
    private static final double MAX_AUTO_STRAFE = 0.5;
    private static final double MAX_AUTO_TURN   = 0.3;

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() throws InterruptedException {
        // Hardware Mapping
        frontLeft  = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotor.class, "backLeft");
        backRight  = hardwareMap.get(DcMotor.class, "backRight");

        // Reverse left motors so positive power drives forward on Mecanum/Tank drive
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // AprilTag Initialization
        AprilTagLibrary.Builder libraryBuilder = new AprilTagLibrary.Builder();
        for (int id = 30; id <= 45; id++) {
            libraryBuilder.addTag(id, "Tag_" + id, 3.5, DistanceUnit.INCH);
        }
        AprilTagLibrary customLibrary = libraryBuilder.build();

        aprilTag = new AprilTagProcessor.Builder()
                .setLensIntrinsics(578.272, 578.272, 402.145, 221.50)
                .setTagLibrary(customLibrary)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .setCamera(hardwareMap.get(WebcamName.class, "Cam1"))
                .setCameraResolution(new Size(1280, 720))
                .addProcessor(aprilTag)
                .build();

        telemetry.addLine("Ready. Waiting for Start...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            boolean targetFound = false;
            AprilTagDetection targetTag = null;

            // Search for target tag in current frame
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                if (detection.ftcPose != null && detection.id == DESIRED_TAG_ID) {
                    targetFound = true;
                    targetTag = detection;
                    break;
                }
            }

            if (targetFound) {
                // Determine errors relative to target parking spot
                double rangeError  = targetTag.ftcPose.range - DESIRED_DISTANCE;
                double headingError = targetTag.ftcPose.bearing;
                double yawError     = targetTag.ftcPose.yaw;

                // Calculate control powers
                double drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                double turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                double strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);

                // Apply drive powers (configured for Mecanum drive; adjust if standard Tank)
                driveRobot(drive, strafe, turn);

                // Check if aligned within tolerance
                if (Math.abs(rangeError) < 1.0 && Math.abs(headingError) < 2.0) {
                    telemetry.addLine("PARKED IN SHOOTING POSITION!");
                }

                telemetry.addData("Target", "ID %d", targetTag.id);
                telemetry.addData("Range Error", "%.2f in", rangeError);
                telemetry.addData("Bearing Error", "%.2f deg", headingError);
            } else {
                // If tag is lost, stop motors
                driveRobot(0, 0, 0);
                telemetry.addLine("Searching for Target Tag...");
            }

            telemetry.update();
        }
    }

    /**
     * Drives a standard Mecanum drivetrain based on drive, strafe, and turn values.
     */
    private void driveRobot(double drive, double strafe, double turn) {
        double leftFrontPower  = drive + strafe + turn;
        double rightFrontPower = drive - strafe - turn;
        double leftBackPower   = drive - strafe + turn;
        double rightBackPower  = drive + strafe - turn;

        // Normalize powers so no motor exceeds 1.0
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower  /= max;
            rightFrontPower /= max;
            leftBackPower   /= max;
            rightBackPower  /= max;
        }

        frontLeft.setPower(leftFrontPower);
        frontRight.setPower(rightFrontPower);
        backLeft.setPower(leftBackPower);
        backRight.setPower(rightBackPower);
    }
}