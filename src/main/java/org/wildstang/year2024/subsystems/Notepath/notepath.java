package org.wildstang.year2024.subsystems.Notepath;

import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class notepath implements Subsystem {

    private DigitalInput aButton, bButton, driverLeftShoulder;
    private AnalogInput driverRightTrigger;
    private WsSpark feed, intake;

    private final double speed = 1.0;
    private double direction = 0;
    private boolean isAmp = false;



   


    @Override
    public void inputUpdate(Input source) {
        if (aButton.getValue() || driverRightTrigger.getValue()>0.15) direction = 1;
        else if (bButton.getValue()) direction = -1;
        else direction = 0;

        isAmp = driverLeftShoulder.getValue() && driverRightTrigger.getValue() > 0.15;
    }

    @Override
    public void init() {
        feed = (WsSpark) WsOutputs.FEED.get();
        feed.setCurrentLimit(50, 50, 0);
        intake = (WsSpark) WsOutputs.INTAKE.get();
        intake.setCurrentLimit(50, 50, 0);

        aButton = (DigitalInput) WsInputs.OPERATOR_FACE_DOWN.get();
        aButton.addInputListener(this);
        bButton = (DigitalInput) WsInputs.OPERATOR_FACE_RIGHT.get();
        bButton.addInputListener(this);
        driverRightTrigger = (AnalogInput) WsInputs.DRIVER_RIGHT_TRIGGER.get();
        driverRightTrigger.addInputListener(this);
        driverLeftShoulder = (DigitalInput) WsInputs.DRIVER_LEFT_SHOULDER.get();
        driverLeftShoulder.addInputListener(this);
    }

    @Override
    public void selfTest() {
        
    }

    @Override
    public void update() {
        if (isAmp){
            intake.setSpeed(0.0);
            feed.setSpeed(-speed);

        } else {
            feed.setSpeed(direction * speed);
            intake.setSpeed(direction*speed);
        }
        SmartDashboard.putNumber("feed speed", direction * speed);
    }

    @Override
    public void resetState() {
        
    }

    @Override
    public String getName() {
        return"Notepath";
    }
}
