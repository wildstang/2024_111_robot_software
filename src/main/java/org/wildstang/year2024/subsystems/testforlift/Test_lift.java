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
    private AnalogInput joyStickUpInput;
   private DigitalInput liftPreset1;
   private DigitalInput liftPreset2;
    private double liftSpeed = 0;
    private double liftPos = 0.0;
    private final double liftBottom = 0.0;
    private final double liftTop = 15.0;






    @Override
    public void init() {
        lift1 = (WsSpark) WsOutputs.LIFT.get();   
        motorSetUp(lift1);

        joyStickUpInput = (WsJoystickAxis) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_JOYSTICK_Y);
        joyStickUpInput.addInputListener(this);


       liftPreset1 = (DigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_LEFT);
       liftPreset1.addInputListener(this);

       liftPreset2 = (DigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_FACE_UP);
       liftPreset2.addInputListener(this);

    }


    @Override
    public void inputUpdate(Input source) {

        if (Math.abs(joyStickUpInput.getValue()) > 0.15) {
            liftSpeed = joyStickUpInput.getValue();
        }
        else {
            liftSpeed = 0;
        }

        if (liftPreset1.getValue() && source == liftPreset1) {
            liftPos = liftBottom;
        }

        if (liftPreset2.getValue() && source == liftPreset2) {
            liftPos = liftTop;
        }
        
    }

   public void setPosition(double newPosition){
        if (newPosition < liftBottom) lift1.setPosition(liftBottom);
        if (newPosition > liftTop) lift1.setPosition(liftTop);
        else lift1.setPosition(newPosition);
    } 


    private void motorSetUp(WsSpark setupMotor){
        setupMotor.setCurrentLimit(50, 50, 0);
        setupMotor.initClosedLoop(0.1, 0.0, 0.0, 0.0);
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        liftPos += liftSpeed;
        setPosition(liftPos);
    }

    @Override
    public void resetState() {
        liftSpeed = 0;
        liftPos = 0.0;
    }

    @Override
    public String getName() {
        return ("Test_lift");
    }

}
