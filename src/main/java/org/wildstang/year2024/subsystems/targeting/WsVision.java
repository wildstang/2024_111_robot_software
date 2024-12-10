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

    public double[] distances = { 48,  60,  95, 145,  192,  250, 251, 252};
    public double[] angles =    {205, 155+5.0, 125+5.0,  87+5.0, 75.5+4.0, 75.5+4.0, 75.5+4.0, 75.5+4.0};
    public int last = distances.length-1;

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
        SmartDashboard.putNumber("Vision distToTarget", distanceToTarget());
        SmartDashboard.putNumber("Vision angleToRot", turnToTarget());
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

     /**
      * Get angle of shooter based on distance to speaker
      */
    public double getAngle(){
        if (isLeftBetter()) {
            inputDistance = left.distanceToTarget();
        }  else {
            inputDistance = right.distanceToTarget();
        }
        if (inputDistance < distances[0]) return angles[0];
        for (int i = 1; i < distances.length; i++){
            if (inputDistance < distances[i]){
                return angles[i-1] + (angles[i]-angles[i-1])*(inputDistance-distances[i-1])/(distances[i]-distances[i-1]);
            }
        }
        return angles[last] + (inputDistance-distances[last])*(angles[last]-angles[last-1])/(distances[last]-distances[last-1]);
    }

    /**
     *  determine whether to take data from left or right camera
     */
    public boolean isLeftBetter(){
        if (left.TargetInView() && !right.TargetInView()) return true;
        if (!left.TargetInView() && right.TargetInView()) return false;
        if (left.getTagDist() < right.getTagDist()) return true;
        if (left.getTagDist() > right.getTagDist()) return false;
        if (left.seesAmp() && !right.seesAmp()) return true;
        if (!left.seesAmp() && right.seesAmp()) return false;
        if (left.getNumTags() > right.getNumTags()) return true;
        if (left.getNumTags() < right.getNumTags()) return false;
        return !Core.isBlue();
    }
    /**
     *  gets bearing degrees for what the robot's heading should be to be pointing at the speaker
     */
    public double turnToTarget(){
        return isLeftBetter() ? left.turnToTarget() : right.turnToTarget();
    }
    /**
     * distance to center of speaker for use in finding shooter angle
     */
    private double distanceToTarget(){
        return isLeftBetter() ? left.distanceToTarget() : right.distanceToTarget();
    }
    /**
     * can either camera see an April Tag
     */ 
    public boolean aprilTagsInView(){
        return left.TargetInView() || right.TargetInView();
    }
    /*
     * can either camera see the speaker, specifying which alliance
     */
    public boolean canSeeSpeaker(){
        return left.canSeeSpeaker() || right.canSeeSpeaker();
    }
    /*
     * can the robot see an amp april tag
     */
    public boolean canSeeAmp(){
        return left.seesAmp() || right.seesAmp();
    }
    /*
     * get the control value to use for driving the robot to a specific X on the field
     */
    public double getXAdjust(){
        if (isLeftBetter()){
            return left.getAlignX();
        } else return right.getAlignX();
    }
    /*
     * get the control value to use for driving the robot to a specific y on the field
     */
    public double getYAdjust(){
        if (isLeftBetter()) return left.getAlignY();
        else return right.getAlignY();
    }
    /*
     * get Y value from cameras to use for determining the direction of the robot
     * to feed a note to the desired location
     */
    public double getYValue(){
        if (isLeftBetter()) return left.blue3D[1];
        else return right.blue3D[1];
    }
    public double getUpdateTime(){
        return lastUpdate.get();
    }
    }