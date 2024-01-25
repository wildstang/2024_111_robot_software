package org.wildstang.year2024.subsystems.shooter;



import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;


public class shooter implements Subsystem {
    private WsSpark Vortex1;
    private WsSpark Vortex2;
    private double Vortex1Speed = 0;
    private double Vortex2Speed = 0;
    private WsSpark NeoMotor1;
    private AnalogInput leftTrigger;
    private AnalogInput rightTrigger;
    private DigitalInput dpadUp;
    private DigitalInput dpadDown;
    private boolean leftTriggerPressed = false;
    private boolean rightTriggerPressed = false;
    private static final double MAX_SPEED = 1.0;
    private static final double MIN_SPEED = 0.0;
    //I'd drop the speed step to like 0.05 for initial testing
    private static final double SPEED_STEP = 0.20;



   


    @Override
    public void inputUpdate(Input source) {
        //this is correct, but instead of an if-else to assign a boolean, you could just directly assign it
        //i.e. leftTriggerPressed = leftTrigger.getValue() > 0.15;
        if (leftTrigger.getValue() > 0.15) {
            leftTriggerPressed = true;
        } else {
            leftTriggerPressed = false;
        }
        if (rightTrigger.getValue() > 0.15) { 
            rightTriggerPressed = true;
        } else {
            rightTriggerPressed = false;
        }
        if (dpadUp.getValue()) {
            Vortex1Speed = Math.min(Vortex1Speed + SPEED_STEP, MAX_SPEED);
            Vortex2Speed = Math.min(Vortex2Speed + SPEED_STEP, MAX_SPEED);
        }
        if (dpadDown.getValue()) {
            Vortex1Speed = Math.max(Vortex1Speed - SPEED_STEP, MIN_SPEED);
            Vortex2Speed = Math.max(Vortex2Speed - SPEED_STEP, MIN_SPEED);
        }
    }

    @Override
    public void init() {
        Vortex1 = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.VORTEX1);
        Vortex2 = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.VORTEX2);
        NeoMotor1 = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.NEOMOTOR1);// add another motors if needed as following motors 

        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);
        leftTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
        //make sure to add .addInputListener(this) for the dpadUp and down buttons
        dpadUp = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_DPAD_UP);
        dpadDown = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_DPAD_DOWN);
    }

    @Override
    public void selfTest() {
        
    }

    @Override
    public void update() {
        if (leftTriggerPressed) {
            Vortex1Speed = Math.min(Math.max(Vortex1Speed, MIN_SPEED), MAX_SPEED);
            Vortex2Speed = Math.min(Math.max(Vortex2Speed, MIN_SPEED), MAX_SPEED);
            Vortex1.setSpeed(Vortex1Speed);
            Vortex2.setSpeed(Vortex2Speed);
        }
        //I would use else here instead of !boolean, so it's easier for future programmers to understand what's intended
        if (!leftTriggerPressed) {
            Vortex1.stop();
            Vortex2.stop();
        }
        if (rightTriggerPressed){
            NeoMotor1.setSpeed(1);
        }
        if (!rightTriggerPressed){
            NeoMotor1.stop();
        }
    }

    @Override
    public void resetState() {
        
    }

    @Override
    public String getName() {
        return"Shooter";
    }
}
