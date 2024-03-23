package org.wildstang.framework.auto.steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.choreo.lib.*;
import com.google.gson.Gson;

public class SwervePathFollowerStep extends AutoStep {

    private static final double mToIn = 39.3701;
    private SwerveDriveTemplate m_drive;
    private ChoreoTrajectory pathtraj;
    private boolean isBlue;

    // x and y field relative
    private double  prevVelocityX, prevVelocityY, prevTime;
    private Pose2d localAutoPose, localRobotPose;

    private Timer timer;

    /** Sets the robot to track a new path
     * finishes after all values have been read to robot
     * @param pathData double[][] that contains path, should be from \frc\paths
     * @param drive the swerveDrive subsystem
     * @param isBlue whether the robot is on the blue alliance
     */
    public SwervePathFollowerStep(String pathData, SwerveDriveTemplate drive, boolean isBlue) {
        
        
        this.pathtraj = getTraj(pathData);
        m_drive = drive;
        
        this.isBlue = isBlue;
        timer = new Timer();
    }

    @Override
    public void initialize() {
        //start path
        m_drive.setToAuto();
        timer.start();
        prevTime = 0.0;
        prevVelocityX = 0.0;
        prevVelocityY = 0.0;
    }

    @Override
    public void update() {
        if (timer.get() >= pathtraj.getTotalTime()) {
            //m_drive.setAutoValues(0.0, -pathtraj.getFinalPose().getRotation().getDegrees(),0.0,0.0,0.0);
            SmartDashboard.putNumber("auto final time", timer.get());
            setFinished();
        } else {

            // Choreo and odometry works in field relative
            ChoreoTrajectoryState sample = pathtraj.sample(timer.get());
            localRobotPose = m_drive.returnPose();
            localAutoPose = sample.getPose();

            // Meters
            double yOffset = localAutoPose.getY() - localRobotPose.getY();
            double xOffset = localAutoPose.getX() - localRobotPose.getX();
            //update values the robot is tracking to
            // Set in alliance relative
            if (isBlue) {
                m_drive.setAutoValues(-sample.velocityY * mToIn, sample.velocityX * mToIn, -yOffset, xOffset);
            } else {
                m_drive.setAutoValues(sample.velocityY * mToIn, -sample.velocityX * mToIn, yOffset, -xOffset);
            }

            m_drive.setAutoHeading(getRotation());
            prevVelocityX = sample.velocityX * mToIn;
            prevVelocityY = sample.velocityY * mToIn;
            prevTime = timer.get();
            SmartDashboard.putNumber("PF local X", localRobotPose.getX());
            SmartDashboard.putNumber("PF path X", localAutoPose.getX());
            }
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
