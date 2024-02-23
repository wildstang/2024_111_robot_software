package org.wildstang.year2024.subsystems.shooter;



import java.util.Map;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class shooter implements Subsystem {
    private WsSpark Vortex, Vortex2;
    private double Vortex1Speed = 0.8;
    private double Vortex2Speed = 1.0;
    private WsSpark NeoMotor1;
    private double NeoMotorSpeed = 1;
    private AnalogInput leftTrigger;
    private AnalogInput rightTrigger;
    private DigitalInput driverStart;
    private boolean leftTriggerPressed = false;
    private boolean rightTriggerPressed = false;

    private Timer timer = new Timer();
    private ShuffleboardTab tab = Shuffleboard.getTab("2024 Testing");
    private GenericEntry shooter1 = tab.add("Shooter 1 Value", 0.8)
        .withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 1)).getEntry();
    private GenericEntry shooter2 = tab.add("Shooter 2 Value", 0.8)
        .withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 1)).getEntry();
    private boolean toUpdate = false;

   


    @Override
    public void inputUpdate(Input source) {
        leftTriggerPressed = leftTrigger.getValue() > 0.15;
        rightTriggerPressed = Math.abs(rightTrigger.getValue()) > 0.15;
        if (rightTriggerPressed) timer.reset();
        if (driverStart.getValue()) toUpdate = true;
        else toUpdate = false;
    }

    @Override
    public void init() {
        Vortex = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.SHOOTER);
        Vortex2 = (WsSpark) WsOutputs.SHOOTER_2.get();
        NeoMotor1 = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.KICKER);// add another motors if needed as following motors 

        Vortex.setCurrentLimit(50,50,0);
        Vortex2.setCurrentLimit(50, 50, 0);
        NeoMotor1.setCurrentLimit(50,50,0);

        rightTrigger = (AnalogInput) WsInputs.DRIVER_RIGHT_TRIGGER.get();
        rightTrigger.addInputListener(this);
        leftTrigger = (AnalogInput) WsInputs.DRIVER_LEFT_TRIGGER.get();
        leftTrigger.addInputListener(this);
        driverStart = (DigitalInput) WsInputs.DRIVER_START.get();
        driverStart.addInputListener(this);

        timer.start();
    }

    @Override
    public void selfTest() {
        
    }

    @Override
    public void update() {
        if (toUpdate){
            Vortex1Speed = shooter1.getDouble(0.8);
            Vortex2Speed = shooter2.getDouble(1.0);
        }
        SmartDashboard.putNumber("Shooter 1 value", Vortex1Speed);
        SmartDashboard.putNumber("Shooter 2 Value", Vortex2Speed);
        SmartDashboard.putBoolean("Shooter LT", leftTriggerPressed);
        SmartDashboard.putNumber("Shooter 1 velocity", Vortex.getVelocity());
        SmartDashboard.putNumber("Shooter 2 Velocity", Vortex2.getVelocity());
        if (leftTriggerPressed) {
            Vortex.setSpeed(Vortex1Speed);
            Vortex2.setSpeed(Vortex2Speed);
        }
         else {
            // Vortex.stop();
            // Vortex2.stop();
            Vortex.setSpeed(0);
            Vortex2.setSpeed(0);
        }
        if (rightTriggerPressed && leftTriggerPressed){
            NeoMotor1.setSpeed(NeoMotorSpeed);
        } else if (!timer.hasElapsed(0.5)){
            NeoMotor1.setSpeed(-0.25);
        }
         else{
            NeoMotor1.stop();
        }
    }

    @Override
    public void resetState() {
        toUpdate = false;
        timer.reset();
        
    }

    @Override
    public String getName() {
        return"Shooter";
    }
}
