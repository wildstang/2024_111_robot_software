package org.wildstang.year2024.subsystems.shooter;



import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class shooter implements Subsystem {
    private WsSpark Vortex;
    private double VortexAllMotorsSpeed = 1.0;
    private WsSpark NeoMotor1;
    private double NeoMotorSpeed = 1;
    private AnalogInput leftTrigger, lt2;
    private AnalogInput rightTrigger, rt2;
    private DigitalInput dpadUp;
    private DigitalInput dpadDown;
    private boolean leftTriggerPressed = false;
    private boolean rightTriggerPressed = false;
    private static final double MAX_SPEED = 1.0;
    private static final double MIN_SPEED = 0.0;
    private static final double SPEED_STEP = 0.05;



   


    @Override
    public void inputUpdate(Input source) {
        leftTriggerPressed = leftTrigger.getValue() > 0.15 || lt2.getValue() > 0.15;
        rightTriggerPressed = Math.abs(rightTrigger.getValue()) > 0.15 || Math.abs(rt2.getValue()) > 0.15;
        if (dpadUp.getValue()) {
            VortexAllMotorsSpeed = Math.min(VortexAllMotorsSpeed + SPEED_STEP, MAX_SPEED);
        }
        if (dpadDown.getValue()) {
            VortexAllMotorsSpeed = Math.max(VortexAllMotorsSpeed - SPEED_STEP, MIN_SPEED);
        }
    }

    @Override
    public void init() {
        Vortex = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.SHOOTER);
        NeoMotor1 = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.KICKER);// add another motors if needed as following motors 

        Vortex.setCurrentLimit(50,50,0);
        NeoMotor1.setCurrentLimit(50,50,0);

        rightTrigger = (AnalogInput) WsInputs.DRIVER_RIGHT_TRIGGER.get();
        rightTrigger.addInputListener(this);
        leftTrigger = (AnalogInput) WsInputs.DRIVER_LEFT_TRIGGER.get();
        leftTrigger.addInputListener(this);
        rt2 = (AnalogInput) WsInputs.OPERATOR_RIGHT_TRIGGER.get();
        rt2.addInputListener(this);
        lt2 = (AnalogInput) WsInputs.OPERATOR_LEFT_TRIGGER.get();
        lt2.addInputListener(this);
        dpadUp = (DigitalInput) WsInputs.OPERATOR_DPAD_UP.get();
        dpadUp.addInputListener(this);
        dpadDown = (DigitalInput) WsInputs.OPERATOR_DPAD_DOWN.get();
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
        if (rightTriggerPressed && leftTriggerPressed){
            NeoMotor1.setSpeed(NeoMotorSpeed);
        }
         else{
            NeoMotor1.stop();
        }
        SmartDashboard.putNumber("Shooter speed", VortexAllMotorsSpeed);
    }

    @Override
    public void resetState() {
        
    }

    @Override
    public String getName() {
        return"Shooter";
    }
}
