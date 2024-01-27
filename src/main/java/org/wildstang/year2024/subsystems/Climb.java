package org.wildstang.year2024.subsystems;

import com.ctre.phoenix.sensors.Pigeon2;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.CANConstants;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.targeting.LimeConsts;
import org.wildstang.year2024.subsystems.targeting.WsVision;

public class Climb implements Subsystem {
    
private WsSpark climbMotor;
private AnalogInput joystick;

private double climbSpeed;

@Override
public void init() {

    climbMotor = (WsSpark) WsOutputs.CLIMB_MOTOR.get();
    //remember to call initInputs() here if you want to have that in a separate method
}


public void initInputs() {

    //make sure to add .addInputListener(this)
    joystick = (AnalogInput) WsInputs.OPERATOR_LEFT_JOYSTICK_Y.get();

}



public void inputUpdate(Input source) {

    //what happens when the joystick isn't pressed? How does the value of climbSpeed return to 0?
if (Math.abs(joystick.getValue()) > 0.05 && source == joystick) {

    climbSpeed = joystick.getValue();

}

}

@Override
public void update() {

climbMotor.setSpeed(climbSpeed);

}

@Override
public void selfTest() {
}

@Override
public void resetState() {

    climbSpeed = 0;

}

@Override
public String getName() {
    return "Climb";
}
}
