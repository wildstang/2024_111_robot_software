package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.LED.LedController;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.year2024.subsystems.targeting.WsVision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class StartOdometryStep extends AutoStep{

    private double x, y, heading;
    private SwerveDrive swerve;
    private WsVision vision;
    private LedController led;
    private boolean color;//true for blue, false for red

    public StartOdometryStep(double X, double Y, double pathHeading, boolean allianceColor){
        x = X;
        y = Y;
        heading = pathHeading;
        color = allianceColor;
    }
    public void update(){
        swerve.setToAuto();
        swerve.setAutoValues(0, 0, 0, 0);
        swerve.setGyro(heading);
        swerve.setAutoHeading(heading);
        if (color){
           swerve.setOdo(new Pose2d(new Translation2d(x, y), new Rotation2d(Math.toRadians(360.0-heading))));
        } else {
            swerve.setOdo(new Pose2d(new Translation2d(x, 8.016-y), new Rotation2d(Math.toRadians(360.0-heading))));
        }
        vision.setAlliance(color);
        led.setAlliance(color);
        this.setFinished();
    }
    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        vision = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);
        led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
    }
    public String toString(){
        return "Start Odometry";
    }
    
}
