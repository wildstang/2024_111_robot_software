package org.wildstang.year2024.robot;

import org.wildstang.framework.core.AutoPrograms;
import org.wildstang.year2024.auto.Programs.CenterBlue;
import org.wildstang.year2024.auto.Programs.CenterRed;
import org.wildstang.year2024.auto.Programs.FastCenterBlue;
import org.wildstang.year2024.auto.Programs.FastCenterRed;
import org.wildstang.year2024.auto.Programs.SmartOffsideBlue;
import org.wildstang.year2024.auto.Programs.OffsideRed;
import org.wildstang.year2024.auto.Programs.RushBlue;
import org.wildstang.year2024.auto.Programs.RushRed;
import org.wildstang.year2024.auto.Programs.TestProgram;

/**
 * All active AutoPrograms are enumerated here.
 * It is used in Robot.java to initialize all programs.
 */
public enum WsAutoPrograms implements AutoPrograms {

    // enumerate programs
    //SAMPLE_PROGRAM("Sample", SampleAutoProgram.class),
    // CENTER_BLUE("Center Blue", CenterBlue.class),
    // CENTER_RED("Center Red", CenterRed.class),
    OFFSIDE_BLUE("Offside Blue", SmartOffsideBlue.class),
    OFFSIDE_RED("Offside Red", OffsideRed.class),
    TEST_PROGRAM("Test Program", TestProgram.class),
    RUSH_BLUE("Rush Blue", RushBlue.class),
    RUSH_RED("Rush Red", RushRed.class),
    FAST_BLUE("Fast Center Blue", FastCenterBlue.class),
    FAST_RED("Fast Center Red", FastCenterRed.class),
    ;

    /**
     * Do not modify below code, provides template for enumerations.
     * We would like to have a super class for this structure, however,
     * Java does not support enums extending classes.
     */
    
    private String name;
    private Class<?> programClass;

    /**
     * Initialize name and AutoProgram map.
     * @param name Name, must match that in class to prevent errors.
     * @param programClass Class containing AutoProgram
     */
    WsAutoPrograms(String name, Class<?> programClass) {
        this.name = name;
        this.programClass = programClass;
    }

    /**
     * Returns the name mapped to the AutoProgram.
     * @return Name mapped to the AutoProgram.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns AutoProgram's class.
     * @return AutoProgram's class.
     */
    @Override
    public Class<?> getProgramClass() {
        return programClass;
    }
}