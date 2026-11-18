package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Controller Driver Main", group = "LinearOpMode")
public class ControllerDriver extends LinearOpMode {

    // Motors
    private DcMotor leftMotor;
    private DcMotor rightMotor;
    private DcMotor launcherMotor;
    private DcMotor collectorMotor;

    // Existing motor toggle states
    private boolean launcherState = false;
    private boolean collectorState = false;

    // Previous button states for press detection
    private boolean lastAState = false;
    private boolean lastXState = false;
    private boolean lastLBState = false;
    private boolean lastRBState = false;

    // Launcher power settings
    private static final double LAUNCHER_POWER_STEP = 0.05;
    private static final double MIN_LAUNCHER_POWER = 0.0;
    private static final double MAX_LAUNCHER_POWER = 1.0;
    private double launcherPower = 0.7;

    @Override
    public void runOpMode() {

        // Hardware initialization: motors only; no camera is required.
        leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        rightMotor = hardwareMap.get(DcMotor.class, "rightMotor");
        launcherMotor = hardwareMap.get(DcMotor.class, "launcherMotor");
        collectorMotor = hardwareMap.get(DcMotor.class, "collectorMotor");

        // Motor directions
        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
        launcherMotor.setDirection(DcMotor.Direction.REVERSE);
        collectorMotor.setDirection(DcMotor.Direction.REVERSE);

        // Launcher configuration
        launcherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Start with all motors stopped
        leftMotor.setPower(0.0);
        rightMotor.setPower(0.0);
        launcherMotor.setPower(0.0);
        collectorMotor.setPower(0.0);

        telemetry.addData("Status", "Initialized - camera not required");
        telemetry.addData("Drive", "Triggers = forward/reverse; Left stick X = turn");
        telemetry.addData("A", "Toggle launcher ON/OFF");
        telemetry.addData("LB / RB", "Decrease / increase launcher power by 0.5");
        telemetry.addData("X", "Toggle collector ON/OFF");
        telemetry.addData("Launcher power setting", "%.2f", launcherPower);
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // NORMAL DRIVING (same drive calculation)
            double drive = gamepad1.left_trigger - gamepad1.right_trigger;
            double turn = gamepad1.left_stick_x;

            double leftPower = drive + turn;
            double rightPower = drive - turn;

            // Normalize motor powers
            double maxPower = Math.max(
                    Math.abs(leftPower),
                    Math.abs(rightPower)
            );

            if (maxPower > 1.0) {
                leftPower /= maxPower;
                rightPower /= maxPower;
            }

            leftMotor.setPower(leftPower);
            rightMotor.setPower(rightPower);

            // A = MANUAL LAUNCHER TOGGLE
            boolean currentA = gamepad1.a;
            if (currentA && !lastAState) {
                launcherState = !launcherState;
            }
            lastAState = currentA;

            // LB / RB = ADJUST LAUNCHER POWER once per press
            boolean currentLB = gamepad1.left_bumper;
            boolean currentRB = gamepad1.right_bumper;

            boolean lbPressed = currentLB && !lastLBState;
            boolean rbPressed = currentRB && !lastRBState;

            // If both are pressed on the same loop, do not adjust.
            if (lbPressed && !rbPressed) {
                launcherPower = Range.clip(
                        launcherPower - LAUNCHER_POWER_STEP,
                        MIN_LAUNCHER_POWER,
                        MAX_LAUNCHER_POWER
                );
            } else if (rbPressed && !lbPressed) {
                launcherPower = Range.clip(
                        launcherPower + LAUNCHER_POWER_STEP,
                        MIN_LAUNCHER_POWER,
                        MAX_LAUNCHER_POWER
                );
            }

            lastLBState = currentLB;
            lastRBState = currentRB;

            // Apply selected power when on; otherwise command zero.
            double appliedLauncherPower = launcherState ? launcherPower : 0.0;
            launcherMotor.setPower(appliedLauncherPower);

            // X = COLLECTOR TOGGLE
            boolean currentX = gamepad1.x;
            if (currentX && !lastXState) {
                collectorState = !collectorState;
            }
            lastXState = currentX;

            collectorMotor.setPower(collectorState ? 1.0 : 0.0);

            // TELEMETRY
            telemetry.addData("Drive", "%.2f", drive);
            telemetry.addData("Turn", "%.2f", turn);
            telemetry.addData("Launcher", launcherState ? "ON" : "OFF");
            telemetry.addData("Launcher power setting", "%.2f", launcherPower);
            telemetry.addData("Launcher power applied", "%.2f", launcherMotor.getPower());
            telemetry.addData("Collector", collectorState ? "ON (1.0)" : "OFF");
            telemetry.update();

            sleep(20);
        }

        // Stop all motors when the OpMode ends
        leftMotor.setPower(0.0);
        rightMotor.setPower(0.0);
        launcherMotor.setPower(0.0);
        collectorMotor.setPower(0.0);
    }
}
