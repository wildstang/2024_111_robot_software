package org.wildstang.year2024.subsystems.testforlift;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;


public class Test_lift implements Subsystem {



    private WsSpark lift1;
    private DigitalInput lockLiftButton;
    private AnalogInput joyStickUpInput;
   // private DigitalInput liftPreset1;
   // private DigitalInput liftPreset2;
    private boolean isLiftLocked = false;
    private double liftSpeed = 0;






    @Override
    public void init() {
        lift1 = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.LIFT1);   
        motorSetUp(lift1);

        joyStickUpInput = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_JOYSTICK_Y);
        joyStickUpInput.addInputListener(this);

        lockLiftButton = (DigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_JOYSTICK_BUTTON);
        lockLiftButton.addInputListener(this);

       // liftPreset1 = (DigitalInput) Core.getInputManager().getInput(WsInputs."Button Preset is Assiagned to");
      // liftPreset1.addInputListener(this);

       // liftPreset2 = (DigitalInput) Core.getInputManager().getInput(WsInputs."Button Preset is Assiagned to");
       // liftPreset2.addInputListener(this);

    }


    @Override
    public void inputUpdate(Input source) {
        // TODO Auto-generated method stub

        if (Math.abs(joyStickUpInput.getValue()) > 0.05 && source == joyStickUpInput && isLiftLocked == false) {
            liftSpeed = joyStickUpInput.getValue();
        }
        else {
            liftSpeed = 0;
        }

        /*if (liftPreset1.getValue() && source == joyStickUpInput && isLiftLocked == false) {
            setPosition("Where motor should move to");
        }
        else {
            liftSpeed = 0;
        }*/

        /*if (liftPreset2.getValue() && source == joyStickUpInput && isLiftLocked == false) {
            setPosition("Where motor should move to");
        }
        else {
            liftSpeed = 0;
        }*/

        if(lockLiftButton.getValue() && source == lockLiftButton && isLiftLocked == true) {
            isLiftLocked = false;
        }

        if(lockLiftButton.getValue() && source == lockLiftButton && isLiftLocked == false) {
            isLiftLocked = true;
        }

        
    }

  /*  public void setPosition(double newPosition){
        if (newPosition < 0) lift1.setPosition(0);
        else lift1.setPosition(newPosition);
    } */


    private void motorSetUp(WsSpark setupMotor){
        setupMotor.setCurrentLimit(80, 20, 10000);
        setupMotor.enableVoltageCompensation();
    }


    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selfTest'");
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        lift1.setSpeed(liftSpeed);
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub
        liftSpeed = 0;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return ("Test_lift");
    }

}
