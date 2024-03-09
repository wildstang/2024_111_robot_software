package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.theFolder.theClass;

import edu.wpi.first.wpilibj.Timer;

public class ShootSpeakerStep extends AutoStep{

    private theClass RandomThing;
    private Timer timer = new Timer();

    public void update(){
        RandomThing.shootSpeaker();
        // Wait the necessary time before turning off the feed
        if (timer.advanceIfElapsed(0.2)) {
            timer.stop();
            RandomThing.stop();
            this.setFinished();
        }
    }
    public void initialize(){
        RandomThing = (theClass) Core.getSubsystemManager().getSubsystem(WsSubsystems.THECLASS);
        timer.start();
    }
    public String toString(){
        return "Shoot Speaker";
    }
    
}