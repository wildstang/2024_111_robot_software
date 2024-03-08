package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.shooter.Shooter;

public class ShooterAutoAim extends AutoStep {

    private Shooter shooter;
    public boolean on;

    public ShooterAutoAim(boolean on){
        this.on = on;
    }
    @Override 
    public void initialize(){
        shooter = (Shooter) Core.getSubsystemManager().getSubsystem(WsSubsystems.SHOOTER);
    }

    @Override
    public void update() {
        shooter.autoAim(on);
        this.setFinished();
    }

    @Override
    public String toString() {
        return "Shooter Auto Aim";
    }    

}
