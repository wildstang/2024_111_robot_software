package org.wildstang.year2024.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.auto.Steps.AutoSetupStep;
import org.wildstang.year2024.auto.Steps.ControlFlowStep;
import org.wildstang.year2024.auto.Steps.ObjectOnStep;
import org.wildstang.year2024.auto.Steps.SetFlywheel;
import org.wildstang.year2024.auto.Steps.SetIntakeSequenceStep;
import org.wildstang.year2024.auto.Steps.ShootSpeakerStep;
import org.wildstang.year2024.auto.Steps.ShooterAutoAim;
import org.wildstang.year2024.auto.Steps.ShooterSetAngle;
import org.wildstang.year2024.auto.Steps.VisionOnStep;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.LED.LedController;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.year2024.subsystems.targeting.WsVision;
import org.wildstang.year2024.subsystems.theFolder.theClass;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class OffsideRed extends AutoProgram {

    private boolean isBlue = false;

    @Override
    protected void defineSteps() {
        
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        LedController led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
        theClass intake = (theClass) Core.getSubsystemManager().getSubsystem(WsSubsystems.THECLASS);
        addStep(new AutoSetupStep(1.45, 3.8, 180.0, Alliance.Red));
        addStep(new ShooterSetAngle(175));
        addStep(new SetFlywheel(true));
        addStep(new VisionOnStep(true));
        addStep(new ShooterAutoAim(true));
        addStep(new AutoStepDelay(500));

        // shoot preload
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(300));

        // grab center E 
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new SwervePathFollowerStep("GrabEOffside", swerve, isBlue));
        AutoSerialStepGroup group1a = new AutoSerialStepGroup();
        group1a.addStep(new AutoStepDelay(300));
        group1a.addStep(new SetIntakeSequenceStep(true));
        group1a.addStep(new ShooterSetAngle(70));
        group1a.addStep(new ShooterAutoAim(false));
        group1a.addStep(new VisionOnStep(false));
        group1a.addStep(new AutoStepDelay(1300));
        group1a.addStep(new ObjectOnStep(true));
        group1.addStep(group1a);
        addStep(group1);
        addStep(new ObjectOnStep(false));

        // AutoSerialStepGroup got = new AutoSerialStepGroup();
        // AutoSerialStepGroup missed = new AutoSerialStepGroup();

        // score middle piece E
        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new SwervePathFollowerStep("ScoreEOffside", swerve, isBlue));
        AutoSerialStepGroup group2a = new AutoSerialStepGroup();
        group2a.addStep(new AutoStepDelay(1700));
        group2a.addStep(new VisionOnStep(true));
        group2a.addStep(new ShooterAutoAim(true));
        group2.addStep(group2a);
        addStep(group2);
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(500));
        addStep(new SetIntakeSequenceStep(true));
        addStep(new VisionOnStep(false));
        addStep(new ShooterAutoAim(false));

        // grab middle piece D
        AutoParallelStepGroup group3 = new AutoParallelStepGroup();
        group3.addStep(new SwervePathFollowerStep("GrabDOffside", swerve, isBlue));
        AutoSerialStepGroup group3a = new AutoSerialStepGroup();
        group3a.addStep(new AutoStepDelay(1500));
        group3a.addStep(new ObjectOnStep(true));
        group3.addStep(group3a);
        addStep(group3);
        addStep(new ObjectOnStep(false));



        // addStep(new ControlFlowStep(intake, got, missed));

        //score middle piece D
        AutoParallelStepGroup group4 = new AutoParallelStepGroup();
        group4.addStep(new SwervePathFollowerStep("ScoreDOffside", swerve, isBlue));
        AutoSerialStepGroup group4a = new AutoSerialStepGroup();
        group4a.addStep(new AutoStepDelay(1700));
        group4a.addStep(new VisionOnStep(true));
        group4a.addStep(new ShooterAutoAim(true));
        group4.addStep(group4a);
        addStep(group4);
        addStep(new AutoStepDelay(500));
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(500));
        addStep(new VisionOnStep(false));
        addStep(new SwervePathFollowerStep("EndOffside", swerve, isBlue));

    }

    @Override
    public String toString() {
        return "Offside Red";
    }
    
}
