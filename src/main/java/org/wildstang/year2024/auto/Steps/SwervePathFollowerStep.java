package org.wildstang.year2024.auto.Steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.shooter.shooter;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.year2024.subsystems.targeting.WsVision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.choreo.lib.*;
import com.google.gson.Gson;

public class SwervePathFollowerStep extends AutoStep {

    private static final double mToIn = 39.3701;
    private WsVision vision;
    private SwerveDrive drive;
    private ChoreoTrajectory pathtraj;
    private boolean isBlue;
    private boolean intakeObject;
    private enum State {PATH, OBJECT_ALIGN, FINAL_CORRECTION}
    private State state = State.PATH;

    // x and y field relative
    private double prevVelocityX, prevVelocityY, prevTime;
    private Pose2d localAutoPose, localRobotPose;

    private Timer timer;

    /** Sets the robot to track a new path
     * finishes after all values have been read to robot
     * @param pathData name of a choreo traj file in /deploy/choreo
     * @param isBlue whether the robot is on the blue alliance
     * @param intakeObject
     */
    public SwervePathFollowerStep(String pathData, boolean isBlue, boolean intakeObject) {
        
        this.intakeObject = intakeObject;
        this.pathtraj = getTraj(pathData);
        this.isBlue = isBlue;
        timer = new Timer();
    }

    @Override
    public void initialize() {
        vision = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);
        drive = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        //start path
        drive.setToAuto();
        timer.start();
        prevTime = 0.0;
        prevVelocityX = 0.0;
        prevVelocityY = 0.0;
    }

    @Override
    public void update() {
        // Choreo and odometry works in field relative
        ChoreoTrajectoryState sample = pathtraj.sample(timer.get());
        // Change into inches/second
        double velocityX = pathtraj.sample(timer.get()).velocityX * mToIn;
        double velocityY = pathtraj.sample(timer.get()).velocityY * mToIn;
        localRobotPose = drive.returnPose().times(mToIn);
        localAutoPose = sample.getPose().times(mToIn);
        double yOffset = localAutoPose.getY() - localRobotPose.getY();
        double xOffset = localAutoPose.getX() - localRobotPose.getX();

        switch (state) {
            case PATH:
            if (intakeObject && vision.back.TargetInView() && (vision.back.getNoteDistance() < 40)) {
                drive.setToObject();
                state = State.OBJECT_ALIGN;
            } else if (timer.get() >= pathtraj.getTotalTime()) {
                state = State.FINAL_CORRECTION;
            } else {

                // Update values the robot is tracking to
                // Set in alliance relative
                if (isBlue) {
                    drive.setAutoValues(-velocityY, velocityX, -yOffset, xOffset);
                } else {
                    drive.setAutoValues(velocityY, -velocityX, yOffset, -xOffset);
                }

                drive.setAutoHeading(getRotation());
                
                SmartDashboard.putNumber("PF local X", localRobotPose.getX());
                SmartDashboard.putNumber("PF path X", localAutoPose.getX());
            }
                break;
            case FINAL_CORRECTION:
                // Are within 4 inches of final target
                if (Math.abs(xOffset) < 4 && Math.abs(yOffset) < 4) {
                    this.setFinished();
                    return;
                }
                if (isBlue) {
                    drive.setAutoValues(0, 0, -yOffset, xOffset);
                } else {
                    drive.setAutoValues(0, 0, yOffset, -xOffset);
                }   

                break;
            case OBJECT_ALIGN:
                if (!vision.back.TargetInView()) {
                    state = State.FINAL_CORRECTION;
                }
                break;
        }
        prevVelocityX = velocityX;
        prevVelocityY = velocityY;
        prevTime = timer.get();

        
    }

    @Override
    public String toString() {
        return "Swerve Path Follower";
    }

    public double getVelocity(){
        return mToIn * Math.hypot(pathtraj.sample(timer.get()).velocityX, pathtraj.sample(timer.get()).velocityY);
    }
    public double getHeading(){
        if (isBlue) return ((-Math.atan2(pathtraj.sample(timer.get()).velocityY, 
            pathtraj.sample(timer.get()).velocityX)*180/Math.PI)+360)%360;
        else return ((-Math.atan2(-pathtraj.sample(timer.get()).velocityY, 
            pathtraj.sample(timer.get()).velocityX)*180/Math.PI)+360)%360;
        // if (isBlue) return ((-pathtraj.sample(timer.get()).heading*180/Math.PI)+360)%360; 
        // else return ((pathtraj.sample(timer.get()).heading*180/Math.PI)+360)%360;
    }
    public double getRotation(){
        if (isBlue) return ((-pathtraj.sample(timer.get()).heading*180/Math.PI)+360)%360;
        // I think this needs to be flipped 180 for Red alliance
        else return ((-pathtraj.sample(timer.get()).heading*180/Math.PI)+180)%360;
    }
    public ChoreoTrajectory getTraj(String fileName){
        Gson gson = new Gson();
        var tempfile = Filesystem.getDeployDirectory();
        var traj_dir = new File(tempfile, "choreo");

        var traj_file = new File(traj_dir, fileName + ".traj");
        try {
      var reader = new BufferedReader(new FileReader(traj_file));
    //var reader = (new FileReader(traj_file));
      return  gson.fromJson(reader, ChoreoTrajectory.class);
    //   return traj;
    } catch (Exception ex) {
      DriverStation.reportError("Shit is fucked", ex.getStackTrace());
    }return new ChoreoTrajectory();
    }
}
