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

    public double distanceToSpeaker() {
        // Make sure we see a AprilTag
        if (!front.TargetInView()) { return -1; }
        // Make sure it is the speaker AprilTag
        if (front.tid != 7 && front.tid != 4) { return -1; }
        return (LimeConsts.APRIL_TAG_HEIGHTS[(int) front.tid] - LimeConsts.VERTICAL_LIMELIGHT_MOUNT) / Math.tan(front.tv);
    }

    @Override
    public void init() {
        LC = new LimeConsts();

        resetState();
    }

    @Override
    public void selfTest() {
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