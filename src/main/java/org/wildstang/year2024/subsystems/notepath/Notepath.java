package org.wildstang.year2024.subsystems.notepath;

import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsDigitalInput;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;

public class Notepath implements Subsystem {

    // State variables
    private double intakeSpeed, feedSpeed, kickSpeed;
    private boolean completingIntake;

    private WsSpark feed, intake, kick;
    private AnalogInput driverRightTrigger, driverLeftTrigger;
    private DigitalInput driverRightShoulder, driverLeftShoulder;
    
    private WsDigitalInput intakeSensor;

    @Override
    public void inputUpdate(Input source) {
        // We want to shoot a note
        if (driverRightTrigger.getValue() > 0.15) {
            // Into Speaker
            if (driverLeftTrigger.getValue() > 0.15) {
                feedSpeed = 1.0;
                kickSpeed = 1.0;
            // Into Amp
            } else if (driverLeftShoulder.getValue()) {
                feedSpeed = -1.0;
            }
        // Detects note, intaking done
        } else {
            kickSpeed = 0;
            feedSpeed = 0;
        }
        if (intakeSensor.getValue()) {
            completingIntake = false;
        } 
        if ((driverRightShoulder.getValue() || completingIntake) && (intakeSensor.getValue() == false)) {
            intakeSpeed = 1.0;
        } else {
            intakeSpeed = 0;
        }
    }

    @Override
    public void init() {

        // Init Outputs
        intake = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.INTAKE);
        intake.setCurrentLimit(80, 20, 10000);

        kick = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.KICKER);
        kick.setCurrentLimit(80, 20, 10000);    

        feed = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.FEED);
        feed.setCurrentLimit(80, 20, 10000);
        
        // Init Inputs
        intakeSensor = (WsDigitalInput) Core.getInputManager().getInput(WsInputs.FEED_SWITCH);
        driverRightTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_TRIGGER);
        driverLeftTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        driverRightShoulder = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_SHOULDER);
        driverLeftShoulder = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_SHOULDER);
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selfTest'");
    }

    @Override
    public void update() {
        if (intakeHasGrabbed()) {
            completingIntake = true;
        }

        intake.setSpeed(intakeSpeed);
        feed.setSpeed(feedSpeed);
        kick.setSpeed(kickSpeed);
    }

    @Override
    public void resetState() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resetState'");
    }

    public boolean intakeHasGrabbed() { 
        // If intake not spinning we not intakin
        return intake.getController().getOutputCurrent() > 15.0;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "FeedSubsystem";
    }
    
}
