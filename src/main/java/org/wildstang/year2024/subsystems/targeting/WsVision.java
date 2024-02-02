package org.wildstang.year2024.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsVision implements Subsystem {

    public WsLL front = new WsLL("limelight-front");
    public WsLL back = new WsLL("limelight-back");

    public LimeConsts LC;

    ShuffleboardTab tab = Shuffleboard.getTab("Tab");

    

    @Override
    public void inputUpdate(Input source) {
        
    }

    @Override
    public void init() {
        LC = new LimeConsts();

        resetState();
    }

    @Override
    public void selfTest() {
    }

    //get distance from Amp
    public double getAmp(double distance){
        if (back.TargetInView()){
        return (AmpHeight - CameraHeight)/ Math.tan(back.ty);
    }
        else {return 0;}
    }

    @Override
    public void update() {
        front.update();
        back.update();
    }

    @Override
    public void resetState() {
        front.update();
        back.update();
    }

    @Override
    public String getName() {
        return "Ws Vision";
    }
}