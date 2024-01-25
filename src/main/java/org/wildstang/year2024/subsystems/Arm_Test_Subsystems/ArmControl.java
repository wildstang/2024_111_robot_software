package org.wildstang.year2024.subsystems.Arm_Test_Subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;
import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkAbsoluteEncoder.Type;
import com.revrobotics.CANSparkBase.ControlType;

public class ArmControl implements Subsystem {
    private WsSpark ArmNeo;
    private DigitalInput Rotate_Positive, Rotate_Negative;
    private double targetAngle = 0.0;
    private final double BaseAngle = 2.0;
    private int logicNumber = 0;

    @Override
    public void inputUpdate(Input source) {
            if (Rotate_Positive.getValue()) {
                logicNumber = 1;  
            } else if (Rotate_Negative.getValue()) {
                logicNumber =  -1;  
            } else{
                logicNumber = 0;
            }
               
            if (logicNumber == 1) {
                //you should parameterize that 360 for a "MAX ANGLE" (and min angle below), and maybe make it like 90 degrees
                   targetAngle = Math.min(targetAngle + BaseAngle, 360);  
        }        
            else if (logicNumber == -1) {
                   targetAngle = Math.max(targetAngle - BaseAngle, 0);  
    }
    
}
    @Override
    public void init() {
        Rotate_Positive = (DigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_SHOULDER);
        Rotate_Positive.addInputListener(this);
        Rotate_Negative = (DigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_SHOULDER);
        Rotate_Negative.addInputListener(this);

        ArmNeo = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.ARMNEO);
        AbsoluteEncoder absEncoder = ArmNeo.getController().getAbsoluteEncoder(Type.kDutyCycle);
        absEncoder.setPositionConversionFactor(360.00);

        //There's a few other commands needed to set up the closed loop. There's a "shortcut" method
        //ArmNeo.initClosedLoop(0.1, 0.0, 0.0, 0.0, absEncoder, false);
        //which does all of them. The arguments for that are the P, I, D, FF (should be 0), encoder, and if it's wrapped
        //(wrapped means does it go from 359 to 1 by crossing the 360 to 0 discontinuity or not. In this application it shouldn't)
        ArmNeo.getController().getPIDController().setP(0.1);  
        ArmNeo.getController().getPIDController().setI(0.0);  
        ArmNeo.getController().getPIDController().setD(0.0);  

        ArmNeo.setBrake();
        //this is a full neo, so this can be set to like 40 to start with
        ArmNeo.setCurrentLimit(15, 15, 0);  
    }

    @Override
    public void update() {
        //this is technically correct, but you can use ArmNeo.setPosition(targetAngle); to be a bit less verbose
        ArmNeo.getController().getPIDController().setReference(targetAngle, ControlType.kPosition);
    }

    @Override
    public void selfTest() {
        
    }

    @Override
    public void resetState() {
        
    }

    @Override
    public String getName() {
        return "Arm Control";
    }
}

        
        
   
    

