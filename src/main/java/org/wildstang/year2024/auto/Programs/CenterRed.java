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

public class CenterRed extends AutoProgram {

    private boolean isBlue = true;

    @Override
    protected void defineSteps() {
        
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        LedController led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
        isBlue = false;
        led.setAlliance(isBlue);
        swerve.setAlliance(isBlue);
        AutoSerialStepGroup startGroup = new AutoSerialStepGroup();
        startGroup.addStep(new SetGyroStep(180.0, swerve));
        startGroup.addStep(new StartOdometryStep(1.3, 5.5, 180.0, isBlue));
        startGroup.addStep(new ShooterSetAngle(175));
        startGroup.addStep(new SetFlywheel(true));
        startGroup.addStep(new AutoStepDelay(300));
        addStep(startGroup);

        // grab first prestaged and shoot preload
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new SwervePathFollowerStep("CenterAred", swerve, isBlue));
        AutoSerialStepGroup group1a = new AutoSerialStepGroup();
        group1a.addStep(new ShootSpeakerStep());
        group1a.addStep(new AutoStepDelay(500));
        group1a.addStep(new ShooterSetAngle(115));
        group1.addStep(group1a);
        addStep(group1);
        addStep(new AutoStepDelay(300));
        addStep(new ShooterSetAngle(99));
        addStep(new ShooterAutoAim(true));

        //grab second prestaged and shoot first prestaged
        addStep(new SwervePathFollowerStep("CenterBred", swerve, isBlue));
        addStep(new AutoStepDelay(500));

        //grab third prestaged and shoot second prestaged
        addStep(new SwervePathFollowerStep("CenterCred", swerve, isBlue));
        addStep(new AutoStepDelay(500));
        addStep(new ShooterAutoAim(true));

        //grab middle A note and shoot third prestaged
        AutoParallelStepGroup group4 = new AutoParallelStepGroup();
        group4.addStep(new SwervePathFollowerStep("CenterDred", swerve, isBlue));
        AutoSerialStepGroup group4a = new AutoSerialStepGroup();
        group4a.addStep(new ShooterSetAngle(75));
        group4a.addStep(new AutoStepDelay(500));
        group4a.addStep(new SetIntakeSequenceStep(true));
        group4.addStep(group4a);
        addStep(group4);

        //return from middle
        addStep(new SwervePathFollowerStep("CenterEred", swerve, isBlue));
        addStep(new ShootSpeakerStep());
        addStep(new AutoStepDelay(300));

        //grab middle B note and shoot middle A note
        AutoParallelStepGroup group5 = new AutoParallelStepGroup();
        group5.addStep(new SwervePathFollowerStep("CenterFred", swerve, isBlue));
        AutoSerialStepGroup group5a = new AutoSerialStepGroup();
        group5a.addStep(new AutoStepDelay(500));
        group5a.addStep(new SetIntakeSequenceStep(true));
        group5.addStep(group5a);
        addStep(group5);

        //return from middle and shoot middle B note
        addStep(new SwervePathFollowerStep("CenterGred", swerve, isBlue));
        addStep(new ShootSpeakerStep());
    }

    @Override
    public String toString() {
        return "Center Red";
    }
    
}