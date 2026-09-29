package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Simple TeleOp", group = "Linear OpMode")
public class SimpleTankTeleOp extends LinearOpMode {
  
    //wheel motors
    private DcMotor leftFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightFront = null;
    private DcMotor rightBack = null;
    

    @Override
    public void runOpMode() {
        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront  = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        // Reverse one side so positive power moves the robot forward
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
      
        waitForStart();

        while (opModeIsActive()) {

            double x = gamepad1.right_stick_x;
            double y  = gamepad1.right_stick_y;
            double r = gamepad1.left_stick_x;

            leftFront.setPower(y + x + r);
            leftBack.setPower(y - x + r);
            rightFront.setPower(y - x - r);
            rightBack.setPower(y + x - r);

            telemetry.addData("Status", "Running");
            telemetry.addData("x: ", x);
            telemetry.addData("y: ", y);
            telemetry.addData("r: ", r);
            telemetry.update();
        }
    }
}

class OdometrySystem {
    DcMotorEx verticalEncoder, horizontalEncoder;
    double currentX = 0.0;
    double currentY = 0.0;

    public OdometrySYstem(DcMotorEx verticalEncoder, DcMotorEx horizontalEncoder){
      DcMotorEx verticalEncoder = verticalEncoder;
      DcMotorEx horizontalEncoder = horizontalEncoder;
    }
    
    // Constants for conversion (ticks to inches)
    static final double COUNTS_PER_INCH = 8192.0; // Example for specific encoder ticks

    public void updatePosition(int verticalTicks, int horizontalTicks) {
        double changeY = verticalTicks / COUNTS_PER_INCH;
        double changeX = horizontalTicks / COUNTS_PER_INCH;
        
        currentY += changeY;
        currentX += changeX;
    }
}
