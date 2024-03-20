package org.wildstang.year2024.subsystems.targeting;

import java.util.List;
import java.util.OptionalDouble;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;
import org.photonvision.targeting.TargetCorner;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsPV {

    public PhotonCamera camera;
    public String cameraID;
    AprilTagFieldLayout aprilTagFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();
    PhotonTrackedTarget target;
    PhotonPipelineResult result;
    private Transform3d robotToCamera = new Transform3d(new Translation3d(0.5, 0.0, 0.5), new Rotation3d(0,0,0));

    private VisionConsts VC = new VisionConsts();

    public int tid = 0;
    public double tx = 0;
    public double ty = 0;
    public double tz = 0;
    public List<TargetCorner> targetCorners;
    public boolean tv = false;
    public Transform3d aprilTag = new Transform3d();
    public Pose3d estimatedPose = new Pose3d();

    //whether the cam detects ATs or Notes
    public boolean isAT;

    public WsPV(String cameraID, boolean isAprilTag){
        this.cameraID = cameraID;
        camera = new PhotonCamera(cameraID);
        isAT = isAprilTag;
    }
    public WsPV(String cameraID){
        this(cameraID, false);
    }

    public void update(){
        result = camera.getLatestResult();
        SmartDashboard.putBoolean("hasTargets", result.hasTargets());
        tv = result.hasTargets();
        if(tv) {
            target = result.getBestTarget();
            tx = target.getYaw();
            ty = target.getPitch();
            tz = target.getSkew();
            

            //only used if it is an AT cam
            if(isAT){
                tid = target.getFiducialId();
                aprilTag = target.getBestCameraToTarget();
                estimatedPose = PhotonUtils.estimateFieldToRobotAprilTag(aprilTag,
                    aprilTagFieldLayout.getTagPose(target.getFiducialId()).get(), robotToCamera);
            }

        }
        updateDashboard();
    }

    public void updateDashboard(){
        SmartDashboard.putBoolean(cameraID + " tv", tv);
        SmartDashboard.putNumber(cameraID + " tid", tid);
        SmartDashboard.putNumber(cameraID + " Y", ty);
        SmartDashboard.putNumber(cameraID + " X", tx);
        SmartDashboard.putNumber(cameraID + " Z", tz);
        SmartDashboard.putBoolean(cameraID + " isAT", isAT);
        SmartDashboard.putNumber(cameraID + "poseX", estimatedPose.getX()*VC.mToIn);
        SmartDashboard.putNumber(cameraID + "posey", estimatedPose.getY()*VC.mToIn);
        SmartDashboard.putNumber(cameraID +  "posez", estimatedPose.getZ());
    }

    /**
     * Toggles between Note and AT detection pipelines
     */
    public void togglePipeline() {
        isAT = !isAT;
        camera.setPipelineIndex(isAT ? VC.notePipelineIndex : VC.ATPipelineIndex);
    }
 
    /**
     * returns what to set rotLocked to
     */
    public double turnToTarget(boolean isBlue, boolean isStage){
        if (isBlue) return getDirection(estimatedPose.getX()*VC.mToIn - (isStage ? VC.blueTrussX: VC.blueSpeakerX),
            estimatedPose.getY()*VC.mToIn - (isStage ? VC.blueTrussY : VC.blueSpeakerY), isBlue);
        else return getDirection(estimatedPose.getX()*VC.mToIn - (isStage ? VC.redTrussX : VC.redSpeakerX),
            estimatedPose.getY()*VC.mToIn - (isStage ? VC.redTrussY : VC.redSpeakerY), isBlue);
    }

    /**
     * returns distance to selected alliances' center of speaker for lookup table use
     */
    public double distanceToTarget(boolean isBlue){
        if (isBlue) return Math.hypot(estimatedPose.getX()*VC.mToIn - VC.blueSpeakerX,
            estimatedPose.getY()*VC.mToIn - VC.blueSpeakerY);
        else return Math.hypot(estimatedPose.getX()*VC.mToIn - VC.redSpeakerX,
            estimatedPose.getY()*VC.mToIn - VC.redSpeakerY);
    }

    /**
     * input of X and Y in frc field coordinates, returns controller bearing degrees (aka what to plug into rotLocked)
     */
    private double getDirection(double x, double y, boolean isBlue) {
        double measurement = Math.toDegrees(Math.atan2(x,y));
        if (isBlue) measurement += 90;
        else measurement -= 90;
        if (measurement < 0) {
            measurement = 360 + measurement;
        }
        else if (measurement >= 360) {
            measurement = measurement - 360;
        }
        return measurement;
    }
    public boolean TargetInView(){
        return tv;
    }
    public double getPoseX(){
        return estimatedPose.getX();
    }
    public double getPoseY(){
        return estimatedPose.getY();
    }
    // Angle to turn to face note
    public OptionalDouble getNoteAngle() {
        // Make sure we can actually see a note, optional might not be the best way
        if (!result.hasTargets()) { return OptionalDouble.empty(); } 
        
        // Get angle to camera 
        double angleToCamera = Math.atan(VisionConsts.cameraPose.getY() / VisionConsts.cameraPose.getY());

        double C = angleToCamera + (Math.PI/2.0 - (VisionConsts.cameraPose.getRotation().getZ() + ty)); // Yaw
        // Distance from camera to note
        double a = (VisionConsts.cameraPose.getZ() / Math.tan(ty + VisionConsts.cameraPose.getRotation().getY())); // Pitch
        // Distance from camera to robot center
        double b = (Math.hypot(VisionConsts.cameraPose.getX(), VisionConsts.cameraPose.getY()));
        // Distance to Note using law of Cosines
        double c = Math.sqrt((a * a) + (b * b) - 2 * a * b * Math.cos(C));
        double A = Math.asin((a * Math.sin(C)) / c);

        // tx as if the camera was in the center of the robot facing forward
        return OptionalDouble.of((Math.PI/2.0 - (angleToCamera + A))*180/Math.PI);
    }


    public double analyzeSkew_speed() {
        double angleTarget = Math.abs(tz);
        
        if (angleTarget >= 160 && angleTarget <= 180) {
            return 1.0;
        } else if (angleTarget >= 110 && angleTarget < 160) {
            return 0.7;
        } else  {
            return 0;
        }
        
        
    }
    public int analyzeSkew_angle() {
        int angleTarget = Math.abs((int)tz); 
        
        if (angleTarget > 180) {
            angleTarget = 180; 
        } else if (angleTarget < 100) {
            angleTarget = 100; 
        }
        
        return angleTarget; 
    }
}
