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
import au.grapplerobotics.LaserCan;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Notepath implements Subsystem {

    // State variables
    private double intakeSpeed, feedSpeed, kickSpeed;
    private boolean isReverse;
    private Intake intakeState; 

    private enum Intake { CHILL, SPINNING, INTAKING, REVERSE, AMP, SHOOT };

    private WsSpark feed, intake, kick;
    private AnalogInput rightTrigger, leftTrigger;
    private DigitalInput leftShoulder, rightShoulder;
    private Timer intakeTimer = new Timer();

    private LaserCan lc;

    @Override
    public void inputUpdate(Input source) {

        if (rightTrigger.getValue()>0.15 && leftTrigger.getValue() > 0.15){
            // Into Speaker
            intakeState = Intake.SHOOT;
        } else if (rightTrigger.getValue()>0.15 && leftShoulder.getValue()){
            // Into Amp
            intakeState = Intake.AMP;
        } else if (leftTrigger.getValue()>0.15 || leftShoulder.getValue()){
            // Cancel intake because LaserCAN now can't see the note
            intakeState = Intake.CHILL;  
        } else if (rightTrigger.getValue()>0.15 && !hasNote()){
            // Intaking
            startIntaking();
        } else if (rightTrigger.getValue()<0.15 && !hasNote()){
            // If driver stops holding down trigger and we never left spinning state, give up
            stopIntaking();;
        }

        isReverse = rightShoulder.getValue();
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
            return 9000;
        }
    }

    public boolean hasNote() {
        // Has reached the centered normal note distance
        return laserDistance() < NotepathConsts.FRAME_DIST;
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
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        SmartDashboard.putNumber("Laser Can distance (mm)", laserDistance());

        switch (intakeState) {
            case CHILL:
                if (isReverse){
                    intakeSpeed = -1.0;
                    feedSpeed = -1.0;
                    kickSpeed = -1.0;
                } else {
                    intakeSpeed = 0;
                    feedSpeed = 0;
                    kickSpeed = 0;
                }
            case SPINNING:
                // Change condition
                if (laserDistance() < NotepathConsts.FRAME_DIST) {
                    intakeState = Intake.INTAKING;
                // Normal state action
                } else if (isReverse){
                    intakeState = Intake.CHILL;
                } else {
                    intakeSpeed = 1.0;
                    feedSpeed = 1.0;
                    kickSpeed = 0;
                }
            case INTAKING:
                if (laserDistance() <= NotepathConsts.REVERSE_INTAKE_DIST) {
                    intakeState = Intake.REVERSE;
                    feedSpeed = -0.25;
                    intakeSpeed = 0;
                    kickSpeed = 0;
                } else if (isReverse){
                    intakeState = Intake.CHILL;
                } else {
                    intakeSpeed = 1.0;
                    feedSpeed = 1.0;
                    kickSpeed = 0;
                }
            case REVERSE:
                if (laserDistance() >= NotepathConsts.NORMAL_NOTE_DIST) {
                    intakeState = Intake.CHILL;
                    intakeSpeed = 0;
                    feedSpeed = 0;
                    kickSpeed = 0;
                } else if (isReverse){
                    intakeState = Intake.CHILL;
                } else {
                    feedSpeed = -0.25;
                    intakeSpeed = 0;
                    kickSpeed = 0;
                }
            case AMP:
                intakeSpeed = 0;
                feedSpeed = isReverse ? 1.0 : -1.0;
                kickSpeed = 0;
            case SHOOT:
                intakeSpeed = 1.0;
                feedSpeed = 1.0;
                kickSpeed = 1.0;
            break;
        }
        setIntake(intakeSpeed);
        feed.setSpeed(feedSpeed);
        kick.setSpeed(kickSpeed);
    }

    @Override
    public void resetState() {
        isReverse = false;
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
        return "FeedSubsystem";
    }
    
}