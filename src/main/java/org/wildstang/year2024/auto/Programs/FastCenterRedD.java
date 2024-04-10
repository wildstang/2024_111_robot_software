package org.wildstang.year2024.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.auto.Steps.AlignOnStep;
import org.wildstang.year2024.auto.Steps.ObjectOnStep;
import org.wildstang.year2024.auto.Steps.SetFlywheel;
import org.wildstang.year2024.auto.Steps.SetIntakeSequenceStep;
import org.wildstang.year2024.auto.Steps.ShootSpeakerStep;
import org.wildstang.year2024.auto.Steps.ShooterAutoAim;
import org.wildstang.year2024.auto.Steps.ShooterSetAngle;
import org.wildstang.year2024.auto.Steps.StartOdometryStep;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.LED.LedController;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;

public class FastCenterRedD extends AutoProgram{

    private boolean isBlue = false;

    @Override
    protected void defineSteps() {
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        LedController led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
        led.setAlliance(isBlue);
        swerve.setAlliance(isBlue);
        AutoSerialStepGroup startGroup = new AutoSerialStepGroup();
        startGroup.addStep(new SetGyroStep(180.0, swerve));
        startGroup.addStep(new StartOdometryStep(1.3, 5.5, 180.0, isBlue));
        startGroup.addStep(new ShooterSetAngle(175));
        startGroup.addStep(new SetFlywheel(true));
        startGroup.addStep(new AutoStepDelay(300));
        addStep(startGroup);

        //shoot two, and grab from center
        addStep(new ShootSpeakerStep());
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new SwervePathFollowerStep("CenterDA", swerve, isBlue));
        AutoSerialStepGroup group1a = new AutoSerialStepGroup();
        group1a.addStep(new AutoStepDelay(400));
        group1a.addStep(new ShooterSetAngle(95));
        group1a.addStep(new AutoStepDelay(800));
        group1a.addStep(new ShooterSetAngle(0));
        group1a.addStep(new SetFlywheel(false));
        group1a.addStep(new SetIntakeSequenceStep(true));
        group1a.addStep(new AutoStepDelay(800));
        group1a.addStep(new ObjectOnStep(true));
        group1.addStep(group1a);
        addStep(group1);

        //return from center and home on target
        addStep(new ObjectOnStep(false));
        addStep(new SwervePathFollowerStep("CenterDB", swerve, isBlue));
        addStep(new SetFlywheel(true));
        addStep(new ShooterSetAngle(105));
        addStep(new AlignOnStep(true));
        addStep(new ShooterAutoAim(true));
        addStep(new AutoStepDelay(2000));
        addStep(new AlignOnStep(false));

        //shoot third
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(500));

        //grab fourth
        addStep(new ShooterAutoAim(false));
        addStep(new ShooterSetAngle(103));
        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new SwervePathFollowerStep("CenterFinish", swerve, isBlue));
        AutoSerialStepGroup group2a = new AutoSerialStepGroup();
        group2a.addStep(new AutoStepDelay(500));
        group2a.addStep(new ObjectOnStep(true));
        group2.addStep(group2a);
        addStep(group2);
        addStep(new ObjectOnStep(false));
        addStep(new AutoStepDelay(500));
        addStep(new SwervePathFollowerStep("CenterFinishB", swerve, isBlue));
    }

    @Override
    public String toString() {
        return "Fast Center Red D";
    }
    
}
