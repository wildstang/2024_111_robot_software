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
    private boolean isAmp = false;
    private boolean isShooting = false;
    private Intake intakeState = Intake.CHILL; 

    private enum Intake { CHILL, SPINNING, INTAKING, REVERSE, AMP, SHOOT };

    private WsSpark feed, intake, kick;
    private AnalogInput rightTrigger, leftTrigger;
    private DigitalInput leftShoulder, rightShoulder;
    private Timer intakeTimer = new Timer();
    private Timer feedTimer = new Timer();

    private LaserCan lc;

    @Override
    public void inputUpdate(Input source) {

        if (Math.abs(rightTrigger.getValue())>0.15 && Math.abs(leftTrigger.getValue()) > 0.15){
            // Into Speaker
            intakeState = Intake.SHOOT;
        } else if (leftShoulder.getValue()){
            // Into Amp
            intakeState = Intake.AMP;
        } else if (Math.abs(leftTrigger.getValue())>0.15 || leftShoulder.getValue()){
            // Cancel intake because LaserCAN now can't see the note
            intakeState = Intake.CHILL;  
        } else if (Math.abs(rightTrigger.getValue())>0.15 && !hasNote()){
            // Intaking
            startIntaking();
        } else if (Math.abs(rightTrigger.getValue())<0.15 && intakeState == Intake.SPINNING){
            // If driver stops holding down trigger and we never left spinning state, give up
            stopIntaking();;
        }

        isReverse = rightShoulder.getValue();
        isFiring = Math.abs(rightTrigger.getValue())>0.15;
        isShooting = Math.abs(leftTrigger.getValue())>0.15;
        isAmp = leftShoulder.getValue();
    }

    public void startIntaking() {
        intakeState = Intake.SPINNING; 
    }

    public void stopIntaking() {
        intakeState = Intake.CHILL;
    }

    public void shootSpeaker() {
        intakeState = Intake.SHOOT;
    }

    public void shootAmp() {
        intakeState = Intake.AMP;
    }

    // Turn off motors
    public void stop() {
        feedSpeed = 0.0;
        kickSpeed = 0.0;
        intakeSpeed = 0.0;
    }

    private double laserDistance() {
        LaserCan.Measurement measurement = lc.getMeasurement();
        if (measurement != null && measurement.status == LaserCan.LASERCAN_STATUS_VALID_MEASUREMENT) {
            return (measurement.distance_mm);
        } else {
        System.out.println("Oh no! The target is out of range, or we can't get a reliable measurement!");
            return 400;
        }
    }

    public boolean hasNote() {
        // Has reached the centered normal note distance
        return laserDistance() < 400;
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

        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);
        leftTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
        leftShoulder = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_SHOULDER);
        leftShoulder.addInputListener(this);
        rightShoulder = (DigitalInput) WsInputs.DRIVER_RIGHT_SHOULDER.get();
        rightShoulder.addInputListener(this);

        intakeTimer.start();
        feedTimer.start();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        SmartDashboard.putNumber("Laser Can distance (mm)", laserDistance());
        
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
                if (laserDistance() < 400) {
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
                if (laserDistance() <= 60) {
                    if (feedTimer.hasElapsed(0.01)){
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
                    if (feedTimer.hasElapsed(2.5)){
                        intakeState = Intake.CHILL;
                        intakeSpeed = 0;
                        feedSpeed = 0;
                        kickSpeed = 0;
                    // }
                } else if (isReverse){
                    intakeState = Intake.CHILL;
                } else {
                    feedSpeed = -0.25;
                    intakeSpeed = 0;
                    kickSpeed = -0.25;
                }
            }
            // case AMP:
            if (intakeState == Intake.AMP){
                intakeSpeed = 0;
                if (isReverse)feedSpeed = 1.0;
                else if (isFiring) feedSpeed = -1.0;
                else feedSpeed = 0;
                kickSpeed = 0;
            }
            // case SHOOT:
            if (intakeState == Intake.SHOOT){
                intakeSpeed = 1.0;
                feedSpeed = 1.0;
                kickSpeed = 1.0;
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
        }
    }

    @Override
    public String getName() {
        return "TheClass";
    }
    
}