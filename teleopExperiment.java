package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "BioBuzzTeleOp", group = "Linear OpMode")
public class teleopExperiment extends LinearOpMode {
    // Drive motors
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;
    // Intake
    private DcMotor intake;
    // Shooter motors
    private DcMotor pollenShooter;
    private DcMotor nectarShooter;
    // Shooter servos
    private Servo pollenServo;
    private Servo nectarServo;
    // goBILDA Pinpoint
    private GoBildaPinpointDriver pinpoint;
    // States
    private boolean pollenShooterState = false;
    private boolean nectarShooterState = false;
    // Button previous states
    private boolean prevStateY = false;
    private boolean prevStateRightTrigger = false;
    private boolean prevStateRightBumper = false;
    private boolean prevStateLeftTrigger = false;
    private boolean prevStateLeftBumper = false;

    @Override
    public void runOpMode() {

        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        intake = hardwareMap.get(DcMotor.class, "intake");

        pollenShooter = hardwareMap.get(DcMotor.class, "pollenShooter");
        nectarShooter = hardwareMap.get(DcMotor.class, "nectarShooter");

        pollenServo = hardwareMap.get(Servo.class, "pollenServo");
        nectarServo = hardwareMap.get(Servo.class, "nectarServo");

        // Pinpoint
        pinpoint = hardwareMap.get(
                GoBildaPinpointDriver.class,
                "pinpoint"
        );

        // Tell Pinpoint that you are using goBILDA Swingarm Pods
        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.GOBUILDA_SWINGARM_POD
        );

        // Change these to REVERSED if your pods report backwards
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );

        // Reset X, Y, and heading to zero
        pinpoint.resetPosAndImu();

        // Motor directions
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        
        // Initial servo positions
        pollenServo.setPosition(0.5);
        nectarServo.setPosition(0.5);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Pinpoint", "Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double x = gamepad1.right_stick_x;   // strafe
            double y = -gamepad1.right_stick_y;  // forward
            double r = gamepad1.left_stick_x;    // rotation

            move(y, x, r);

            
            // Update Pinpoint
            pinpoint.update();

            // Shooter toggle
            if (gamepad1.y && !prevStateY) {

                if (pollenShooterState) {
                    // Turn both shooters off
                    pollenShooter.setPower(0);
                    nectarShooter.setPower(0);

                    pollenShooterState = false;
                    nectarShooterState = false;

                } else {
                    // Turn both shooters on
                    pollenShooter.setPower(-1);
                    pollenShooterState = true;
                    nectarShooter.setPower(-1);
                    nectarShooterState = true;
                }
            }

            prevStateY = gamepad1.y;

            // Pollen servo
            if (gamepad1.right_trigger > 0.05
                    && !prevStateRightTrigger) {

                pollenServo.setPosition(0);
            }

            prevStateRightTrigger =
                    gamepad1.right_trigger > 0.05;

            if (gamepad1.right_bumper
                    && !prevStateRightBumper) {

                pollenServo.setPosition(0.5);
            }

            prevStateRightBumper = gamepad1.right_bumper;

            // Nectar servo
            if (gamepad1.left_trigger > 0.05
                    && !prevStateLeftTrigger) {

                nectarServo.setPosition(0);
            }

            prevStateLeftTrigger =
                    gamepad1.left_trigger > 0.05;

            if (gamepad1.left_bumper
                    && !prevStateLeftBumper) {

                nectarServo.setPosition(0.5);
            }

            prevStateLeftBumper = gamepad1.left_bumper;


            telemetry.addData("Status", "Running");

            telemetry.addData("Strafe", x);
            telemetry.addData("Forward", y);
            telemetry.addData("Rotate", r);

            // Pinpoint position
            telemetry.addData(
                    "X (mm)",
                    pinpoint.getPosition().getX(0)
            );

            telemetry.addData(
                    "Y (mm)",
                    pinpoint.getPosition().getY(0)
            );

            telemetry.addData(
                    "Heading (deg)",
                    pinpoint.getHeading()
            );

            telemetry.update();
        }
    }

    // Mecanum drive
    private void move(double y, double x, double r) {

        double leftFrontPower = y + x + r;
        double leftBackPower = y - x + r;
        double rightFrontPower = y - x - r;
        double rightBackPower = y + x - r;

        double magnitude = Math.max(
                1.0,
                Math.max(
                        Math.max(
                                Math.abs(leftFrontPower),
                                Math.abs(leftBackPower)
                        ),
                        Math.max(
                                Math.abs(rightFrontPower),
                                Math.abs(rightBackPower)
                        )
                )
        );

        leftFront.setPower(leftFrontPower / magnitude);
        leftBack.setPower(leftBackPower / magnitude);
        rightFront.setPower(rightFrontPower / magnitude);
        rightBack.setPower(rightBackPower / magnitude);
    }
}
