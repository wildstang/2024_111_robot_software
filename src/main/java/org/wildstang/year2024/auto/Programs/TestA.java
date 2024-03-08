package org.wildstang.year2024.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.auto.Steps.StartOdometryStep;
import org.wildstang.year2024.auto.Steps.StuffOnStep;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;

public class TestA extends AutoProgram {

    private boolean isBlue = false;

    @Override
    protected void defineSteps() {
        
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new SetGyroStep(180.0, swerve));
        addStep(new StartOdometryStep(1.3, 5.5, 180.0, isBlue));
        addStep(new SwervePathFollowerStep("Alt end get", swerve, isBlue));
    }

    @Override
    public String toString() {
        return "Test A";
    }
    
}
