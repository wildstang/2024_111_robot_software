package org.wildstang.year2024.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.auto.Steps.SetIntakeSequenceStep;
import org.wildstang.year2024.auto.Steps.ShootSpeakerStep;
import org.wildstang.year2024.auto.Steps.ShooterSetAngle;
import org.wildstang.year2024.auto.Steps.StartOdometryStep;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.shooter.ShooterConsts;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;

import com.choreo.lib.Choreo;


public class WingAutoProgram extends AutoProgram{
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new ShooterSetAngle(ShooterConsts.SUBWOOFER_ANGLE));
        addStep(new ShootSpeakerStep());
        addStep(new SetIntakeSequenceStep(true));
        addStep(new SwervePathFollowerStep(Choreo.getTrajectory("NewPath.1"), swerve, true));
        addStep(new SwervePathFollowerStep(Choreo.getTrajectory("NewPath.2"), swerve, true));
        addStep(new ShootSpeakerStep());
    }

    public String toString(){
        return "Test Program";
    }
}
