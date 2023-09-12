package org.wildstang.sample.subsystems;

import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
//import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsOutputs;

/**
 * Sample Subsystem that controls a motor with a joystick.
 * @author Liam
 */
public class SampleSubsystem implements Subsystem {
    // inputs
    WsJoystickAxis joystick;

    // outputs
    //WsPhoenix motor;

    // states
    double speed;


    @Override
    public void init() {
        joystick = (WsJoystickAxis) WsInputs.DRIVER_LEFT_JOYSTICK_Y.get();

        //motor = (WsPhoenix) WSOutputs.TEST_MOTOR.get();

        speed = 0;
    }

    @Override
    public void resetState() {
        speed = 0;
    }

    @Override
    public void update() {
        //motor.setValue(speed);
    }

    @Override
    public void inputUpdate(Input source) {
        if (source == joystick) {
            speed = joystick.getValue();
        }
    }

    @Override
    public String getName() {
        return "Sample";
    }

    @Override
    public void selfTest() {
    }
}