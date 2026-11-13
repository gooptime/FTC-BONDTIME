package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "Shooter", group = "Autonomous")
public class Shooter extends LinearOpMode {

    private DcMotor shooterMotor;

    @Override

    public void runOpMode() {
        shooterMotor  = hardwareMap.get(DcMotor.class, "shooterMotor");
        shooterMotor.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Ready to shoot uwu. shoutout to all the yuri");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            setDrivePower(0.7,  100000);
        }
    }

    private void setDrivePower(double shooterPower, long durationMs) {
        if (!opModeIsActive()) return;

        shooterMotor.setPower(shooterPower);

        sleep(durationMs);
    }
}