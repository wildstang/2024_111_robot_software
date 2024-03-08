package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.Notepath.notepath;
import org.wildstang.year2024.subsystems.shooter.shooter;

public class StuffOnStep extends AutoStep {

    notepath note;
    shooter shoot;
    boolean on;

    public StuffOnStep(boolean isOn){
        on = isOn;
    }

    @Override
    public void initialize() {
        note = (notepath) Core.getSubsystemManager().getSubsystem(WsSubsystems.NOTEPATH);
        shoot = (shooter) Core.getSubsystemManager().getSubsystem(WsSubsystems.SHOOTER);
    }

    @Override
    public void update() {
        note.intakeOn(on);
        shoot.turnOn(on);
        setFinished(true);
    }

    @Override
    public String toString() {
        return "Stuff goes on";
    }
    
}
