package main.java.org.wildstang.year2024.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.shooter.Shooter;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.year2024.subsystems.targeting.WsVision;

public class ShooterPresetAngle extends AutoStep {

    private boolean isClose;
    
    private Shooter shooter;
    
        public ShooterPresetAngle(boolean position){
            this.isClose = position; 
        }
    
        @Override 
        public void initialize(){
            shooter = (Shooter) Core.getSubsystemManager().getSubsystem(WsSubsystems.SHOOTER);
        }
    
        @Override
        public void update() {
            shooter.autoSetAngle(isClose);
            this.setFinished();
        }
    
        @Override
        public String toString() {
            return "ShooterStep";
        }
    
    
    }
