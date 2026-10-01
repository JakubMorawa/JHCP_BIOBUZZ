package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo; 

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
    //servo for shooters
    private Servo pollenServo = null;
    private Servo nectarServo = null;
    //ocometry pods
    private DcMotorEx verticalEncoder = null;
    private DcMotorEx horizontalEncoder = null;

    private boolean intakeState = false;
    private boolean pollenShooterState = false;
    private boolean nectarShooterState = false;

    private double intakePower = 0;
    private double pollenShooterPower = 0;
    private double nectarShooterPower = 0;

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

        pollenServo = hardwareMap.get(Servo.class, "pollenServo");
        nectarSerco = hardwareMap.get(Servo.class, "nectarServo");
  
        verticalEncoder = hardwareMap.get(DcMotorEx.class, "verticalEncoder");
        horizontalEncoder = hardwareMap.get(DcMotorEx.class, "horizontalEncoder");
        // Reverse one side so positive power moves the robot forward
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        public void move(x,y,z){
            double leftFrontPower = y + x + r;
            double leftBackPower = y - x + r;
            double rightFrontPower = y - x - r;
            double rightBackPower = y + x - r;
            double magnitude = Math.max(1,Math.max(
                Math.max(Math.abs(leftFrontPower), Math.abs(leftBackPower)),
                Math.max(Math.abs(rightFrontPower), Math.abs(rightBackPower))
            ));
            
            leftFront.setPower(leftFrontPower/magnitude); 
            leftBack.setPower(leftBackPower/magnitude);
            rightFront.setPower(rightFrontPower/magnitude);
            rightBack.setPower(rightBackPower/magnitude);
        }

        telemetry.addData("Status", "Initialized");
        telemetry.update();
      
        waitForStart();

        while (opModeIsActive()) {
            double x = gamepad1.right_stick_x; //strafe
            double y  = gamepad1.right_stick_y; //forward
            double r = gamepad1.left_stick_x; //rotation

            pollenServo.setPosition(0.5);
            nectarServo.setPosition(0.5);
            move(y,x,r);

            if (gamepad1.y && !prevStateY) {
              if (pollenShooterState) {
                  pollenShooterPower = 0;
                  pollenShooter.setPower(pollenShooterPower);
                  pollenShooterState = false;
                
                  nectarShooterPower = 0;
                  nectarShooter.setPower(nectarShooterPower);
                  nectarShooterState = false;
                
              }
              else {
                  pollenShooterPower = -1;
                  pollenShooter.setPower(pollenShooterPower);
                  pollenShooterState = true;

                  nectarShooterPower = -1;
                  nectarShooter.setPower(nectarShooterPower);
                  nectarShooterState = false;
              }
            }
            prevStateY = gamepad1.y;

            //this sets the servo to each position depending what the dirver wants, using the trigger and bumper
            if ((gamepad1.right_trigger>0.05) && !prevStateRightTrigger) {
              pollenServo.setPosition(0);
            }
            prevStateRightTrigger = (gamepad1.right_trigger>0.05);

            if ((gamepad1.right_bumper) && !prevStateRightBumper) {
              pollenServo.setPosition(0.5);
            }
            prevStateRightBumper = gamepad1.right_bumper;


            if ((gamepad1.left_trigger>0.05) && !prevStateLeftTrigger) {
              nectarServo.setPosition(0);
            }
            prevStateLeftTrigger = (gamepad1.left_trigger>0.05);

            if ((gamepad1.left_bumper) && !prevStateLeftBumper) {
              nectarServo.setPosition(0.5);
            }
            prevStateLeftBumper = gamepad1.left_bumper;
            

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
