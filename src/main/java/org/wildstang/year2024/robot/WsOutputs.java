package org.wildstang.year2024.robot;

// expand this and edit if trouble with Ws
import org.wildstang.framework.core.Core;
import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.framework.io.outputs.Output;
import org.wildstang.hardware.roborio.outputs.config.WsMotorControllers;
import org.wildstang.hardware.roborio.outputs.config.WsSparkConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSparkFollowerConfig;

/**
 * Output mappings are stored here.
 * Below each Motor, PWM, Digital Output, Solenoid, and Relay is enumerated with their appropriated IDs.
 * The enumeration includes a name, output type, and output config object.
 */
public enum WsOutputs implements Outputs {

    // ---------------------------------
    // Drive Motors
    // ---------------------------------
    DRIVE1("Module 1 Drive Motor", new WsSparkConfig(CANConstants.DRIVE1, WsMotorControllers.SPARK_FLEX_BRUSHLESS)),
    ANGLE1("Module 1 Angle Motor", new WsSparkConfig(CANConstants.ANGLE1, WsMotorControllers.SPARK_MAX_BRUSHLESS)),
    DRIVE2("Module 2 Drive Motor", new WsSparkConfig(CANConstants.DRIVE2, WsMotorControllers.SPARK_FLEX_BRUSHLESS)),
    ANGLE2("Module 2 Angle Motor", new WsSparkConfig(CANConstants.ANGLE2, WsMotorControllers.SPARK_MAX_BRUSHLESS)),
    DRIVE3("Module 3 Drive Motor", new WsSparkConfig(CANConstants.DRIVE3, WsMotorControllers.SPARK_FLEX_BRUSHLESS)),
    ANGLE3("Module 3 Angle Motor", new WsSparkConfig(CANConstants.ANGLE3, WsMotorControllers.SPARK_MAX_BRUSHLESS)),
    DRIVE4("Module 4 Drive Motor", new WsSparkConfig(CANConstants.DRIVE4, WsMotorControllers.SPARK_FLEX_BRUSHLESS)),
    ANGLE4("Module 4 Angle Motor", new WsSparkConfig(CANConstants.ANGLE4, WsMotorControllers.SPARK_MAX_BRUSHLESS)),
    SHOOTER_VORTEX("Shooter", new WsSparkConfig(CANConstants.SHOOTER, WsMotorControllers.SPARK_FLEX_BRUSHLESS)),
    SHOOTER_FOLLOWER("Shooter Follower", new WsSparkFollowerConfig("Shooter", CANConstants.SHOOTER_FOLLOWER, WsMotorControllers.SPARK_FLEX_BRUSHLESS, true)),
    SHOOTER_NEO("Shooter Neo", new WsSparkConfig(CANConstants.SHOOTER_NEO, WsMotorControllers.SPARK_MAX_BRUSHLESS)),
    INTAKE("Intake", new WsSparkConfig(CANConstants.INTAKE, WsMotorControllers.SPARK_MAX_BRUSHLESS)),
    FEED("Feed", new WsSparkConfig(CANConstants.FEED, WsMotorControllers.SPARK_MAX_BRUSHED)),
    KICKER("Kicker", new WsSparkConfig(CANConstants.KICKER, WsMotorControllers.SPARK_MAX_BRUSHED)),
    ARM_PIVOT("Arm Pivot", new WsSparkConfig(CANConstants.ARM_PIVOT, WsMotorControllers.SPARK_MAX_BRUSHLESS)),
    LIFT("Lift", new WsSparkConfig(CANConstants.LIFT, WsMotorControllers.SPARK_MAX_BRUSHLESS, true)),
    LIFT_FOLLOWER("Lift Follower", new WsSparkFollowerConfig("Lift", CANConstants.LIFT_FOLLOWER, WsMotorControllers.SPARK_MAX_BRUSHLESS, true)),
    

    // ---------------------------------
    // Other Motors
    // ---------------------------------

    // ---------------------------------
    // Solenoids
    // ---------------------------------

    // ---------------------------------
    // Other
    // ---------------------------------

    ; // end of enum

    /**
     * Do not modify below code, provides template for enumerations.
     * We would like to have a super class for this structure, however,
     * Java does not support enums extending classes.
     */

    private String m_name;
    private OutputConfig m_config;

    /**
     * Initialize a new Output.
     * @param p_name Name, must match that in class to prevent errors.
     * @param p_config Corresponding configuration for OutputType.
     */
    WsOutputs(String p_name, OutputConfig p_config) {
        m_name = p_name;
        m_config = p_config;
    }

    /**
     * Returns the name mapped to the Output.
     * @return Name mapped to the Output.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the config of Output for the enumeration.
     * @return OutputConfig of enumeration.
     */
    public OutputConfig getConfig() {
        return m_config;
    }

    /**
     * Returns the actual Output object from the OutputManager
     * @return The corresponding output.
     */
    public Output get() {
        return Core.getOutputManager().getOutput(this);
    }
}