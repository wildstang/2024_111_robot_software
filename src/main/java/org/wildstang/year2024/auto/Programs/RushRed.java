package org.wildstang.year2024.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SplitGroup;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.auto.Steps.ObjectOnStep;
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

public class RushRed extends AutoProgram {

    private boolean isBlue = false;
    // private AutoStep[] steps;

    @Override
    protected void defineSteps() {
        
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        LedController led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
        isBlue = false;
        led.setAlliance(isBlue);
        swerve.setAlliance(isBlue);
        AutoSerialStepGroup startGroup = new AutoSerialStepGroup();
        startGroup.addStep(new SetGyroStep(180.0, swerve));
        startGroup.addStep(new StartOdometryStep(1.46, 6.6, 180.0, isBlue));
        startGroup.addStep(new ShooterSetAngle(170));
        startGroup.addStep(new SetFlywheel(true));
        addStep(startGroup);
        addStep(new ShooterAutoAim(true));
        addStep(new PathHeadingStep(isBlue ? 142.0 : 218.0, swerve));
        addStep(new AutoStepDelay(200));
        addStep(new VisionOnStep(true));
        addStep(new AutoStepDelay(500));
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(500));
        addStep(new PathHeadingStep(180, swerve));
        addStep(new AutoStepDelay(200));

        // grab first prestaged and shoot preload
        //steps = new AutoStep[]{new VisionOnStep(false),new SetIntakeSequenceStep(true)};
        //addStep(new SplitGroup(new SwervePathFollowerStep("RushA", swerve, isBlue), steps).get());
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new SwervePathFollowerStep("RushA", swerve, isBlue));
        AutoSerialStepGroup group1a = new AutoSerialStepGroup();
        group1a.addStep(new VisionOnStep(false));
        group1a.addStep(new SetIntakeSequenceStep(true));
        group1.addStep(group1a);
        addStep(group1);
        addStep(new VisionOnStep(true));
        addStep(new AutoStepDelay(200));
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(500));
        // addStep(new SetFlywheel(false));
        // addStep(new AutoStepDelay(100000));
        // addStep(new ShooterSetAngle(106));
        System.out.println("Got HERE XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        //to the center for AAutoParallelStepGroup group1 = new AutoParallelStepGroup();
        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new SwervePathFollowerStep("RushB", swerve, isBlue));
        AutoSerialStepGroup group2a = new AutoSerialStepGroup();
        group2a.addStep(new AutoStepDelay(1000));
        group2a.addStep(new SetIntakeSequenceStep(true));
        group2a.addStep(new ObjectOnStep(true));
        group2.addStep(group2a);
        addStep(group2);
        // steps = new AutoStep[]{new AutoStepDelay(1000), new SetIntakeSequenceStep(true), new ObjectOnStep(true)};
        // addStep(new SplitGroup(new SwervePathFollowerStep("RushB", swerve, isBlue), steps).get());
        addStep(new ObjectOnStep(false));
        System.out.println("Got HERE XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        //return and shoot A
        AutoParallelStepGroup group3 = new AutoParallelStepGroup();
        group3.addStep(new SwervePathFollowerStep("RushC", swerve, isBlue));
        AutoSerialStepGroup group3a = new AutoSerialStepGroup();
        group3a.addStep(new AutoStepDelay(1000));
        group3a.addStep(new VisionOnStep(true));
        group3.addStep(group3a);
        addStep(group3);
        // steps = new AutoStep[]{new AutoStepDelay(1000), new VisionOnStep(true)};
        // addStep(new SplitGroup(new SwervePathFollowerStep("RushC", swerve, isBlue), steps).get());
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(400));
        System.out.println("Got HERE XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        //go to center and grab B
        AutoParallelStepGroup group4 = new AutoParallelStepGroup();
        group4.addStep(new SwervePathFollowerStep("RushD", swerve, isBlue));
        AutoSerialStepGroup group4a = new AutoSerialStepGroup();
        group4a.addStep(new AutoStepDelay(1000));
        group4a.addStep(new VisionOnStep(false));
        group4a.addStep(new SetIntakeSequenceStep(true));
        group4a.addStep(new ObjectOnStep(true));
        group4.addStep(group4a);
        addStep(group4);
        // steps = new AutoStep[]{new AutoStepDelay(1000),  new VisionOnStep(false), 
        //         new SetIntakeSequenceStep(true), new ObjectOnStep(true)};
        // addStep(new SplitGroup(new SwervePathFollowerStep("RushD", swerve, isBlue), steps).get());
        addStep(new ObjectOnStep(false));
        System.out.println("Got HERE XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        //return from center and shoot B
        addStep(new SwervePathFollowerStep("RushE", swerve, isBlue));
        addStep(new VisionOnStep(true));
        addStep(new AutoStepDelay(200));
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(400));
        addStep(new VisionOnStep(false));
        System.out.println("Got HERE XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        //go to center and grab note C
        AutoParallelStepGroup group6 = new AutoParallelStepGroup();
        group6.addStep(new SwervePathFollowerStep("RushF", swerve, isBlue));
        AutoSerialStepGroup group6a = new AutoSerialStepGroup();
        group6a.addStep(new AutoStepDelay(1000));
        group6a.addStep(new SetIntakeSequenceStep(true));
        group6a.addStep(new ObjectOnStep(true));
        group6.addStep(group6a);
        addStep(group6);
        // steps = new AutoStep[]{new AutoStepDelay(1000), new SetIntakeSequenceStep(true), new ObjectOnStep(true)};
        // addStep(new SplitGroup(new SwervePathFollowerStep("RushF", swerve, isBlue), steps).get());
        addStep(new ObjectOnStep(false));

        //return from center and shoot C
        // addStep(new SwervePathFollowerStep("RushG", swerve, isBlue));
        // addStep(new ShootSpeakerStep());
    }

    @Override
    public String toString() {
        return "Rush Red";
    }
}