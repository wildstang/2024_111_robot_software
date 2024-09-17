package org.wildstang.year2024.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.swerve.SwerveDrive;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsVision implements Subsystem {

    public WsLL left = new WsLL("limelight-left");
    public WsLL right = new WsLL("limelight-right");
    public WsLL back = new WsLL("limelight-back");
    
    public SwerveDrive swerve;

    public VisionConsts VC;

    // public double[] distances = {41, 50, 70, 105, 146};
    // public double[] angles = {174, 145+7.5, 127+75, 95+7.5, 75+9};
    public double[] distances = { 48,  60,  95, 145,  192,  250, 251, 252};
    // public double[] angles = {205, 160, 130,  87, 75.5, 75.5, 205, 205};
    public double[] angles =    {205, 155+5.0, 125+5.0,  87+5.0, 75.5+4.0, 75.5+4.0, 75.5+4.0, 75.5+4.0};
    public int last = distances.length-1;

    public boolean isBlue = true;
    private double inputDistance = 0;

    private DigitalInput driverLeftShoulder;

    private Timer lastUpdate = new Timer();

    @Override
    public void inputUpdate(Input source) {

    }

    @Override
    public void initSubsystems() {
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
    }

    @Override
    public void init() {
        VC = new VisionConsts();

        

        //same as update()
        //resetState();
        driverLeftShoulder = (DigitalInput) WsInputs.DRIVER_LEFT_SHOULDER.get();
        driverLeftShoulder.addInputListener(this);
        lastUpdate.start();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {


        left.update(swerve.getFieldYaw());
        right.update(swerve.getFieldYaw());
        back.update(swerve.getFieldYaw());
        if (aprilTagsInView()) lastUpdate.reset();
        SmartDashboard.putNumber("Vision getAngle", getAngle());
        SmartDashboard.putNumber("Vision distToTarget", distanceToTarget(isBlue));
        SmartDashboard.putNumber("Vision angleToRot", turnToTarget(isBlue));
        SmartDashboard.putBoolean("Vision targetinView", aprilTagsInView());
        SmartDashboard.putNumber("GP X", back.tx);
        SmartDashboard.putNumber("GP Y", back.ty);
        SmartDashboard.putBoolean("GP tv", back.TargetInView());
        SmartDashboard.putNumber("Vision back tx", back.tx);
        SmartDashboard.putNumber("Vision back ty", back.ty);

    }

    @Override
    public void resetState() {
        left.update(swerve.getFieldYaw());
        right.update(swerve.getFieldYaw());
        back.update(swerve.getFieldYaw());
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

    public double getAngle(){
        if (isLeftBetter()) {
            inputDistance = left.distanceToTarget(isBlue);
        }  else {
            inputDistance = right.distanceToTarget(isBlue);
        }
        if (inputDistance < distances[0]) return angles[0];
        for (int i = 1; i < distances.length; i++){
            if (inputDistance < distances[i]){
                return angles[i-1] + (angles[i]-angles[i-1])*(inputDistance-distances[i-1])/(distances[i]-distances[i-1]);
            }
        }
        return angles[last] + (inputDistance-distances[last])*(angles[last]-angles[last-1])/(distances[last]-distances[last-1]);
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
    private boolean isLeftBetter(){
        if (left.TargetInView() && !right.TargetInView()) return true;
        if (!left.TargetInView() && right.TargetInView()) return false;
        if (left.getTagDist() < right.getTagDist()) return true;
        if (left.getTagDist() > right.getTagDist()) return false;
        if (left.seesAmp() && !right.seesAmp()) return true;
        if (!left.seesAmp() && right.seesAmp()) return false;
        if (left.getNumTags() > right.getNumTags()) return true;
        if (left.getNumTags() < right.getNumTags()) return false;
        return !this.isBlue;
    }
    public double turnToTarget(boolean isBlue){
        return isLeftBetter() ? left.turnToTarget(isBlue) : right.turnToTarget(isBlue);
    }
    public double distanceToTarget(boolean isBlue){
        return isLeftBetter() ? left.distanceToTarget(isBlue) : right.distanceToTarget(isBlue);
    }
    public boolean aprilTagsInView(){
        return left.TargetInView() || right.TargetInView();
    }
    public boolean canSeeSpeaker(boolean isBlue){
        return left.canSeeSpeaker(isBlue) || right.canSeeSpeaker(isBlue);
    }
    public boolean canSeeSpeaker(){
        return canSeeSpeaker(isBlue);
    }
    public boolean canSeeAmp(){
        return left.seesAmp() || right.seesAmp();
    }
    public double getXAdjust(){
        if (isLeftBetter()){
            return left.getAlignX(isBlue);
        } else return right.getAlignX(isBlue);
    }
    public double getYAdjust(){
        if (isLeftBetter()) return left.getAlignY(isBlue);
        else return right.getAlignY(isBlue);
    }
    public double getYValue(){
        if (isLeftBetter()) return left.blue3D[1];
        else return right.blue3D[1];
    }
    public double getUpdateTime(){
        return lastUpdate.get();
    }
    }