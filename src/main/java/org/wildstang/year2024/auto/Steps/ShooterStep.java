package main.java.org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.year2024.subsystems.targeting.WsVision;

public class ShooterStep extends AutoStep {

private Shooter shooter;

    public ShooterStep(){
    }

    @Override 
    public void initialize(){
        shooter = (Shooter) Core.getSubsystemManager().getSubsystem(WSSubsystems.SHOOTER);
    }

    @Override
    public void update() {
        shooter.autoScore();
    }

    @Override
    public String toString() {
        return "ShooterStep";
    }


}
