package org.wildstang.year2024.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.auto.Steps.AutoSetupStep;
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

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class CenterRed extends AutoProgram {


    @Override
    protected void defineSteps() {
        
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        LedController led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
        AutoSerialStepGroup startGroup = new AutoSerialStepGroup();
        addStep(new AutoSetupStep(0.7, 4.0, 180.0, Alliance.Red));
        startGroup.addStep(new ShooterSetAngle(175));
        startGroup.addStep(new SetFlywheel(true));
        startGroup.addStep(new AutoStepDelay(300));
        addStep(startGroup);

        // grab first prestaged and shoot preload
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new SwervePathFollowerStep("CenterAred", swerve));
        AutoSerialStepGroup group1a = new AutoSerialStepGroup();
        group1a.addStep(new ShootSpeakerStep());
        group1a.addStep(new AutoStepDelay(500));
        group1a.addStep(new ShooterSetAngle(115));
        group1.addStep(group1a);
        addStep(group1);
        addStep(new AutoStepDelay(300));
        addStep(new ShooterSetAngle(103));
        //addStep(new ShooterAutoAim(true));

        //grab second prestaged and shoot first prestaged
        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new SwervePathFollowerStep("CenterBred", swerve));
        AutoSerialStepGroup group2a = new AutoSerialStepGroup();
        group2a.addStep(new AutoStepDelay(500));
        group2a.addStep(new ShooterAutoAim(true));
        group2a.addStep(new AutoStepDelay(400));
        group2a.addStep(new ObjectOnStep(true));
        // group2a.addStep(new VisionOnStep(true));
        group2.addStep(group2a);
        addStep(group2);
        addStep(new AutoStepDelay(500));
        addStep(new ObjectOnStep(false));
        // addStep(new VisionOnStep(false));

        //grab third prestaged and shoot second prestaged
        AutoParallelStepGroup group3 = new AutoParallelStepGroup();
        group3.addStep(new SwervePathFollowerStep("CenterCred", swerve));
        AutoSerialStepGroup group3a = new AutoSerialStepGroup();
        group3a.addStep(new AutoStepDelay(1500));
        group3a.addStep(new ObjectOnStep(true));
        group3a.addStep(new VisionOnStep(true));
        group3.addStep(group3a);
        addStep(group3);
        addStep(new ObjectOnStep(false));
        addStep(new AutoStepDelay(500));
        addStep(new VisionOnStep(false));
        //addStep(new ShooterAutoAim(true));

        //grab middle A note and shoot third prestaged
        AutoParallelStepGroup group4 = new AutoParallelStepGroup();
        group4.addStep(new SwervePathFollowerStep("CenterDred", swerve));
        AutoSerialStepGroup group4a = new AutoSerialStepGroup();
        group4a.addStep(new ShooterSetAngle(82.5));
        group4a.addStep(new AutoStepDelay(500));
        group4a.addStep(new ShooterAutoAim(true));
        group4a.addStep(new SetIntakeSequenceStep(true));
        group4a.addStep(new AutoStepDelay(800));
        group4a.addStep(new ObjectOnStep(true));
        group4.addStep(group4a);
        addStep(group4);

        //return from middle
        addStep(new ObjectOnStep(false));
        addStep(new VisionOnStep(true));
        addStep(new SwervePathFollowerStep("CenterEred", swerve));
        addStep(new AutoStepDelay(150));
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(250));
        addStep(new VisionOnStep(false));

        //grab middle B note and shoot middle A note
        AutoParallelStepGroup group5 = new AutoParallelStepGroup();
        group5.addStep(new SwervePathFollowerStep("CenterFred", swerve));
        AutoSerialStepGroup group5a = new AutoSerialStepGroup();
        group5a.addStep(new AutoStepDelay(500));
        group5a.addStep(new SetIntakeSequenceStep(true));
        group5a.addStep(new AutoStepDelay(600));
        group5a.addStep(new ObjectOnStep(true));
        group5.addStep(group5a);
        addStep(group5);

        //return from middle and shoot middle B note
        addStep(new ObjectOnStep(false));
        addStep(new VisionOnStep(true));
        addStep(new SwervePathFollowerStep("CenterGred", swerve));
        addStep(new AutoStepDelay(150));
        addStep(new ShootSpeakerStep());
    }

    @Override
    public String toString() {
        return "Center Red";
    }
    
}
