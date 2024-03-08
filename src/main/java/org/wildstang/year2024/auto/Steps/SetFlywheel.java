package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.shooter.shooter;

public class SetFlywheel extends AutoStep {

    private shooter shooter;
    private boolean on;

    public SetFlywheel(boolean on){
        this.on = on;
    }
    @Override 
    public void initialize(){
        shooter = (shooter) Core.getSubsystemManager().getSubsystem(WsSubsystems.SHOOTER);
    }

    @Override
    public void update() {
        shooter.setFlywheel(on);
        this.setFinished();
    }

    @Override
    public String toString() {
        return "Flywheel On";
    }    

}