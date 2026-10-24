package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.Point;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.ThreeWheelIMULocalizer;
import com.pedropathing.algorithm.Foresight;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedro.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedro.constants.LConstants;

@Autonomous(name = "Pedro Pathing Test", group = "Test")
public class pedropathingTest extends LinearOpMode {
    private Follower follower;

    @Override
    public void runOpMode() {
        // Initialize the follower using the new refactored constants
        follower = new Follower(
                new ThreeWheelIMULocalizer(hardwareMap, LConstants.localizerConfig),
                new Mecanum(hardwareMap, FConstants.drivetrainConfig),
                new Foresight(FConstants.foresightConfig)
        );

        // Define a simple path: move 24 inches forward
        Path path = new Path(new BezierLine(new Point(0, 0, Point.CARTESIAN), new Point(24, 0, Point.CARTESIAN)));
        path.setConstantHeadingInterpolation(0);

        // Set the starting pose of the robot to (0, 0, 0)
        follower.setStartingPose(new Pose(0, 0, 0));

        telemetry.addLine("Ready!");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        // Start following the path
        follower.followPath(path);

        while (opModeIsActive() && !isStopRequested()) {
            // Update the follower
            follower.update();

            // Display the robot's current position via telemetry
            Pose currentPose = follower.getPose();
            telemetry.addData("x", currentPose.getX());
            telemetry.addData("y", currentPose.getY());
            telemetry.addData("heading", Math.toDegrees(currentPose.getHeading()));
            telemetry.addData("isBusy", follower.isBusy());
            telemetry.update();
        }
    }
}
