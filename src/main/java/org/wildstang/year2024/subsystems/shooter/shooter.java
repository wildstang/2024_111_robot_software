package org.wildstang.year2024.subsystems.shooter;

import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;

public class shooter implements Subsystem {

    private WsSpark vortexFlywheel;
    private double vortexMotorsSpeed = 0;

    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'inputUpdate'");
    }

    @Override
    public void init() {
        motorSetUp(vortexFlywheel);

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selfTest'");
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resetState'");
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    private void motorSetUp(WsSpark setupMotor){
        setupMotor.setCurrentLimit(80, 20, 10000);
        setupMotor.enableVoltageCompensation();
    }
    
}
