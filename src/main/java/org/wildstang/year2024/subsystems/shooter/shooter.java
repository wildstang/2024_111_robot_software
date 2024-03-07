package org.wildstang.year2024.subsystems.shooter;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.notepath.Notepath;
import org.wildstang.year2024.subsystems.targeting.WsVision;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkAbsoluteEncoder.Type;

public class Shooter implements Subsystem {

    // Inputs
    private AnalogInput leftTrigger;

    // Motors
    private WsSpark vortexFlywheel;
    private WsSpark neoFlywheel;
    private WsSpark angleNeo;
    private AbsoluteEncoder angleEncoder;

    // State variables
    private boolean autoAim = false;
    private boolean autoOverride = false;
    private enum Speeds { 
        OFF(0.0), 
        IDLE(ShooterConsts.IDLE_SPEED), 
        MAX(100.0);
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

    private Notepath notepath;
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
        leftTriggerPressed = leftTrigger.getValue() > 0.15;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void autoAim(boolean on) {
        autoAim = on;
    }

    @Override
    public void init() {
        wsVision = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);
        notepath = (Notepath) Core.getSubsystemManager().getSubsystem(WsSubsystems.NOTEPATH);

        // Init Motors
        vortexFlywheel = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.SHOOTER_FOLLOWER);

        neoFlywheel = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.SHOOTER_NEO);

        angleNeo = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.ARM_PIVOT);
        angleEncoder = angleNeo.getController().getAbsoluteEncoder(Type.kDutyCycle);

        // PID
        angleNeo.initClosedLoop(ShooterConsts.P, ShooterConsts.I, ShooterConsts.D, 0, this.angleEncoder);
        angleNeo.setBrake();
        // Returns position in degrees instead of rotations
        angleEncoder.setPositionConversionFactor(360.0);
        //set current limit 
        angleNeo.setCurrentLimit(20, 20, 0);
        vortexFlywheel.setCurrentLimit(50, 50, 0);


        // Init Inputs
        leftTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selfTest'");
    }

    public void setFlywheel(boolean on) {
        speed = on ? Speeds.MAX : Speeds.OFF;
    }

    @Override
    public void update() {
        double distanceToSpeaker = wsVision.distanceToSpeaker();
        // Aim if trigger pressed and Speaker in sight
        if ((leftTriggerPressed || autoAim) && distanceToSpeaker != -1) {
            speed = Speeds.MAX;
            angle = angle(distanceToSpeaker);
        } else if (!autoOverride) {
            if (notepath.hasNote()) {
                speed = Speeds.IDLE;
            } else {
                angle = ShooterConsts.MIN_ANGLE;
                speed = Speeds.OFF;
            }
        }
        
        vortexFlywheel.setSpeed(speed.getPercent());
        neoFlywheel.setSpeed(speed.getPercent() * ShooterConsts.SPIN_RATIO);
        angleNeo.setPosition(angle);
    }

    @Override
    public void resetState() {
        vortexFlywheel.setSpeed(0);
        neoFlywheel.setSpeed(0);
        angleNeo.setPosition(0);
    }

    @Override
    public String getName() {
        return "Competition Shooter";
    }
}