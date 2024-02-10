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

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArmControl implements Subsystem {
    private WsSpark ArmNeo;
    private DigitalInput Rotate_Positive, Rotate_Negative;
    private double targetAngle = 0.0;
    private final double BaseAngle = 2.0;
    private int logicNumber = 0;
    private final double  MAX_Angle = 180; // This maximum angle of rotation of Arm
    private final double  MIN_Angle = 0; // This minimum angle of rotation of Arm

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
                   targetAngle = Math.min(targetAngle + BaseAngle, MAX_Angle);  
        }        
            else if (logicNumber == -1) {
                   targetAngle = Math.max(targetAngle - BaseAngle, MIN_Angle);  
    }
    
}
    @Override
    public void init() {
        Rotate_Positive = (DigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_SHOULDER);
        Rotate_Positive.addInputListener(this);
        Rotate_Negative = (DigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_LEFT_SHOULDER);
        Rotate_Negative.addInputListener(this);

        ArmNeo = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.ARM_PIVOT);
        AbsoluteEncoder absEncoder = ArmNeo.getController().getAbsoluteEncoder(Type.kDutyCycle);
        absEncoder.setPositionConversionFactor(360.00);


        ArmNeo.initClosedLoop(0.1, 0.0, 0.0, 0.0, absEncoder);

        ArmNeo.setBrake();

        ArmNeo.setCurrentLimit(40, 40, 0);  
    }

    @Override
    public void update() {
        ArmNeo.setPosition(targetAngle);
        SmartDashboard.putNumber("arm angle target", targetAngle);
        SmartDashboard.putNumber("arm angle", ArmNeo.getPosition());
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

        
        
   
    

