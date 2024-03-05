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
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Notepath implements Subsystem {

    // State variables
    private double intakeSpeed, feedSpeed, kickSpeed;
    private Intake intakeState; 

    private enum Intake { CHILL, SPINNING, INTAKING, REVERSE, HAS_NOTE };

    private WsSpark feed, intake, kick;
    private AnalogInput driverRightTrigger, driverLeftTrigger;
    private DigitalInput driverLeftShoulder;

    private LaserCan lc;

    @Override
    public void inputUpdate(Input source) {
        if (source != driverRightTrigger) { return; }

        if (hasNote()) {
            // We want to shoot a note
            if (driverRightTrigger.getValue() > 0.15) {
                intakeState = Intake.CHILL;
                // Into Speaker
                if (driverLeftTrigger.getValue() > 0.15) {
                    feedSpeed = 1.0;
                    kickSpeed = 1.0;
                // Into Amp
                } else if (driverLeftShoulder.getValue()) {
                    feedSpeed = -1.0;
                }
            } else {
                kickSpeed = 0.0;
                feedSpeed = 0.0;
            }
        } else {
            // Intaking
            if ((driverRightTrigger.getValue() > 0.15) && !hasNote()) {
                startIntaking();
            } else {
                // If driver stops holding down trigger and we never left spinning state, give up
                if (intakeState == Intake.SPINNING) {
                    stopIntaking();;    
                }
            }
        }
    }

    public void startIntaking() {
        if (intakeState == Intake.CHILL) { intakeState = Intake.SPINNING; }
    }

    public void stopIntaking() {
        intakeState = Intake.CHILL;
    }

    public void shootSpeaker() {
        feedSpeed = 1.0;
        kickSpeed = 1.0;
        intakeState = Intake.CHILL;
    }

    public void shootAmp() {
        feedSpeed = -1.0;
        intakeState = Intake.CHILL;
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
        return intakeState == Intake.HAS_NOTE;
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
        //the intake senseor will be a LaserCAN
        // https://github.com/GrappleRobotics/LaserCAN/blob/master/docs/example-java.md
        lc = new LaserCan(0);

        driverRightTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_TRIGGER);
        driverRightTrigger.addInputListener(this);
        driverLeftTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        driverLeftTrigger.addInputListener(this);
        driverLeftShoulder = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_SHOULDER);
        driverLeftShoulder.addInputListener(this);
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selfTest'");
    }

    @Override
    public void update() {
        SmartDashboard.putNumber("Laser Can distance (mm)", laserDistance());

        switch (intakeState) {
            case CHILL:
            break;
            case SPINNING:
                // Change condition
                if (laserDistance() < NotepathConsts.FRAME_DIST) {
                    intakeState = Intake.INTAKING;
                // Normal state action
                } else {
                    intakeSpeed = 1.0;
                    feedSpeed = 1.0;
                    break;
                }
            case INTAKING:
                if (laserDistance() <= NotepathConsts.REVERSE_INTAKE_DIST) {
                    intakeState = Intake.REVERSE;
                } else {
                    intakeSpeed = 1.0;
                    feedSpeed = 1.0;
                }
            case REVERSE:
                if (laserDistance() >= NotepathConsts.NORMAL_NOTE_DIST) {
                    intakeState = Intake.HAS_NOTE;
                    intakeSpeed = 0;
                    feedSpeed = 0;
                } else {
                    feedSpeed = -0.25;
                    intakeSpeed = 0;
                }
            case HAS_NOTE:
            break;
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