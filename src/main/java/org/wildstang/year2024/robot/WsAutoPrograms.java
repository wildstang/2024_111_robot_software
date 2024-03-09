package org.wildstang.year2024.robot;

import org.wildstang.framework.core.AutoPrograms;
import org.wildstang.year2024.auto.Programs.Auto113;
import org.wildstang.year2024.auto.Programs.Drake;
import org.wildstang.year2024.auto.Programs.TestA;
import org.wildstang.year2024.auto.Programs.TestB;
import org.wildstang.year2024.auto.Programs.TestC;
import org.wildstang.year2024.auto.Programs.TestProgram;
import org.wildstang.year2024.auto.Programs.TestProgram2;

/**
 * All active AutoPrograms are enumerated here.
 * It is used in Robot.java to initialize all programs.
 */
public enum WsAutoPrograms implements AutoPrograms {

    // enumerate programs
    //SAMPLE_PROGRAM("Sample", SampleAutoProgram.class),
    TEST_PROGRAM("Test Program", TestProgram.class),
    TEST_PROGRAM_2("Test Program 2", TestProgram2.class),
    AUTO_113("Auto 113 BAC", Auto113.class),
    TESTA("Test A", TestA.class),
    TESTB("Test B", TestB.class),
    TESTC("Test C", TestC.class),
    THE_DRAKE("The Drake", Drake.class)
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