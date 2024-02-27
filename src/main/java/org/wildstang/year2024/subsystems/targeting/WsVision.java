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

    public WsPV front = new WsPV("photonvision", true);
    public WsPV back = new WsPV("object detection", false);

    public VisionConsts VC;

    ShuffleboardTab tab = Shuffleboard.getTab("Tab");

    public double[] distances = {0.0, 0.0, 0.0};
    public double[] speeds = {0.0, 0.0, 0.0};
    public double[] angles = {0.0, 0.0, 0.0};
    public int last = distances.length-1;

    public boolean isBlue;

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

    /*
     * April Tag IDs:
     * - Stage: 11, 12, 13, 14, 15, 16
     * - Speaker: 3, 4, 7, 8
     * - Amp: 5, 6
     */

    /**
     * can back see stage AT?
     */
    public boolean backSeesStage(){

        if (VC.stageATs.contains(back.tid)) {
            return true;
        }

        return false;

    }


    /**
     * can front see speaker AT?
     */
    public boolean frontSeesSpeaker(){

        if (VC.speakerATs.contains(back.tid)) {
            return true;
        }

        return false;

    }

    /**
     * can back see amp AT?
     */
    public boolean backSeesAmp(){

        if (VC.ampATs.contains(back.tid)) {
            return true;
        }

        return false;

    }

    /**
     * can back see gamepiece?
     */
    public boolean backSeesNote(){

        if (back.isAT == false && back.result.hasTargets()) {

            return true;

        }

        return false;

    }

    //tx for front
    public double getFrontTx(){

        return front.tx;

    }
    //ty for front
    public double getFrontTy(){

        return front.ty;

    }

    //tx for gamepiece (back)
    public double getNoteTx(){

        if (back.isAT == false) {

            return back.tx;

        }

        return 0.0;

    }

    //3D values for amp (back)
    public Pose3d getBackPose() {

        return back.estimatedPose;

    }

    //field angle for stage AT we are seeing (back)
    public double getFieldAngleForStage() {

        if (backSeesStage()) {

            if (back.tid == 13 || back.tid == 14) {

                return 0.0;

            } else if (back.tid == 15 || back.tid == 11) {

                return 240.0;

            } else if (back.tid == 12 || back.tid == 16) {

                return 120.0;

            }

            return 0.0;

        }

        return 0.0;
    }

    //Stuart's stuff that I don't understand:
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
}