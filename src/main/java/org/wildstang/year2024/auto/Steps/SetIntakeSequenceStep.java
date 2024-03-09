package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.theFolder.theClass;

public class SetIntakeSequenceStep extends AutoStep{

    private boolean turnOn;
    private theClass RandomThing;

    public SetIntakeSequenceStep(boolean on) {
        turnOn = on;
    }
    public void update(){
        if (turnOn) {
            RandomThing.startIntaking();
        } else {
            RandomThing.stopIntaking();
        }
        this.setFinished();
    }
    public void initialize(){
        RandomThing = (theClass) Core.getSubsystemManager().getSubsystem(WsSubsystems.THECLASS);
    }
    public String toString(){
        return "Set Intake Sequence";
    }
    
}
