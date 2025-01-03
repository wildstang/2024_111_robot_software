package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.shooter.shooter;

public class ShooterSetAngle extends AutoStep {

    private double angle;
    
    private shooter shooter;
    
        public ShooterSetAngle(double angle){
            this.angle = angle; 
        }
    
        @Override 
        public void initialize(){
            shooter = (shooter) Core.getSubsystemManager().getSubsystem(WsSubsystems.SHOOTER);
        }
    
        @Override
        public void update() {
            shooter.setAngle(angle);
            this.setFinished();
        }
    
        @Override
        public String toString() {
            return "Shooter Preset Angle";
        }
    
    
    }
