package org.wildstang.year2024.robot;

import org.wildstang.framework.core.Subsystems;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.year2024.subsystems.targeting.WsVision;
import org.wildstang.year2024.subsystems.testforlift.Test_lift;
import org.wildstang.year2024.subsystems.Arm_Test_Subsystems.ArmControl;
import org.wildstang.year2024.subsystems.Notepath.Notepath;
import org.wildstang.year2024.subsystems.LED.LedController;
import org.wildstang.year2024.subsystems.shooter.shooter;

/**
 * All subsystems are enumerated here.
 * It is used in Robot.java to initialize all subsystems.
 */
public enum WsSubsystems implements Subsystems {

    // enumerate subsystems
    WS_VISION("Ws Vision", WsVision.class),
    SWERVE_DRIVE("Swerve Drive", SwerveDrive.class),
    SHOOTER("Shooter", shooter.class),
    ARM("Arm Control", ArmControl.class),
    NOTEPATH("Notepath", Notepath.class),
    LIFT("Test_lift", Test_lift.class),
    LED("Led Controller", LedController.class)
    ;

    /**
     * Do not modify below code, provides template for enumerations.
     * We would like to have a super class for this structure, however,
     * Java does not support enums extending classes.
     */
    
    private String name;
    private Class<?> subsystemClass;

    /**
     * Initialize name and Subsystem map.
     * @param name Name, must match that in class to prevent errors.
     * @param subsystemClass Class containing Subsystem
     */
    WsSubsystems(String name, Class<?> subsystemClass) {
        this.name = name;
        this.subsystemClass = subsystemClass;
    }

    /**
     * Returns the name mapped to the subsystem.
     * @return Name mapped to the subsystem.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns subsystem's class.
     * @return Subsystem's class.
     */
    @Override
    public Class<?> getSubsystemClass() {
        return subsystemClass;
    }
}