package org.wildstang.year2024.subsystems.shooter;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.year2024.subsystems.targeting.WsVision;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkAbsoluteEncoder;
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
    private double speed = 0;
    private double angle;
    private boolean leftTriggerPressed;
    private boolean noteHeld;

    private WsVision wsVision;

    public void updateState(double distance) {

            // Assumes array is in order of distance
            for (int i = 0; i < ShooterConsts.SHOOTER_POSIIONS.length; i++) {
                double[] position = ShooterConsts.SHOOTER_POSIIONS[i];
                if (distance > position[0])
                {
                    // Linear interpolation between two points
                    // Speed = speed1 + slope between points * change in distance
                    speed = position[1] + ((ShooterConsts.SHOOTER_POSIIONS[i + 1][1] - position[1]) / (ShooterConsts.SHOOTER_POSIIONS[i + 1][0] - position[0])) * (ShooterConsts.SHOOTER_POSIIONS[i + 1][0] - position[1]);
                    angle = position[2] + ((ShooterConsts.SHOOTER_POSIIONS[i + 1][2] - position[2]) / (ShooterConsts.SHOOTER_POSIIONS[i + 1][0] - position[0])) * (ShooterConsts.SHOOTER_POSIIONS[i + 1][0] - position[1]);
                    break;
                }
            }
    }

    @Override
    public void inputUpdate(Input source) {
        if (source == leftTrigger) {
            leftTriggerPressed = leftTrigger.getValue() >= 0;
        }
        // Update noteHeld Here using a reference to notepath subsystem which is currently in another branch

        double distanceToSpeaker = wsVision.distanceToSpeaker();
        // Aim if trigger pressed and Speaker in sight
        if (leftTriggerPressed && distanceToSpeaker != -1) {
            updateState(distanceToSpeaker);
        } else {
            if (noteHeld) {
                angle = 0;
                speed = ShooterConsts.IDLE_SPEED;
            } else {
                //we can keep the angle all the way down all the time now
                angle = ShooterConsts.MIN_ANGLE;
                speed = 0;
            }
        }
    }

    @Override
    public void init() {
        wsVision = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);


        // Init Motors
        vortexFlywheel = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.SHOOTER_FOLLOWER);

        neoFlywheel = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.SHOOTER_NEO);

        angleNeo = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.ARM_PIVOT);
        angleEncoder = angleNeo.getController().getAbsoluteEncoder(Type.kDutyCycle);

        // PID
        angleNeo.initClosedLoop(ShooterConsts.P, ShooterConsts.I, ShooterConsts.D, 0, this.angleEncoder);
        // Returns position in degrees instead of rotations
        angleEncoder.setPositionConversionFactor(360.0);
        //set current limit 
        angleNeo.setCurrentLimit(30, 30, 100);
        vortexFlywheel.setCurrentLimit(80, 20, 10000);


        // Init Inputs
        leftTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selfTest'");
    }

    @Override
    public void update() {
        vortexFlywheel.setSpeed(speed);
        neoFlywheel.setSpeed(speed * ShooterConsts.SPIN_RATIO);
        angleNeo.setPosition(angle);
    }

    @Override
    public void resetState() {
        vortexFlywheel.setSpeed(0);
        angleNeo.setPosition(0);
    }

    @Override
    public String getName() {
        return "Competition Shooter";
    }
}
