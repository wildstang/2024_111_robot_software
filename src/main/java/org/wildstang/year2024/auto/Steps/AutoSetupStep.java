package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.LED.LedController;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class AutoSetupStep extends AutoStep{

    private double x, y, heading;
    private SwerveDrive swerve;

    // Setup based on starting Choreo position
    public AutoSetupStep(double x, double y, double pathHeading, Alliance alliance){
        this.x = x;
        this.y = y;
        heading = pathHeading;
        Core.setAlliance(alliance);
        LedController led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
        led.setAlliance(alliance);
    }

    public void update(){
        // Gyro reset and reads within 1 degree of what we told it to
        if (swerve.getGyroAngle() < heading + 1 && swerve.getGyroAngle() > heading -1) {
            // Sets odometry field relative, flipping for red
            if (Core.isBlue()){
                swerve.setOdo(new Pose2d(new Translation2d(x, y), new Rotation2d(Math.toRadians(360.0-heading))));
            } else {
                swerve.setOdo(new Pose2d(new Translation2d(16.5410515-x, y), new Rotation2d(Math.toRadians(180.0-heading))));
            }
            this.setFinished();
        }
    }
    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        swerve.setAutoValues(0, 0, 0, 0);
        swerve.setAutoHeading(heading);
        swerve.setGyro(heading);
    }
    public String toString(){
        return "Auto Setup Step";
    }
    
}
