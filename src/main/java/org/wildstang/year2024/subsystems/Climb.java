package org.wildstang.year2024.subsystems;

import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;

public class Climb implements Subsystem{
    
private WsSpark climbMotor;
private AnalogInput joystick;
private DigitalInput start;
private DigitalInput select;

private double climbSpeed;
private double positionA = 5;
private double positionB = 10;
private boolean stepA;
private enum preset {none, A, B}
private preset currentPreset;

@Override
public void init() {

    climbMotor = (WsSpark) WsOutputs.CLIMB.get();
    climbMotor.initClosedLoop(0.1,0.0,0.0,0.0);
    climbMotor.setCurrentLimit(40, 40, 0);

    initInputs();

}

public void initInputs() {

    joystick = (AnalogInput) WsInputs.OPERATOR_LEFT_JOYSTICK_Y.get();
    joystick.addInputListener(this);

    start = (DigitalInput) WsInputs.OPERATOR_START.get();
    start.addInputListener(this);
    select = (DigitalInput) WsInputs.OPERATOR_SELECT.get();
    select.addInputListener(this);

}

public void inputUpdate(Input source) {

if (Math.abs(joystick.getValue()) > 0.05 && source == joystick) {

    climbSpeed = joystick.getValue();

}

if (start.getValue() && select.getValue()){
   
    if(stepA == false){
        currentPreset = preset.A;
        stepA = true;
    }

    else if(stepA == true){
        currentPreset = preset.B;
        stepA = false;
    }

}

}

@Override
public void update() {

if(currentPreset == preset.A){
    climbMotor.setPosition(positionA);
    currentPreset = preset.none;
}

if(currentPreset == preset.B){
    climbMotor.setPosition(positionB);
    currentPreset = preset.none;    
}

else{
    climbMotor.setSpeed(climbSpeed);
}

}
@Override
public void selfTest() {
}

public void resetState() {

    climbSpeed = 0;
    currentPreset = preset.none;
    stepA = false;

}

public String getName() {
    return "Climb";
}
}
