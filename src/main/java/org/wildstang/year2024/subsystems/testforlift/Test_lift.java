package org.wildstang.year2024.subsystems.testforlift;

import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Test_lift implements Subsystem {



    private WsSpark lift1;
   private DigitalInput driverLeftBumper;
    private double liftPos = 0.0;
    private final double liftBottom = 0.0;
    private final double liftTop = 34.0;

    @Override
    public void init() {
        lift1 = (WsSpark) WsOutputs.LIFT.get();   
        motorSetUp(lift1);

       driverLeftBumper = (DigitalInput) WsInputs.DRIVER_LEFT_SHOULDER.get();
       driverLeftBumper.addInputListener(this);

    }
    @Override
    public void initSubsystems() {}
    
    private void motorSetUp(WsSpark setupMotor){
        setupMotor.initClosedLoop(0.2, 0.0, 0.0, 0.0);
        setupMotor.setBrake();
        setupMotor.setCurrentLimit(50, 50, 0);
    }

    @Override
    public void inputUpdate(Input source) {

        if (driverLeftBumper.getValue()) liftPos = liftTop;
        else liftPos = liftBottom;
        
    }


    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        if (liftPos < liftBottom) liftPos = liftBottom;
        if (liftPos > liftTop) liftPos = liftTop;
        lift1.setPosition(liftPos);
        SmartDashboard.putNumber("lift target", liftPos);
        SmartDashboard.putNumber("lift position", lift1.getPosition());
    }

    @Override
    public void resetState() {
        liftPos = liftBottom;
    }

    @Override
    public String getName() {
        return ("Test_lift");
    }

}
