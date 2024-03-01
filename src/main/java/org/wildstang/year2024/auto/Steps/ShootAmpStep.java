package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.notepath.Notepath;
import org.wildstang.year2024.subsystems.notepath.NotepathConsts;

import edu.wpi.first.wpilibj.Timer;

public class ShootAmpStep extends AutoStep{

    private Notepath notepath;
    private Timer timer;

    public void update(){
        notepath.shootAmp();
        // Wait the necessary time before turning off the feed
        if (timer.advanceIfElapsed(NotepathConsts.TIME_SHOOT_AMP)) {
            timer.stop();
            notepath.stop();
            this.setFinished();
        }
    }
    public void initialize(){
        notepath = (Notepath) Core.getSubsystemManager().getSubsystem(WsSubsystems.NOTEPATH);
    }
    public String toString(){
        return "Tag Align On";
    }
    
}