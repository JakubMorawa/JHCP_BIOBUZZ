package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "teleopBIOBUZZ", group = "Test")
public class teleopBIOBUZZ extends LinearOpMode {

    // Define encoder motor port mapped in configuration
    TestBenchIMU bench = new TestBenchIMU();
    private DcMotorEx left_front;  // Y-axis (Forward) Odometry Pod 2
    private DcMotorEx left_back;   // Y-axis (Forward) Odometry Pod 1
    private DcMotorEx right_front; // X-axis (Strafe) Odometry Pod
    private DcMotor right_back;
    private DcMotor intakeMotor;

    // Constants for your specific odometry pod (update these!)
    final double COUNTS_PER_REV = 1900.0;
    final double WHEEL_DIAMETER_INCHES = 1.88976; 
    final double INCHES_PER_COUNT = (Math.PI * WHEEL_DIAMETER_INCHES) / COUNTS_PER_REV;

    @Override
    public void runOpMode() {
        // Initialize the hardware map
        bench.init(hardwareMap);
        left_front = hardwareMap.get(DcMotorEx.class, "left_front");
        left_back = hardwareMap.get(DcMotorEx.class, "left_back");
        right_front = hardwareMap.get(DcMotorEx.class, "right_front");
        right_back = hardwareMap.get(DcMotor.class, "right_back");

        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");

        // Reset the encoder counts to zero at startup for odometry pods
        left_front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left_front.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        left_back.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left_back.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right_front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right_front.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        left_front.setDirection(DcMotorSimple.Direction.REVERSE);
        left_back.setDirection(DcMotorSimple.Direction.REVERSE);
        right_front.setDirection(DcMotorSimple.Direction.FORWARD);
        right_back.setDirection(DcMotorSimple.Direction.FORWARD);

        teleopHeadingAndStatus(); // Wait for start
        waitForStart();

        while (opModeIsActive()) {
            double x = gamepad1.left_stick_x;   // strafe
            double y = -gamepad1.left_stick_y;  // forward
            double r = gamepad1.right_stick_x;    // rotation

            move(y, x, r);

            // Intake motor logic
            if (gamepad1.a) {
                intakeMotor.setPower(1.0);
            } else if (gamepad1.dpad_down) {
                intakeMotor.setPower(0.8);
            } else {
                intakeMotor.setPower(0.0);
            }

            // Read raw encoder ticks
            int rawTicksX = right_front.getCurrentPosition();  // right_front is strafe pod
            int rawTicksY1 = left_back.getCurrentPosition();   // left_back is forward pod 1
            int rawTicksY2 = left_front.getCurrentPosition();  // left_front is forward pod 2

            // Calculate distance traveled in inches
            double distanceInchesX = rawTicksX * INCHES_PER_COUNT;
            double distanceInchesY1 = rawTicksY1 * INCHES_PER_COUNT;
            double distanceInchesY2 = rawTicksY2 * INCHES_PER_COUNT;

            telemetry.addData("Movement-", "");
            telemetry.addData("Strafe", x);
            telemetry.addData("Forward", y);
            telemetry.addData("Rotate", r);

            // Intake status
            telemetry.addData("Intake Power", intakeMotor.getPower());

            // Display data on Driver Hub screen
            telemetry.addData("Robot Position-", "");
            telemetry.addData("Raw Ticks X (Strafe)", rawTicksX);
            telemetry.addData("Raw Ticks Y1 (Forward)", rawTicksY1);
            telemetry.addData("Raw Ticks Y2 (Forward)", rawTicksY2);
            telemetry.addData("X-axis Distance (in)", "%.2f", distanceInchesX);
            telemetry.addData("Y1-axis Distance (in)", "%.2f", distanceInchesY1);
            telemetry.addData("Y2-axis Distance (in)", "%.2f", distanceInchesY2);

            telemetry.addData("IMU", "OK");
            telemetry.addData("Heading", bench.getHeading());
            telemetry.addData("Pitch", bench.getPitch());
            telemetry.addData("Roll", bench.getRoll());

            telemetry.update();
        }
    }

    private void teleopHeadingAndStatus() {
        telemetry.addData("Status", "Initialized. Ready to start.");
        telemetry.update();
    }

    private void move(double y, double x, double r) {

        double leftFrontPower = y + x + r;
        double leftBackPower = y - x + r;
        double rightFrontPower = y - x - r;
        double rightBackPower = y + x - r;

        // Ensure the powers are scaled down proportionally if any exceed 1.0
        double magnitude = 1.0;
        magnitude = Math.max(magnitude, Math.abs(leftFrontPower));
        magnitude = Math.max(magnitude, Math.abs(leftBackPower));
        magnitude = Math.max(magnitude, Math.abs(rightFrontPower));
        magnitude = Math.max(magnitude, Math.abs(rightBackPower));

        left_front.setPower(leftFrontPower / magnitude);
        left_back.setPower(leftBackPower / magnitude);
        right_front.setPower(rightFrontPower / magnitude);
        right_back.setPower(rightBackPower / magnitude);
    }
}
