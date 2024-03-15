package org.wildstang.year2024.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.auto.Steps.SetFlywheel;
import org.wildstang.year2024.auto.Steps.SetIntakeSequenceStep;
import org.wildstang.year2024.auto.Steps.ShootSpeakerStep;
import org.wildstang.year2024.auto.Steps.ShooterAutoAim;
import org.wildstang.year2024.auto.Steps.ShooterSetAngle;
import org.wildstang.year2024.auto.Steps.StartOdometryStep;
import org.wildstang.year2024.auto.Steps.VisionOnStep;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.LED.LedController;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.year2024.subsystems.targeting.WsVision;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class OffsideRed extends AutoProgram {

    private boolean isBlue = true;

    @Override
    protected void defineSteps() {
        
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        LedController led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
        isBlue = false;
        led.setAlliance(isBlue);
        swerve.setAlliance(isBlue);
        addStep(new SetGyroStep(isBlue ? 210.0 : 150.0, swerve));
        addStep(new StartOdometryStep(0.7, 4.0, isBlue ? 210.0 : 150.0, isBlue));
        addStep(new ShooterSetAngle(175));
        addStep(new SetFlywheel(true));
        addStep(new VisionOnStep(true));
        addStep(new AutoStepDelay(500));

        // shoot preload
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(500));

        // grab center E 
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new SwervePathFollowerStep("GrabEOffsidered", swerve, isBlue));
        AutoSerialStepGroup group1a = new AutoSerialStepGroup();
        group1a.addStep(new AutoStepDelay(300));
        group1a.addStep(new SetIntakeSequenceStep(true));
        group1a.addStep(new ShooterSetAngle(70));
        group1a.addStep(new ShooterAutoAim(false));
        group1a.addStep(new VisionOnStep(false));
        group1.addStep(group1a);
        addStep(group1);

        // score middle piece E
        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new SwervePathFollowerStep("ScoreEOffsidered", swerve, isBlue));
        AutoSerialStepGroup group2a = new AutoSerialStepGroup();
        group2a.addStep(new AutoStepDelay(1000));
        group2a.addStep(new VisionOnStep(true));
        group2.addStep(group2a);
        addStep(group2);
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(500));
        addStep(new SetIntakeSequenceStep(true));
        addStep(new VisionOnStep(false));

        // grab middle piece D
        addStep(new SwervePathFollowerStep("GrabDOffsidered", swerve, isBlue));

        //score middle piece D
        AutoParallelStepGroup group4 = new AutoParallelStepGroup();
        group4.addStep(new SwervePathFollowerStep("ScoreDOffsidered", swerve, isBlue));
        AutoSerialStepGroup group4a = new AutoSerialStepGroup();
        group4a.addStep(new AutoStepDelay(1000));
        group4a.addStep(new VisionOnStep(true));
        group4.addStep(group4a);
        addStep(group4);
        addStep(new ShootSpeakerStep());
        // addStep(new AutoStepDelay(300));
        // addStep(new SetIntakeSequenceStep(true));
        // addStep(new VisionOnStep(false));

        // addStep(new SwervePathFollowerStep("GrabCOffside", swerve, isBlue));
        // AutoParallelStepGroup group5 = new AutoParallelStepGroup();
        // group5.addStep(new SwervePathFollowerStep("ScoreCOffside", swerve, isBlue));
        // AutoSerialStepGroup group5a = new AutoSerialStepGroup();
        // group5a.addStep(new AutoStepDelay(1000));
        // group5a.addStep(new VisionOnStep(true));
        // group5.addStep(group5a);
        // addStep(group5);
        // addStep(new ShootSpeakerStep());

    }

    @Override
    public String toString() {
        return "Offside Red";
    }
    
}