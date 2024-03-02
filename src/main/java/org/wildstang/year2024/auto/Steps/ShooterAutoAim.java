package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.shooter.Shooter;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.year2024.subsystems.targeting.WsVision;

public class ShooterAutoAim extends AutoStep {

private Shooter shooter;
public double speed;

    public ShooterAutoAim(){

    }
    @Override 
    public void initialize(){
        shooter = (Shooter) Core.getSubsystemManager().getSubsystem(WsSubsystems.SHOOTER);
    }

    @Override
    public void update() {
        shooter.autoAim();
        this.setFinished();
    }

    @Override
    public String toString() {
        return "Shooter Auto Aim";
    }    

}
