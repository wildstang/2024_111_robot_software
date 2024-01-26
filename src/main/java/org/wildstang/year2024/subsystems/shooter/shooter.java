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
    private WsSpark Vortex;
    private double VortexAllMotorsSpeed = 0;
    private WsSpark NeoMotor1;
    private double NeoMotorSpeed = 1;
    private AnalogInput leftTrigger;
    private AnalogInput rightTrigger;
    private DigitalInput dpadUp;
    private DigitalInput dpadDown;
    private boolean leftTriggerPressed = false;
    private boolean rightTriggerPressed = false;
    private static final double MAX_SPEED = 1.0;
    private static final double MIN_SPEED = 0.0;
    private static final double SPEED_STEP = 0.05;



   


    @Override
    public void inputUpdate(Input source) {
        leftTriggerPressed = leftTrigger.getValue() > 0.15;
        rightTriggerPressed = rightTrigger.getValue() > 0.15;
        if (dpadUp.getValue()) {
            VortexAllMotorsSpeed = Math.min(VortexAllMotorsSpeed + SPEED_STEP, MAX_SPEED);
        }
        if (dpadDown.getValue()) {
            VortexAllMotorsSpeed = Math.max(VortexAllMotorsSpeed - SPEED_STEP, MIN_SPEED);
        }
    }

    @Override
    public void init() {
        Vortex = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.VORTEX1);
        NeoMotor1 = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.NEOMOTOR1);// add another motors if needed as following motors 

        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);
        leftTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
        dpadUp = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_DPAD_UP);
        dpadUp.addInputListener(this);
        dpadDown = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_DPAD_DOWN);
        dpadDown.addInputListener(this);
    }

    @Override
    public void selfTest() {
        
    }

    @Override
    public void update() {
        if (leftTriggerPressed) {
            VortexAllMotorsSpeed = Math.min(Math.max(VortexAllMotorsSpeed, MIN_SPEED), MAX_SPEED);
            Vortex.setSpeed(VortexAllMotorsSpeed);
        }
         else {
            Vortex.stop();
        }
        if (rightTriggerPressed){
            NeoMotor1.setSpeed(NeoMotorSpeed);
        }
         else{
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
