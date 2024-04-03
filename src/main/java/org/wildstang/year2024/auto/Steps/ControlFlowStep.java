package org.wildstang.year2024.auto.Steps;

import java.util.function.Function;

import org.wildstang.framework.auto.AutoStep;


public class ControlFlowStep extends AutoStep {

    final AutoStep step1;
    final AutoStep step2;
    boolean runStep1;
    Function<Void, Boolean> getProgram;
    
    public ControlFlowStep(Function<Void, Boolean> getStep, AutoStep program1, AutoStep program2) {
        this.getProgram = getStep;
        this.step1 = program1;
        this.step2 = program2;
    }

    @Override
    public void initialize() {
        runStep1 = getProgram.apply(null);
        if (runStep1) {
            step1.initialize();
        } else {
            step2.initialize();
        }
    }

    @Override
    public void update() {
        if (runStep1) {
            step1.update();
            if (step1.isFinished()) {
                setFinished();
            }
        } else {
            step2.update();
            if (step2.isFinished()) {
                setFinished();
            }
        }
    }

    @Override
    public String toString() {
        return "Control Flow";
    }
}
