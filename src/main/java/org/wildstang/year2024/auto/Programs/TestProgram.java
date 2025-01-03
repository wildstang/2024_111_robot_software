package org.wildstang.year2024.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.AutoSerialStepGroup;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;
import org.wildstang.framework.core.Core;
import org.wildstang.year2024.auto.Steps.AutoSetupStep;
import org.wildstang.year2024.auto.Steps.ObjectOnStep;
import org.wildstang.year2024.auto.Steps.SetIntakeSequenceStep;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;

import com.choreo.lib.*;

import edu.wpi.first.wpilibj.DriverStation.Alliance;


public class TestProgram extends AutoProgram{
    
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new AutoSetupStep(0.7, 4.0, 180.0, Alliance.Blue));

        //addStep(new SetIntakeSequenceStep(true));
        // AutoParallelStepGroup group = new AutoParallelStepGroup();
        // group.addStep(new SwervePathFollowerStep("TestSlow", swerve, true));
        // AutoSerialStepGroup groupa = new AutoSerialStepGroup();
        // groupa.addStep(new AutoStepDelay(800));
        // groupa.addStep(new ObjectOnStep(true));
        // group.addStep(groupa);
        // addStep(group);
        // addStep(new StartOdometryStep(1.0, 5.0, 0.0, true));
        addStep(new SwervePathFollowerStep("TestSlow", swerve));
        
 
    }

    public String toString(){
        return "Test Program";
    }
}
