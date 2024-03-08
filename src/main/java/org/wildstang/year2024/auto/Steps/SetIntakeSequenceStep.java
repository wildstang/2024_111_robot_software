package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.notepath.Notepath;

public class SetIntakeSequenceStep extends AutoStep{

    private boolean turnOn;
    private Notepath notepath;

    public SetIntakeSequenceStep(boolean on) {
        turnOn = on;
    }
    public void update(){
        if (turnOn) {
            notepath.startIntaking();
        } else {
            notepath.stopIntaking();
        }
        this.setFinished();
    }
    public void initialize(){
        notepath = (Notepath) Core.getSubsystemManager().getSubsystem(WsSubsystems.NOTEPATH);
    }
    public String toString(){
        return "Set Intake Sequence";
    }
    
}
