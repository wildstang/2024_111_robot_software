package org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.shooter.Shooter;

public class ShooterSetAngle extends AutoStep {

    private double angle;
    
    private Shooter shooter;
    
        public ShooterSetAngle(double angle){
            this.angle = angle; 
        }
    
        @Override 
        public void initialize(){
            shooter = (Shooter) Core.getSubsystemManager().getSubsystem(WsSubsystems.SHOOTER);
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
