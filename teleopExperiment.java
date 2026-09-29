package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Simple TeleOp", group = "Linear OpMode")
public class teleopExperiment extends LinearOpMode {
  
    //wheel motors
    private DcMotor leftFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightFront = null;
    private DcMotor rightBack = null;
    //intake motor
    private DcMotor intake = null;
    //shooter motors
    private DcMotor pollenShooter = null;
    private DcMotor nectarShooter = null;
    //ocometry pods
    private DcMotorEx verticalEncoder = null;
    private DcMotorEx horizontalEncoder = null;

    private OdometrySystem OdoSys = new OdometrySystem();
    

    @Override
    public void runOpMode() {
        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront  = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        intake = hardwareMap.get(DcMotor.class, "intake");

        pollenShooter = hardwareMap.get(DcMotor.class, "pollenShooter");
        nectarShooter = hardwareMap.get(DcMotor.class, "nectarShooter");
  
        verticalEncoder = hardwareMap.get(DcMotorEx.class, "verticalEncoder");
        horizontalEncoder = hardwareMap.get(DcMotorEx.class, "horizontalEncoder");

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
            //                    ^^
            // do you need to normalize these input?

            int verticalTicks = verticalEncoder.getCurrentPosition();
            int horizontalTicks = horizontalEncoder.getCurrentPosition();
            
            OdoSys.updatePosition(verticalTicks, horizontalTicks);

            telemetry.addData("Status", "Running");
            telemetry.addData("strafe: ", x);
            telemetry.addData("forward: ", y);
            telemetry.addData("rotate: ", r);

            telemetry.addData("Current X: ", OdoSys.currentX);
            telemetry.addData("Current Y: ", OdoSys.currentY);
          
            telemetry.update();
        }
    }
}

class OdometrySystem {
    public double currentX = 0.0;
    public double currentY = 0.0;
    
    // Constants for conversion (ticks to inches)
    static final double COUNTS_PER_INCH = 8192.0; // Example for specific encoder ticks

    public void updatePosition(int verticalTicks, int horizontalTicks) {
        double changeY = verticalTicks / COUNTS_PER_INCH;
        double changeX = horizontalTicks / COUNTS_PER_INCH;
        
        currentY += changeY;
        currentX += changeX;
    }
}
