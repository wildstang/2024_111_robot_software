package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;

public class ObjectOnStep extends AutoStep{

    private SwerveDrive swerve;
    private boolean isOn;

    public ObjectOnStep(boolean isOn){
        this.isOn = isOn;
    }
    @Override
    public void initialize() {
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
    }

    @Override
    public void update() {
        swerve.setAutoObject(isOn);;
        setFinished();
    }

    @Override
    public String toString() {
        return "Object On Step";
    }
    
}
