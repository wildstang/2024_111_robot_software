package main.java.org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.shooter.Shooter;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.year2024.subsystems.targeting.WsVision;

public class ShooterStep extends AutoStep {

private POSITION shooterPosition;

private Shooter shooter;

    public ShooterStep(POSITION position){
        this.shooterPosition = position; 
    }

    @Override 
    public void initialize(){
        shooter = (Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER);
    }

    @Override
    public void update() {
        shooter.autoScore();
        this.setFinished();
    }

    @Override
    public String toString() {
        return "ShooterStep";
    }


}
