//when working on a new tele op file, you can use this template to get started with coding


package org.firstinspires.ftc.teamcode;

//imports are needed when introducing new objects like motors and servos
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "BioBuzzTeleOp", group = "Linear OpMode")
//IMPORTANT TO HAVE THE CLASS NAME CALLED THE FILE NAME
public class teleopBarebones extends LinearOpMode {
    // Drive motors
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;
 
    //this function is called when you start the file on the driver hub
    @Override
    public void runOpMode() {

        //this sets the motors to the names in the driver hub configuration
        // IMPORTANT TO HAVE THE EXACT SAME NAME, and in the right places ofcourse
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        // Motor directions
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        //shows this text in the window to the right of the driver hub
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        //MAIN LOOP, EVERY ACTION INSIDE IS REPEATED AFTER YOU PRESS START
        while (opModeIsActive()) {
            double x = gamepad1.right_stick_x;   // strafe
            double y = -gamepad1.right_stick_y;  // forward
            double r = gamepad1.left_stick_x;    // rotation

            //move function that takes in your controller inputs
            move(y, x, r);


            //gives data of controller inputs
            telemetry.addData("Status", "Running");

            telemetry.addData("Strafe", x);
            telemetry.addData("Forward", y);
            telemetry.addData("Rotate", r);

            telemetry.update();
        }
    }

    // Mecanum drive
    private void move(double y, double x, double r) {

        double leftFrontPower = y + x + r;
        double leftBackPower = y - x + r;
        double rightFrontPower = y - x - r;
        double rightBackPower = y + x - r;

        //this will normalize our powers so that it doesn't exceed 1.0
        //you can not have over power 1.0 so we would have to divide by either 1 or the largest maginitude
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
