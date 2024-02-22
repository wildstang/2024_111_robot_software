package org.wildstang.year2024.subsystems.Arm_Test_Subsystems;

import java.util.Map;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;
import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkAbsoluteEncoder.Type;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArmControl implements Subsystem {
    private WsSpark ArmNeo;
    private DigitalInput driverStart;
    private double targetAngle = 0.0;
    private AbsoluteEncoder absEncoder;
    private final double  MAX_Angle = 180; // This maximum angle of rotation of Arm
    private final double  MIN_Angle = 0; // This minimum angle of rotation of Arm
    private ShuffleboardTab tab = Shuffleboard.getTab("name");
    private GenericEntry arm = tab.add("Arm Position", 0.0)
        .withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 180)).getEntry();
    private boolean toUpdate = false;

    @Override
    public void inputUpdate(Input source) {
    if (driverStart.getValue()) toUpdate = true;
    else toUpdate = false;
    
}
    @Override
    public void init() {
        driverStart = (DigitalInput) WsInputs.DRIVER_START.get();
        driverStart.addInputListener(this);

        ArmNeo = (WsSpark) WsOutputs.ARM_PIVOT.get();
        absEncoder = ArmNeo.getController().getAbsoluteEncoder(Type.kDutyCycle);
        absEncoder.setPositionConversionFactor(360.00);


        ArmNeo.initClosedLoop(0.1, 0.0, 0.0, 0.0, absEncoder);

        ArmNeo.setBrake();

        ArmNeo.setCurrentLimit(20, 20, 0);  
    }

    @Override
    public void update() {
        if (toUpdate) targetAngle = arm.getDouble(0.0);
        ArmNeo.setPosition(Math.min(MAX_Angle, Math.max(MIN_Angle, targetAngle)));
        SmartDashboard.putNumber("arm angle target", targetAngle);
        SmartDashboard.putNumber("arm angle", absEncoder.getPosition());
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

        
        
   
    

