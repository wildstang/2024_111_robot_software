package org.wildstang.year2024.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose3d;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsVision implements Subsystem {

    public WsPV front = new WsPV("photon-front", true);
    public WsPV back = new WsPV("photon-back", false);

    public VisionConsts VC;

    ShuffleboardTab tab = Shuffleboard.getTab("Tab");

    // public double[] distances = {43.3, 83, 129};
    public double[] distances = {41, 50, 70, 105, 146};//32 is +15, 82 is +6.5, 110 is +7.5
    public double[] speeds = {0.0, 0.0, 0.0};
    // public double[] angles = {180, 127, 101};
    public double[] angles = {174, 145+7.5, 127+75, 95+7.5, 75+9};
    public int last = distances.length-1;

    public boolean isBlue = true;

    private DigitalInput driverLeftShoulder;

    @Override
    public void inputUpdate(Input source) {

    }

    @Override
    public void init() {
        VC = new VisionConsts();

        //same as update()
        resetState();
        driverLeftShoulder = (DigitalInput) WsInputs.DRIVER_LEFT_SHOULDER.get();
        driverLeftShoulder.addInputListener(this);
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        front.update();
        //back.update();
        SmartDashboard.putNumber("Vision getAngle", getAngle());
        SmartDashboard.putNumber("Vision distToTarget", front.distanceToTarget(isBlue));
        SmartDashboard.putNumber("Vision angleToRot", front.turnToTarget(isBlue, isStage()));
        SmartDashboard.putBoolean("Vision targetinView", front.TargetInView());
        // SmartDashboard.putNumber("GP X", back.tx);
        // SmartDashboard.putNumber("GP Y", back.ty);
        // SmartDashboard.putBoolean("GP tv", back.TargetInView());

    }

    @Override
    public void resetState() {
        front.update();
    }

    @Override
    public String getName() {
        return "Ws Vision";
    }

    /*
     * April Tag IDs:
     * - Stage: 11, 12, 13, 14, 15, 16
     * - Speaker: 3, 4, 7, 8
     * - Amp: 5, 6
     */

    public double getSpeed(){
        double inputDistance = front.distanceToTarget(isBlue);
        if (inputDistance < distances[0]) return speeds[0];
        for (int i = 1; i < distances.length; i++){
            if (inputDistance < distances[i]){
                return speeds[i-1] + (speeds[i]-speeds[i-1])*(inputDistance-distances[i-1])/(distances[i]-distances[i-1]);
            }
        }
        return speeds[last] + (inputDistance-distances[last])*(speeds[last]-speeds[last-1])/(distances[last]-distances[last-1]);
    }
    public double getAngle(){
        double inputDistance = front.distanceToTarget(isBlue);
        if (inputDistance < distances[0]) return angles[0];
        for (int i = 1; i < distances.length; i++){
            if (inputDistance < distances[i]){
                return angles[i-1] + (angles[i]-angles[i-1])*(inputDistance-distances[i-1])/(distances[i]-distances[i-1]);
            }
        }
        return angles[last] + (inputDistance-distances[last])*(angles[last]-angles[last-1])/(distances[last]-distances[last-1]);
    }
    public boolean isStage(){
        return front.tid == 13 || front.tid == 14;
    }
    public void setAlliance(boolean alliance){
        this.isBlue = alliance;
    }
     /**
     * @return true if on blue alliance
     */
    public boolean getAlliance() {
        return isBlue;
    }
    }