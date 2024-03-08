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
import org.wildstang.year2024.auto.Steps.StartOdometryStep;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.year2024.subsystems.targeting.WsVision;

public class Drake extends AutoProgram {

    private boolean isBlue = true;

    @Override
    protected void defineSteps() {
        
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new SetGyroStep(180.0, swerve));
        addStep(new StartOdometryStep(1.3, 5.5, 180.0, isBlue));
        addStep(new ShooterAutoAim(true));
        addStep(new AutoStepDelay(500));

        // grab first prestaged and shoot preload
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new SwervePathFollowerStep("DrakeA", swerve, isBlue));
        AutoSerialStepGroup group1a = new AutoSerialStepGroup();
        group1a.addStep(new ShootSpeakerStep());
        group1a.addStep(new SetIntakeSequenceStep(true));
        group1.addStep(group1a);
        addStep(group1);

        //grab second prestaged and shoot first prestaged
        AutoParallelStepGroup group2 = new AutoParallelStepGroup();
        group2.addStep(new SwervePathFollowerStep("DrakeB", swerve, isBlue));
        AutoSerialStepGroup group2a = new AutoSerialStepGroup();
        group2a.addStep(new ShootSpeakerStep());
        group2a.addStep(new SetIntakeSequenceStep(true));
        group2.addStep(group2a);
        addStep(group2);

        //grab third prestaged and shoot second prestaged
        AutoParallelStepGroup group3 = new AutoParallelStepGroup();
        group3.addStep(new SwervePathFollowerStep("DrakeC", swerve, isBlue));
        AutoSerialStepGroup group3a = new AutoSerialStepGroup();
        group3a.addStep(new ShootSpeakerStep());
        group3a.addStep(new SetIntakeSequenceStep(true));
        group3.addStep(group3a);
        addStep(group3);

        //grab middle A note and shoot third prestaged
        AutoParallelStepGroup group4 = new AutoParallelStepGroup();
        group4.addStep(new SwervePathFollowerStep("DrakeD", swerve, isBlue));
        AutoSerialStepGroup group4a = new AutoSerialStepGroup();
        group4a.addStep(new ShootSpeakerStep());
        group4a.addStep(new SetIntakeSequenceStep(true));
        group4.addStep(group4a);
        addStep(group4);

        //return from middle
        addStep(new SwervePathFollowerStep("DrakeE", swerve, isBlue));

        //grab middle B note and shoot middle A note
        AutoParallelStepGroup group5 = new AutoParallelStepGroup();
        group5.addStep(new SwervePathFollowerStep("DrakeF", swerve, isBlue));
        AutoSerialStepGroup group5a = new AutoSerialStepGroup();
        group5a.addStep(new ShootSpeakerStep());
        group5a.addStep(new SetIntakeSequenceStep(true));
        group5.addStep(group5a);
        addStep(group5);

        //return from middle and shoot middle B note
        addStep(new SwervePathFollowerStep("DrakeG", swerve, isBlue));
        addStep(new ShootSpeakerStep());
    }

    @Override
    public String toString() {
        return "The Drake";
    }
    
}
