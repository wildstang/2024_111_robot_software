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

public class RushRedB extends AutoProgram {

    private boolean isBlue = false;

    @Override
    protected void defineSteps() {
        
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        LedController led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
        led.setAlliance(isBlue);
        swerve.setAlliance(isBlue);
        AutoSerialStepGroup startGroup = new AutoSerialStepGroup();
        startGroup.addStep(new SetGyroStep(180.1, swerve));
        startGroup.addStep(new StartOdometryStep(1.46, 6.6, 180.0, isBlue));
        startGroup.addStep(new ShooterSetAngle(152));
        startGroup.addStep(new SetFlywheel(true));
        addStep(startGroup);
        addStep(new VisionOnStep(true));
        addStep(new AutoStepDelay(500));
        addStep(new ShootSpeakerStep());
        addStep(new VisionOnStep(false));
        addStep(new AutoStepDelay(300));

        // grab first prestaged and shoot preload
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new SwervePathFollowerStep("RushAB", swerve, isBlue));
        AutoSerialStepGroup group1a = new AutoSerialStepGroup();
        group1a.addStep(new ShooterSetAngle(83));
        group1a.addStep(new AutoStepDelay(2300));
        group1a.addStep(new ObjectOnStep(true));
        group1a.addStep(new SetIntakeSequenceStep(true));
        group1.addStep(group1a);
        addStep(group1);
        addStep(new ObjectOnStep(false));

        //return and shoot A
        AutoParallelStepGroup group3 = new AutoParallelStepGroup();
        group3.addStep(new SwervePathFollowerStep("RushBB", swerve, isBlue));
        AutoSerialStepGroup group3a = new AutoSerialStepGroup();
        group3a.addStep(new AutoStepDelay(1000));
        group3a.addStep(new ShooterAutoAim(true));
        group3.addStep(group3a);
        addStep(group3);
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(400));

        //go to center and grab B
        AutoParallelStepGroup group4 = new AutoParallelStepGroup();
        group4.addStep(new SwervePathFollowerStep("RushCB", swerve, isBlue));
        AutoSerialStepGroup group4a = new AutoSerialStepGroup();
        group4a.addStep(new AutoStepDelay(1000));
        group4a.addStep(new SetIntakeSequenceStep(true));
        group4a.addStep(new ObjectOnStep(true));
        group4.addStep(group4a);
        addStep(group4);
        addStep(new ObjectOnStep(false));

        //return from center and shoot B
        addStep(new SwervePathFollowerStep("RushDB", swerve, isBlue));
        addStep(new AutoStepDelay(500));
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(400));
        addStep(new SetIntakeSequenceStep(true));
        addStep(new SetFlywheel(false));

        addStep(new SwervePathFollowerStep("RushE", swerve, isBlue));
    }

    @Override
    public String toString() {
        return "Rush Red B";
    }
}