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

public class RushBlue extends AutoProgram {

    private boolean isBlue = true;
    private AutoStep[] steps;

    @Override
    protected void defineSteps() {
        
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        LedController led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
        isBlue = true;
        led.setAlliance(isBlue);
        swerve.setAlliance(isBlue);
        AutoSerialStepGroup startGroup = new AutoSerialStepGroup();
        startGroup.addStep(new SetGyroStep(180.0, swerve));
        startGroup.addStep(new StartOdometryStep(1.46, 6.6, 180.0, isBlue));
        startGroup.addStep(new ShooterSetAngle(170));
        startGroup.addStep(new SetFlywheel(true));
        addStep(startGroup);
        addStep(new PathHeadingStep(isBlue ? 142.0 : 218.0, swerve));
        addStep(new AutoStepDelay(500));

        // grab first prestaged and shoot preload
        steps = new AutoStep[]{new ShootSpeakerStep(), new AutoStepDelay(500), new ShooterSetAngle(115)};
        addStep(new SplitGroup(new SwervePathFollowerStep("RushA", swerve, isBlue), steps).get());
        addStep(new AutoStepDelay(300));
        addStep(new ShooterSetAngle(106));

        //to the center for A
        steps = new AutoStep[]{new AutoStepDelay(1000), new SetIntakeSequenceStep(true), new ObjectOnStep(true)};
        addStep(new SplitGroup(new SwervePathFollowerStep("RushB", swerve, isBlue), steps).get());
        addStep(new ObjectOnStep(false));

        //return and shoot A
        steps = new AutoStep[]{new AutoStepDelay(1000), new ShooterSetAngle(82.5), new ShooterAutoAim(true)};
        addStep(new SplitGroup(new SwervePathFollowerStep("RushC", swerve, isBlue), steps).get());
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(400));

        //go to center and grab B
        steps = new AutoStep[]{new AutoStepDelay(1000), new SetIntakeSequenceStep(true), new ObjectOnStep(true)};
        addStep(new SplitGroup(new SwervePathFollowerStep("RushD", swerve, isBlue), steps).get());
        addStep(new ObjectOnStep(false));

        //return from center and shoot B
        addStep(new SwervePathFollowerStep("RushE", swerve, isBlue));
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(400));

        //go to center and grab note C
        steps = new AutoStep[]{new AutoStepDelay(1000), new SetIntakeSequenceStep(true), new ObjectOnStep(true)};
        addStep(new SplitGroup(new SwervePathFollowerStep("RushF", swerve, isBlue), steps).get());
        addStep(new ObjectOnStep(false));

        //return from center and shoot C
        addStep(new SwervePathFollowerStep("RushG", swerve, isBlue));
        addStep(new ShootSpeakerStep());
    }

    @Override
    public String toString() {
        return "Rush Blue";
    }
}