package org.wildstang.year2024.subsystems.Notepath;

import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class notepath implements Subsystem {

    private DigitalInput driverLeftShoulder;
    private AnalogInput driverRightTrigger;
    private WsSpark feed, intake;

    private final double speed = 1.0;
    private double direction = 0;
    private boolean isAmp = false;
    private boolean store = false;
    private boolean isIntake = false;
    private boolean isUp = false;

    private Timer timer = new Timer();

   


    @Override
    public void inputUpdate(Input source) {
        if (Math.abs(driverRightTrigger.getValue()) > 0.15) timer.reset();

        isIntake = Math.abs(driverRightTrigger.getValue()) > 0.15;

        isAmp = driverLeftShoulder.getValue() && Math.abs(driverRightTrigger.getValue()) > 0.15;

        isUp = driverLeftShoulder.getValue();


    }

    @Override
    public void init() {
        feed = (WsSpark) WsOutputs.FEED.get();
        feed.setCurrentLimit(50, 50, 0);
        intake = (WsSpark) WsOutputs.INTAKE.get();
        intake.setCurrentLimit(50, 50, 0);

        driverRightTrigger = (AnalogInput) WsInputs.DRIVER_RIGHT_TRIGGER.get();
        driverRightTrigger.addInputListener(this);
        driverLeftShoulder = (DigitalInput) WsInputs.DRIVER_LEFT_SHOULDER.get();
        driverLeftShoulder.addInputListener(this);

        timer.start();
    }

    @Override
    public void selfTest() {
        
    }

    @Override
    public void update() {
        
        store = !timer.hasElapsed(2.5);

        if (isAmp){
            intake.setSpeed(0.0);
            feed.setSpeed(-speed);
        } else if (isIntake){
            feed.setSpeed(1.0);
            intake.setSpeed(1.0);
        } else if (store){
            intake.setSpeed(0.0);
            feed.setSpeed(isUp ? 0.0 : -speed * 0.25);
        } else {
            feed.setSpeed(direction * speed);
            intake.setSpeed(direction*speed);
        }
        SmartDashboard.putNumber("feed speed", direction * speed);
    }

    @Override
    public void resetState() {
        isAmp = false;
        store = false;
    }

    @Override
    public String getName() {
        return"Notepath";
    }
}