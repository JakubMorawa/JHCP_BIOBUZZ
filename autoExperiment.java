package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Simple Auto", group = "Linear OpMode")
public class autoExperiment extends LinearOpMode {

    private static double SPEED = 0.5;
  
    //wheel motors
    private DcMotor leftFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightFront = null;
    private DcMotor rightBack = null;

    private double leftFrontPower = 0;
    private doulbe leftBackPower = 0;
    private double rihtFrontPower = 0;
    private doulbe rightBackPower = 0;
  
  
    //intake motor
    private DcMotor intake = null;
    //shooter motors
    private DcMotor pollenShooter = null;
    private DcMotor nectarShooter = null;
    //ocometry pods
    private DcMotorEx verticalEncoder = null;
    private DcMotorEx horizontalEncoder = null;

    private ElapsedTime runtime = new ElapsedTime();


    private OdometrySystem OdoSys = new OdometrySystem();

    //helper functions
    private void move(double x, double y, double r, double duration) {
        double leftFrontPower = (y - x - r) * SPEED;
        double leftBackPower = (y + x - r) * SPEED;
        double rightFrontPower = (y + x + r) * SPEED;
        double rightBackPower = (y - x + r) * SPEED;

        leftFront.setPower(leftFrontPower);
        rightFront.setPower(rightFrontPower);
        leftBack.setPower(leftBackPower);
        rightBack.setPower(rightBackPower);

        runtime.reset();
        while (opModeIsActive() && runtime.seconds() < duration) {
            // Wait for move to complete
        }

        stopDriveMotors();
    }

    
    @Override
    public void runOpMode() throws InterruptedException {
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
            
            move(0.1,0.1,0.1);

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
