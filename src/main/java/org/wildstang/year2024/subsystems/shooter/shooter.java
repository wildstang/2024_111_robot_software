package org.wildstang.year2024.subsystems.shooter;

import java.util.Random;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.targeting.WsVision;
import org.wildstang.year2024.subsystems.theFolder.theClass;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkAbsoluteEncoder.Type;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class shooter implements Subsystem {

    // Inputs
    private AnalogInput leftTrigger, leftStickY, rightTrigger;
    private DigitalInput startButton, aButton;
    private DigitalInput dPadUp;
    private DigitalInput dPadDown;

    // Motors
    private WsSpark vortexFlywheel;
    private WsSpark neoFlywheel;
    private WsSpark angleNeo;
    private AbsoluteEncoder angleEncoder;

    private Timer idleTimer = new Timer();
    private Timer shootTimer = new Timer();
    private Timer angleTimer = new Timer();

    // State variables
    private double aimOffset = 0;
    private int angleSlot = 0;
    private boolean subwooferAimOverride = false;
    private boolean autoAim = false;
    private boolean autoOverride = false;
    private enum Speeds { 
        OFF(0.0), 
        IDLE(ShooterConsts.IDLE_SPEED), 
        CYCLE(ShooterConsts.CYCLE_SPEED),
        FEED(ShooterConsts.FEED_SPEED),
        MAX(1.0);
        private double percent;

        public double getPercent() {
            return percent;
        }
        Speeds(double percent) {
            this.percent = percent;
        }
    };
    private Speeds speed;
    private double angle = ShooterConsts.MIN_ANGLE;
    private boolean leftTriggerPressed;
    private boolean rightTriggerPressed;
    private double autoassume = 0;

    private theClass RandomThing;
    private WsVision wsVision;

    public double angle(double distance) {
        // Assumes array is in order of distance
        for (int i = 0; i < ShooterConsts.SHOOTER_POSIIONS.length; i++) {
            double[] position = ShooterConsts.SHOOTER_POSIIONS[i];
            if (distance > position[0])
            {
                // Linear interpolation between two points
                // Angle = angle1 + angle between points * change in distance
                return (position[2] + ((ShooterConsts.SHOOTER_POSIIONS[i + 1][1] - position[1]) / (ShooterConsts.SHOOTER_POSIIONS[i + 1][0] - position[0])) * (ShooterConsts.SHOOTER_POSIIONS[i + 1][0] - position[1]));
            }
        }
        return ShooterConsts.MIN_ANGLE;
    }

    @Override
    public void inputUpdate(Input source) {
        autoAim = false;
        leftTriggerPressed = leftTrigger.getValue() > 0.15;
        rightTriggerPressed = Math.abs(rightTrigger.getValue())>0.15;
        if (source == startButton && startButton.getValue()) {
            subwooferAimOverride = !subwooferAimOverride;
        }
        if (source == dPadUp && dPadUp.getValue()) {
            aimOffset += ShooterConsts.ANGLE_INCREMENT;
        }
        if (source == dPadDown && dPadDown.getValue()) {
            aimOffset -= ShooterConsts.ANGLE_INCREMENT;
        }
    }

    public void setAngle(double angle) {
        this.angle = angle;
        autoOverride = true;
        autoassume = angle;
    }

    public void autoAim(boolean on) {
        autoAim = on;
    }

    @Override
    public void init() {
        wsVision = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);
        RandomThing = (theClass) Core.getSubsystemManager().getSubsystem(WsSubsystems.THECLASS);

        // Init Motors
        vortexFlywheel = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.SHOOTER_VORTEX);
        vortexFlywheel.setCoast();

        neoFlywheel = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.SHOOTER_NEO);
        neoFlywheel.setCoast();

        angleNeo = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.ARM_PIVOT);
        angleEncoder = angleNeo.getController().getAbsoluteEncoder(Type.kDutyCycle);

        // PID
        angleNeo.initClosedLoop(ShooterConsts.P, ShooterConsts.I, ShooterConsts.D, 0, this.angleEncoder);
        angleNeo.addClosedLoop(0, ShooterConsts.P, ShooterConsts.I, ShooterConsts.D, 0);
        angleNeo.addClosedLoop(1, 0.02, 0, 0, 0);
        // Returns position in degrees instead of rotations
        angleEncoder.setPositionConversionFactor(360.0);
        //set current limit 
        angleNeo.setCurrentLimit(20, 20, 0);
        vortexFlywheel.setCurrentLimit(40, 40, 0);
        vortexFlywheel.setCoast();
        neoFlywheel.setCurrentLimit(40, 40, 0);


        // Init Inputs
        leftTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);

        startButton = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_START);
        startButton.addInputListener(this);
        aButton = (DigitalInput) WsInputs.DRIVER_FACE_DOWN.get();
        aButton.addInputListener(this);

        dPadDown = (DigitalInput) WsInputs.DRIVER_DPAD_DOWN.get();
        dPadDown.addInputListener(this);

        dPadUp = (DigitalInput) WsInputs.DRIVER_DPAD_UP.get();
        dPadUp.addInputListener(this);

        //this is just to turn autoaim off in teleop
        leftStickY = (AnalogInput) WsInputs.DRIVER_LEFT_JOYSTICK_Y.get();
        leftStickY.addInputListener(this);

        idleTimer.start();
        shootTimer.start();
        angleTimer.start();
    }

    @Override
    public void selfTest() {
    }

    public void setFlywheel(boolean on) {
        speed = on ? Speeds.MAX : Speeds.OFF;
    }

    @Override
    public void update() {
        // Aim if trigger pressed and Speaker in sight

        if (wsVision.aprilTagsInView()) angleTimer.reset();
        if (!RandomThing.hasNote() || rightTriggerPressed){
            idleTimer.reset();
        }
        if (leftTriggerPressed || autoAim) {
            angleSlot = 0;
            speed = Speeds.MAX;
            if (wsVision.getAngle() > 200) speed = Speeds.FEED;
            if (wsVision.aprilTagsInView()){
                if (!rightTriggerPressed) angle = wsVision.getAngle();
                shootTimer.reset();
            } else if (autoAim && shootTimer.hasElapsed(0.5)){
                angle =autoassume;
            }
        } else if (!autoOverride) {
            if (idleTimer.hasElapsed(1.0)) {
                speed = Speeds.IDLE;
                if (wsVision.canSeeSpeaker()) speed = Speeds.CYCLE;
                angleSlot = 1;
                if (wsVision.aprilTagsInView()) angle = wsVision.getAngle();
                if (angleTimer.hasElapsed(0.5)) angle = ShooterConsts.PREP_ANGLE;
                // else angle = ShooterConsts.PREP_ANGLE;
                // angle = ShooterConsts.PREP_ANGLE;
            } else {
                angle = ShooterConsts.MIN_ANGLE;
                speed = Speeds.OFF;
            }
        }
        if (subwooferAimOverride) {
            angle = ShooterConsts.SUBWOOFER_ANGLE;
            angleSlot = 0;
            speed = Speeds.CYCLE;
        } 
        vortexFlywheel.setSpeed(speed.getPercent());
        neoFlywheel.setSpeed(speed.getPercent() * ShooterConsts.SPIN_RATIO);
        angleNeo.setPosition(angle+aimOffset, angleSlot);
        SmartDashboard.putBoolean("isAutoAim", autoAim);
        SmartDashboard.putNumber("automatic angle offset", aimOffset);
        SmartDashboard.putNumber("shooter angle", angle);
        SmartDashboard.putNumber("shooter speed", speed.getPercent());
        SmartDashboard.putBoolean("shooter subwoofer override", subwooferAimOverride);
        SmartDashboard.putNumber("shooter vortex spin", vortexFlywheel.getVelocity());
        SmartDashboard.putNumber("shooter neo spin", neoFlywheel.getVelocity());
        SmartDashboard.putNumber("shooter vortex voltage", vortexFlywheel.getController().getAppliedOutput());
        SmartDashboard.putNumber("shooter neo voltage", neoFlywheel.getController().getAppliedOutput());
    }

    @Override
    public void resetState() {
        vortexFlywheel.setSpeed(0);
        neoFlywheel.setSpeed(0);
        angleNeo.setPosition(0);
        autoAim = false;
        leftTriggerPressed = false;
        rightTriggerPressed = false;
        autoOverride = false;
    }

    @Override
    public String getName() {
        return "Shooter";
    }
    public double getShooterVelocity(){
        return Math.abs(vortexFlywheel.getVelocity());
    }
    // public void setShooter(double speed){
    //     if (speed == 1){
    //         shootTimer.reset();
    //         vortexFlywheel.setSpeed(0);
    //     } else if (speed < 1){
    //         vortexFlywheel.setSpeed(Math.min(0.0, 1-4.0*shootTimer.get()));
    //     }
    // }
}