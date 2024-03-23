package org.wildstang.year2024.subsystems.theFolder;

import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsDigitalInput;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;
import au.grapplerobotics.LaserCan;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class theClass implements Subsystem {

    // State variables
    private double intakeSpeed=0, feedSpeed=0, kickSpeed=0;
    private boolean isReverse = false;
    private boolean isFiring = false;
    private Intake intakeState = Intake.CHILL; 

    private enum Intake { CHILL, SPINNING, INTAKING, REVERSE, AMP, SHOOT };

    private WsSpark feed, intake, kick;
    private AnalogInput rightTrigger, leftTrigger;
    private DigitalInput leftShoulder, rightShoulder, aButton;
    private Timer intakeTimer = new Timer();
    private Timer feedTimer = new Timer();

    private LaserCan lc, lc2;

    @Override
    public void inputUpdate(Input source) {

        if (Math.abs(rightTrigger.getValue())>0.15 && Math.abs(leftTrigger.getValue()) > 0.15){
            // Into Speaker
            intakeState = Intake.SHOOT;
        } else if (aButton.getValue() && Math.abs(rightTrigger.getValue())>0.15){
            intakeState = Intake.SHOOT;
        } else if (leftShoulder.getValue()){
            // Into Amp
            intakeState = Intake.AMP;
        } else if (Math.abs(leftTrigger.getValue())>0.15 || leftShoulder.getValue()){
            // Cancel intake because LaserCAN now can't see the note
            intakeState = Intake.CHILL;  
        } else if (Math.abs(rightTrigger.getValue())>0.15){// && !hasNote()){
            // Intaking
            intakeState = Intake.SPINNING;
        } else if (Math.abs(rightTrigger.getValue())<0.15 && intakeState == Intake.SPINNING){
            // If driver stops holding down trigger and we never left spinning state, give up
            intakeState = Intake.CHILL;
        }

        isReverse = rightShoulder.getValue();
        isFiring = Math.abs(rightTrigger.getValue())>0.15;
    }

    public void startIntaking() {
        intakeState = Intake.SPINNING; 
    }

    public void stopIntaking() {
        intakeState = Intake.CHILL;
    }

    public void shootSpeaker() {
        intakeState = Intake.SHOOT;
        isFiring = true;
    }

    public void shootAmp() {
        intakeState = Intake.AMP;
        isFiring = true;
    }

    // Turn off motors
    public void stop() {
        feedSpeed = 0.0;
        kickSpeed = 0.0;
        intakeSpeed = 0.0;
    }

    private double laserDistance(int id) {
        LaserCan.Measurement measurement = lc.getMeasurement();
        if (id == 1) measurement = lc2.getMeasurement();
        if (measurement != null && measurement.status == LaserCan.LASERCAN_STATUS_VALID_MEASUREMENT) {
            return (measurement.distance_mm);
        } else {
        // System.out.println("Oh no! The target is out of range, or we can't get a reliable measurement!");
            return 400;
        }
    }
    private boolean closeLaser(){
        LaserCan.Measurement measure = lc2.getMeasurement();
        return (measure != null && measure.distance_mm < 300);
    }
    private boolean farLaser(){
        LaserCan.Measurement measure = lc.getMeasurement();
        return (measure != null && measure.distance_mm < 260);
    }

    public boolean hasNote() {
        // Has reached the centered normal note distance
        // return laserDistance() < 400;
        return closeLaser() || farLaser() || (intakeTimer.hasElapsed(0.5) && intake.getController().getOutputCurrent()>30);
    }
    public boolean isIntaking(){
        return intakeState == Intake.SPINNING;
    }

    @Override
    public void init() {

        // Init Outputs
        intake = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.INTAKE);
        intake.setCurrentLimit(50, 50, 0);

        kick = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.KICKER);
        kick.setCurrentLimit(50, 50, 0);    

        feed = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.FEED);
        feed.setCurrentLimit(50, 50, 0);
        
        // Init Inputs
        //the intake senseor will be a LaserCAN
        // https://github.com/GrappleRobotics/LaserCAN/blob/master/docs/example-java.md
        lc = new LaserCan(0);
        lc2 = new LaserCan(1);

        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);
        leftTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
        leftShoulder = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_SHOULDER);
        leftShoulder.addInputListener(this);
        rightShoulder = (DigitalInput) WsInputs.DRIVER_RIGHT_SHOULDER.get();
        rightShoulder.addInputListener(this);
        aButton = (DigitalInput) WsInputs.DRIVER_FACE_DOWN.get();
        aButton.addInputListener(this);

        intakeTimer.start();
        feedTimer.start();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        SmartDashboard.putNumber("Laser Can 1 distance (mm)", laserDistance(0));
        SmartDashboard.putNumber("Laser Can 2 distance (mm)", laserDistance(1));
        
        intakeSpeed = 0;
        feedSpeed = 0;
        kickSpeed = 0;

        // switch (intakeState) {
        //     case CHILL:
            if (intakeState == Intake.CHILL){
                if (isReverse){
                    intakeSpeed = -1.0;
                    feedSpeed = -1.0;
                    kickSpeed = -1.0;
                } else {
                    intakeSpeed = 0;
                    feedSpeed = 0;
                    kickSpeed = 0;
                }
            }
            // case SPINNING:
                // Change condition
            if (intakeState == Intake.SPINNING){
                if (closeLaser()) {
                    if (feedTimer.hasElapsed(0.1)){
                        intakeState = Intake.INTAKING;
                    }
                // Normal state action
                } else if (isReverse){
                    intakeState = Intake.CHILL;
                } else {
                    feedTimer.reset();
                    intakeSpeed = 1.0;
                    feedSpeed = 1.0;
                    kickSpeed = 0;
                }
            }
            // case INTAKING:
            if (intakeState == Intake.INTAKING){
                if (farLaser()) {
                    if (feedTimer.hasElapsed(0.05)){
                        intakeState = Intake.REVERSE;
                        feedTimer.reset();
                        feedSpeed = -0.25;
                        intakeSpeed = 0;
                        kickSpeed = 0;
                    }
                } else if (isReverse){
                    intakeState = Intake.CHILL;
                } else {
                    feedTimer.reset();
                    intakeSpeed = 1.0;
                    feedSpeed = 0.5;
                    kickSpeed = 0;
                }
            }
            // case REVERSE:
            if (intakeState == Intake.REVERSE){
                // if (laserDistance() >= 205) {
                    if (!farLaser()){
                        if (feedTimer.hasElapsed(0.1)){
                            intakeState = Intake.CHILL;
                            intakeSpeed = 0;
                            feedSpeed = 0;
                            kickSpeed = 0;
                        }
                    // }
                } else if (isReverse){
                    intakeState = Intake.CHILL;
                } else {
                    feedTimer.reset();
                    feedSpeed = -0.5;
                    intakeSpeed = 0;
                    kickSpeed = -0.25;
                }
            }
            // case AMP:
            if (intakeState == Intake.AMP){
                if (isReverse) intakeSpeed = -1.0;
                else intakeSpeed = 0;
                if (isReverse)feedSpeed = 1.0;
                else if (isFiring) feedSpeed = -1.0;
                else feedSpeed = 0;
                if (isReverse) kickSpeed = 1.0;
                else kickSpeed = 0;
            }
            // case SHOOT:
            if (intakeState == Intake.SHOOT){
                if (isFiring){
                    intakeSpeed = 1.0;
                    feedSpeed = 1.0;
                    kickSpeed = 1.0;
                } else {
                    intakeSpeed = 0;
                    feedSpeed = 0;
                    kickSpeed = 0;
                }
            }
            
        // }
        setIntake(intakeSpeed);
        feed.setSpeed(feedSpeed);
        kick.setSpeed(kickSpeed);
        SmartDashboard.putNumber("feed feed speed", feedSpeed);
        SmartDashboard.putNumber("feed intake speed", intakeSpeed);
        SmartDashboard.putNumber("feed kick speed", kickSpeed);
        SmartDashboard.putString("feed state", intakeState.toString());
        SmartDashboard.putBoolean("feed reverse", isReverse);
        SmartDashboard.putNumber("intake actual speed", intake.getOutput());
    }

    @Override
    public void resetState() {
        isReverse = false;
        intakeState = Intake.CHILL;
        intakeSpeed = 0;
        feedSpeed = 0;
        kickSpeed = 0;
    }

    public void setIntake(double speed){
        if (speed == 0){
            intakeTimer.reset();
            intake.setSpeed(0);
        } else if (speed > 0){
            intake.setSpeed(Math.min(1.0, 4.0*intakeTimer.get()));
        } else intake.setSpeed(speed);
    }

    @Override
    public String getName() {
        return "TheClass";
    }
    
}