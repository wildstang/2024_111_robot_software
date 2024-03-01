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
    private WsSpark angleNeo;
    private AbsoluteEncoder angleEncoder;

    // State variables
    private double vortexMotorsSpeed = 0;
    private double angle;
    private boolean leftTriggerPressed;
    private boolean noteHeld;

    // Auto
    public boolean isClose;
    private double autoClose = 0.5;
    private double autoFar = 0.3;

    private WsVision wsVision;


    @Override
    public void inputUpdate(Input source) {
        if (source == leftTrigger) {
            leftTriggerPressed = leftTrigger.getValue() >= 0;
        }
        // Update noteHeld Here using a reference to notepath subsystem which is currently in another branch

        double distanceToSpeaker = WsVision.distanceToSpeaker();
        // Aim if trigger pressed and Speaker in sight
        if (leftTriggerPressed && distanceToSpeaker != -1) {
        
            // Assumes array is in order of distance
            for (int i = 0; i < ShooterConsts.SHOOTER_POSIIONS.length; i++) {
                double[] position = ShooterConsts.SHOOTER_POSIIONS[i];
                if (distanceToSpeaker > position[0])
                {
                    // Linear interpolation between two points
                    // Speed = speed1 + slope between points * change in distance
                    vortexMotorsSpeed = position[1] + ((ShooterConsts.SHOOTER_POSIIONS[i + 1][1] - position[1]) / (ShooterConsts.SHOOTER_POSIIONS[i + 1][0] - position[0])) * (ShooterConsts.SHOOTER_POSIIONS[i + 1][0] - position[1]);
                    angle = position[2] + ((ShooterConsts.SHOOTER_POSIIONS[i + 1][2] - position[2]) / (ShooterConsts.SHOOTER_POSIIONS[i + 1][0] - position[0])) * (ShooterConsts.SHOOTER_POSIIONS[i + 1][0] - position[1]);
                    break;
                }
            }
        } else {
            if (noteHeld) {
                angle = 0;
                vortexMotorsSpeed = 30;
            } else {
                angle = ShooterConsts.MAX_ANGLE;
                vortexMotorsSpeed = 0;
            }
        }
    }

    @Override
    public void init() {
        wsVision = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);


        // Init Motors
        vortexFlywheel.setCurrentLimit(80, 20, 10000);
        vortexFlywheel = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.VORTEX1);
        angleNeo = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.ARMNEO);
        angleEncoder = angleNeo.getController().getAbsoluteEncoder(Type.kDutyCycle);
        angleNeo.initClosedLoop(ShooterConsts.P, ShooterConsts.I, ShooterConsts.D, 0, this.angleEncoder);
        // Returns position in degrees instead of rotations
        angleEncoder.setPositionConversionFactor(360.0);

        // Init Inputs
        leftTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);

    }

    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selfTest'");
    }

    @Override
    public void update() {
        vortexFlywheel.setSpeed(vortexMotorsSpeed);
        angleNeo.setPosition(angle);
    }

    public void autoSetAngle(boolean isClose) {

        if (isClose == true){
            angle = autoClose;
        }
        if (isClose == false){
            angle = autoFar;
        }
    }

    public void setSpeed(double speed){
        this.vortexMotorsSpeed = speed;
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
