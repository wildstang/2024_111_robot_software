package org.wildstang.year2024.subsystems.swerve;

import com.ctre.phoenix.sensors.Pigeon2;

import java.util.Arrays;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.CANConstants;
//import org.wildstang.year2024.robot.KalmanFilterJenny;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.targeting.VisionConsts;
import org.wildstang.year2024.subsystems.targeting.WsVision;
import org.wildstang.year2024.subsystems.theFolder.theClass;

import edu.wpi.first.math.estimator.KalmanFilter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**Class: SwerveDrive
 * inputs: driver left joystick x/y, right joystick x, right trigger, right bumper, select, face buttons all, gyro
 * outputs: four swerveModule objects
 * description: controls a swerve drive for four swerveModules through autonomous and teleoperated control
 */
public class SwerveDrive extends SwerveDriveTemplate {

    private AnalogInput leftStickX;//translation joystick x
    private AnalogInput leftStickY;//translation joystick y
    private AnalogInput rightStickX;//rot joystick
    private AnalogInput rightTrigger;//intake, score when aiming
    private AnalogInput leftTrigger;//scoring to speaker
    private DigitalInput rightBumper;//slowdown
    private DigitalInput leftBumper;//lift up to amp
    private DigitalInput select;//gyro reset
    private DigitalInput start;//endgame toggle
    private DigitalInput faceUp;//rotation lock 0 degrees
    private DigitalInput faceRight;//rotation lock 90 degrees
    private DigitalInput faceLeft;//rotation lock 270 degrees
    private DigitalInput faceDown;//rotation lock 180 degrees
    private DigitalInput dpadLeft;//defense mode
    private DigitalInput dpadRight;

    private double xPower;
    private double yPower;
    private double rotSpeed;
    private double thrustValue;
    private boolean rotLocked;
    private boolean isSnake;

    /**Direction to face */
    private double rotTarget;

    private boolean isVision = false;
    private boolean autoAlign = false;
    private boolean isFeedVision = false;
    private boolean isCurrentLow = false;
    private boolean isOverride = false;
    private boolean isAutoObject = false;
    private boolean isFeedModeUpdate = false;
    private double yObject = 0;
    private double feedOffset = 0;
    
    private final double mToIn = 39.37;

    //private final AHRS gyro = new AHRS(SerialPort.Port.kUSB);
    public final Pigeon2 gyro = new Pigeon2(CANConstants.GYRO);
    public SwerveModule[] modules;
    private SwerveSignal swerveSignal;
    private WsSwerveHelper swerveHelper = new WsSwerveHelper();
    public SwerveDriveOdometry odometry;
    StructPublisher<Pose2d> publisher;
    public ChassisSpeeds speeds;

    private WsVision vision;
    private theClass intake;
    //private KalmanFilterJenny kf;

    public enum driveType {TELEOP, AUTO, CROSS, OBJECT};
    public driveType driveState;

    @Override
    public void inputUpdate(Input source) {

        //determine if we are in cross or teleop
        // if (driveState != driveType.AUTO && dpadLeft.getValue()) {
        //     driveState = driveType.CROSS;
        //     for (int i = 0; i < modules.length; i++) {
        //         modules[i].setDriveBrake(true);
        //     }
        //     this.swerveSignal = new SwerveSignal(new double[]{0, 0, 0, 0 }, swerveHelper.setCross().getAngles());
        // }
        // else if (driveState == driveType.CROSS || driveState == driveType.AUTO) {
        //     driveState = driveType.TELEOP;
        // }
        if (driveState == driveType.AUTO) driveState = driveType.TELEOP;

        //toggles feeding vs scoring notes
        if (source == start && start.getValue()){
            isFeedVision = !isFeedVision;
        }

        //get x and y speeds
        xPower = swerveHelper.scaleDeadband(leftStickX.getValue(), DriveConstants.DEADBAND);
        yPower = swerveHelper.scaleDeadband(leftStickY.getValue(), DriveConstants.DEADBAND);
        
        
        //reset gyro
        if (source == select && select.getValue()) {
            gyro.setYaw(0.0);
            odometry.resetPosition(new Rotation2d(), odoPosition(), 
                new Pose2d(new Translation2d(odometry.getPoseMeters().getX(),odometry.getPoseMeters().getY()),new Rotation2d()));
            if (rotLocked) rotTarget = 0.0;
        }

        //determine snake or pid locks
        // if (start.getValue() && (Math.abs(xSpeed) > 0.1 || Math.abs(ySpeed) > 0.1)) {
        //     rotLocked = true;
        //     isSnake = true;
        //     rotTarget = swerveHelper.getDirection(xSpeed, ySpeed);
        // }
        // else {
        //     isSnake = false;
        // }
        if (source == faceUp && faceUp.getValue()){
            if (faceLeft.getValue()) rotTarget = 300.0;
            else if (faceRight.getValue()) rotTarget = 60.0;
            else  rotTarget = 0.0;
            rotLocked = true;
        }
        if (source == faceLeft && faceLeft.getValue()){
            if (faceUp.getValue()) rotTarget = 300.0;
            else if (faceDown.getValue()) rotTarget = 240.0;
            else rotTarget = 270.0;
            rotLocked = true;
        }
        if (source == faceDown && faceDown.getValue()){
            if (faceLeft.getValue()) rotTarget = 240.0;
            else if (faceRight.getValue()) rotTarget = 120.0;
            else rotTarget = 180.0;
            rotLocked = true;
        }
        if (source == faceRight && faceRight.getValue()){
            if (faceUp.getValue()) rotTarget = 60.0;
            else if (faceDown.getValue()) rotTarget = 120.0;
            else rotTarget = 90.0;
            rotLocked = true;
        }
        //auto gamepiece pickup when intaking and hitting a face button
        //rotates the robot towards the piece and forces the joystick to point at the piece
        if (intake.isIntaking() && (faceDown.getValue() || faceLeft.getValue() || faceRight.getValue() || faceUp.getValue())) {
            driveState = driveType.OBJECT;
            rotLocked = true;
            rotTarget = getGyroAngle();
        } else driveState = driveType.TELEOP;

        //get rotational joystick
        rotSpeed = rightStickX.getValue()*Math.abs(rightStickX.getValue());
        rotSpeed = swerveHelper.scaleDeadband(rotSpeed, DriveConstants.DEADBAND);
        // if (rotSpeed == 0 && rotLocked == false){
        //     if (Math.abs(getGyroAngle() - rotTarget) < 1.0) rotLocked = true;
        //     rotTarget = getGyroAngle();
        // }
        //if the rotational joystick is being used, the robot should not be auto tracking heading
        if (rotSpeed != 0) {
            rotLocked = false;
        }
        //if aiming in feed mode
        if (leftTrigger.getValue() > 0.15 && isFeedVision){
            rotLocked = true;
            rotTarget = vision.getFeedRotation(feedOffset);//update below as well
            isFeedModeUpdate = true;
            isVision = false;
        //else if aiming while in scoring mode
        }else if (leftTrigger.getValue() > 0.15 && vision.aprilTagsInView()){
            rotLocked = true;
            isVision = true;
            isFeedModeUpdate = false;
            xPower *= 0.7;
            yPower *= 0.7;
            rotSpeed *= 0.7;
        } else {
            isVision = false;
            isFeedModeUpdate = false;
        }
        
        //assign thrust - no thrust this year, low cg robot means max speed all the time
        // thrustValue = 1 - DriveConstants.DRIVE_THRUST + DriveConstants.DRIVE_THRUST * Math.abs(rightTrigger.getValue());
        // xSpeed *= thrustValue;
        // ySpeed *= thrustValue;
        // rotSpeed *= thrustValue;

        //slow down while amp scoring
        //don't slow rotation in case a note gets stuck
        if (leftBumper.getValue()){
            xPower *= 0.25;
            yPower *= 0.35;
            //rotSpeed *= 0.25;
        }

        //lower drive current while aiming so we don't brown out before scoring
        if (leftTrigger.getValue() > 0.15 && !isCurrentLow){
            for (int i = 0; i < 4; i++){
                modules[i].tempDriveCurrent(DriveConstants.DRIVE_CURRENT_LIMIT - 20);
            }
            isCurrentLow = true;
        } else if (isCurrentLow && leftTrigger.getValue() < 0.15){
            for (int i = 0; i < 4; i++){
                modules[i].tempDriveCurrent(DriveConstants.DRIVE_CURRENT_LIMIT);
            }
            isCurrentLow = false;
        }

        if (source == dpadLeft && dpadLeft.getValue()) feedOffset-=2;
        if (source == dpadRight && dpadRight.getValue()) feedOffset+=2;
    }
 
    @Override
    public void init() {
        publisher = NetworkTableInstance.getDefault()
        .getStructTopic("MyPose", Pose2d.struct).publish();

        initInputs();
        initOutputs();
        resetState();
        gyro.setYaw(0.0);

        //kf = new KalmanFilterJenny();
        
    }

    public void initSubsystems() {
        vision = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);
        intake = (theClass) Core.getSubsystemManager().getSubsystem(WsSubsystems.THECLASS);
    }

    public void initInputs() {

        leftStickX = (AnalogInput) WsInputs.DRIVER_LEFT_JOYSTICK_X.get();
        leftStickX.addInputListener(this);
        leftStickY = (AnalogInput) WsInputs.DRIVER_LEFT_JOYSTICK_Y.get();
        leftStickY.addInputListener(this);
        rightStickX = (AnalogInput) WsInputs.DRIVER_RIGHT_JOYSTICK_X.get();
        rightStickX.addInputListener(this);
        rightTrigger = (AnalogInput) WsInputs.DRIVER_RIGHT_TRIGGER.get();
        rightTrigger.addInputListener(this);
        leftTrigger = (AnalogInput) WsInputs.DRIVER_LEFT_TRIGGER.get();
        leftTrigger.addInputListener(this);
        rightBumper = (DigitalInput) WsInputs.DRIVER_RIGHT_SHOULDER.get();
        rightBumper.addInputListener(this);
        leftBumper = (DigitalInput) WsInputs.DRIVER_LEFT_SHOULDER.get();
        leftBumper.addInputListener(this);
        select = (DigitalInput) WsInputs.DRIVER_SELECT.get();
        select.addInputListener(this);
        start = (DigitalInput) WsInputs.DRIVER_START.get();
        start.addInputListener(this);
        faceUp = (DigitalInput) WsInputs.DRIVER_FACE_UP.get();
        faceUp.addInputListener(this);
        faceLeft = (DigitalInput) WsInputs.DRIVER_FACE_LEFT.get();
        faceLeft.addInputListener(this);
        faceRight = (DigitalInput) WsInputs.DRIVER_FACE_RIGHT.get();
        faceRight.addInputListener(this);
        faceDown = (DigitalInput) WsInputs.DRIVER_FACE_DOWN.get();
        faceDown.addInputListener(this);
        dpadLeft = (DigitalInput) WsInputs.DRIVER_DPAD_LEFT.get();
        dpadLeft.addInputListener(this);
        dpadRight = (DigitalInput) WsInputs.DRIVER_DPAD_RIGHT.get();
        dpadRight.addInputListener(this);
    }

    public void initOutputs() {
        //create four swerve modules
        modules = new SwerveModule[]{
            new SwerveModule((WsSpark) WsOutputs.DRIVE1.get(), 
                (WsSpark) WsOutputs.ANGLE1.get(), DriveConstants.FRONT_LEFT_OFFSET),
            new SwerveModule((WsSpark) WsOutputs.DRIVE2.get(), 
                (WsSpark) WsOutputs.ANGLE2.get(), DriveConstants.FRONT_RIGHT_OFFSET),
            new SwerveModule((WsSpark) WsOutputs.DRIVE3.get(), 
                (WsSpark) WsOutputs.ANGLE3.get(), DriveConstants.REAR_LEFT_OFFSET),
            new SwerveModule((WsSpark) WsOutputs.DRIVE4.get(), 
                (WsSpark) WsOutputs.ANGLE4.get(), DriveConstants.REAR_RIGHT_OFFSET)
        };
        //create default swerveSignal
        swerveSignal = new SwerveSignal(new double[]{0.0, 0.0, 0.0, 0.0}, new double[]{0.0, 0.0, 0.0, 0.0});
        odometry = new SwerveDriveOdometry(new SwerveDriveKinematics(new Translation2d(0.2794, 0.33), new Translation2d(0.2794, -0.33),
            new Translation2d(-0.2794, 0.33), new Translation2d(-0.2794, -0.33)), odoAngle(), odoPosition(), new Pose2d());
    }
    
    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        odometry.update(odoAngle(), odoPosition());
        SmartDashboard.putNumber("Drive Speed", robotSpeed());
        if (robotSpeed() < 0.5) {
            setOdo(new Pose2d(vision.getCameraPose(), odoAngle()));
        }
        vision.setOdometry(odometry.getPoseMeters().getTranslation());
        publisher.set(odometry.getPoseMeters());
        //kf.kfPeriodic(); //calling periodic kalman filter method

        if (driveState == driveType.CROSS) {
            //set to cross - done in inputupdate
            this.swerveSignal = swerveHelper.setCross();
            drive();
        }
        if (driveState == driveType.TELEOP) {
            if (rotLocked){
                //get rotation of the robot to aim while feeding
                if (isFeedModeUpdate) rotTarget = vision.getFeedRotation(feedOffset);
                //point at target to score in speaker
                else if (isVision) rotTarget = vision.turnToTarget(VisionConsts.speaker);
                rotSpeed = swerveHelper.getRotControl(rotTarget, getGyroAngle());
                if (Math.abs(rotTarget - getGyroAngle()) < 1.0) rotSpeed = 0;
                // if (isSnake) {
                //     if (Math.abs(rotSpeed) < 0.05) {
                //         rotSpeed = 0;
                //     }
                //     else {
                //         rotSpeed *= 4;
                //         if (Math.abs(rotSpeed) > 1) rotSpeed = 1.0 * Math.signum(rotSpeed);
                //     }
                // } 
            }
            this.swerveSignal = swerveHelper.setDrive(xPower, yPower, rotSpeed, getGyroAngle());
            SmartDashboard.putNumber("FR signal", swerveSignal.getSpeed(0));
            drive();
        }
        if (driveState == driveType.AUTO) {
            //auto Align to reset robot to a specific X/Y location on the field, in case of any serious collisions
            if (autoAlign){
                this.swerveSignal = swerveHelper.setDrive(vision.getYAdjust(VisionConsts.shot),
                     vision.getXAdjust(VisionConsts.shot), swerveHelper.getRotControl(180, getGyroAngle()), getGyroAngle());
            //shooting at speaker during auto
            } else if (isVision && vision.aprilTagsInView()) {
                rotTarget = vision.turnToTarget(VisionConsts.speaker);
                rotSpeed = swerveHelper.getRotControl(rotTarget, getGyroAngle());
                this.swerveSignal = swerveHelper.setDrive(xPower, yPower, rotSpeed, getGyroAngle());
            //picking up a game piece with vision assistance in auto
            } else if (isAutoObject && !intake.hasNote() && vision.back.TargetInView()){
                yObject = swerveHelper.adjustObjectAuto(vision.back.ty, xPower, yPower);
                if (yObject > 0) this.swerveSignal = swerveHelper.setObject(0, yObject, vision.back.tx);
                else this.swerveSignal = swerveHelper.setObject(xPower, yPower, vision.back.tx);
            } else {
                //get controller generated rotation value
                rotSpeed = swerveHelper.getAutoRotation(rotTarget, getGyroAngle());
                this.swerveSignal = swerveHelper.setDrive(xPower, yPower, rotSpeed, getGyroAngle());
            }
            
            // Pre generated power values in set auto
            drive();        
        } 
        if (driveState == driveType.OBJECT) {
            //for teleop game piece pickup
            if (vision.back.TargetInView() && !intake.hasNote()) {
                //force the controller input to point at the gamepiece, only thing driver controls is speed of the robot
                this.swerveSignal = swerveHelper.setObject(xPower, yPower, vision.back.tx);
                drive();
            }
            else {
                this.swerveSignal = swerveHelper.setDrive(xPower, yPower, rotSpeed, getGyroAngle());
                drive();
            }
        }
        SmartDashboard.putNumber("Gyro Reading", getGyroAngle());
        SmartDashboard.putNumber("X Power", xPower);
        SmartDashboard.putNumber("Y Power", yPower);
        SmartDashboard.putNumber("rotSpeed", rotSpeed);
        SmartDashboard.putString("Drive mode", driveState.toString());
        SmartDashboard.putBoolean("rotLocked", rotLocked);
        SmartDashboard.putNumber("Rotation target", rotTarget);
        SmartDashboard.putBoolean("Object Override", isOverride);
        SmartDashboard.putNumber("Odo X", odometry.getPoseMeters().getX());
        SmartDashboard.putNumber("Odo Y", odometry.getPoseMeters().getY());
        SmartDashboard.putNumber("Yaw", gyro.getYaw());
        SmartDashboard.putNumber("Roll", gyro.getRoll());
        SmartDashboard.putNumber("Pitch", gyro.getPitch());
        short[] shortAcceleration = {0,0,0};
        gyro.getBiasedAccelerometer(shortAcceleration);
        double[] acceleration = new double[3];
        for (int i=0;i<3;i++) {
            acceleration[i] = shortAcceleration[i];
        }
        SmartDashboard.putNumberArray("Accelerometer", acceleration);
        SmartDashboard.putBoolean("Alliance Color", DriverStation.getAlliance().isPresent());
        SmartDashboard.putNumber("Feed offset", feedOffset);
    }
    
    @Override
    public void resetState() {
        xPower = 0;
        yPower = 0;
        rotSpeed = 0;
        rotLocked = false;
        rotTarget = 0.0;
        isOverride = false;
        feedOffset = 0;

        isSnake = false;
        setToTeleop();
    }

    @Override
    public String getName() {
        return "Swerve Drive";
    }

    /** sets the drive to teleop/cross, and sets drive motors to coast */
    public void setToTeleop() {
        driveState = driveType.TELEOP;
        for (int i = 0; i < modules.length; i++) {
            modules[i].setDriveBrake(true);
        }
        rotSpeed = 0;
        xPower = 0;
        yPower = 0;
        rotLocked = false;
    }

    /**sets the drive to autonomous */
    public void setToAuto() {
        driveState = driveType.AUTO;
        for (int i = 0; i < modules.length; i++) {
            modules[i].setDriveBrake(true);
        }
    }

    /** Sets the drive state = OBJECT */
    public void setToObject() {
        driveState = driveType.AUTO;
        xPower = 0;
        yPower = 0;
    }

    /**drives the robot at the current swerveSignal, and displays information for each swerve module */
    private void drive() {
        if (driveState == driveType.CROSS) {
            for (int i = 0; i < modules.length; i++) {
                modules[i].runCross(swerveSignal.getSpeed(i), swerveSignal.getAngle(i));
                modules[i].displayNumbers(DriveConstants.POD_NAMES[i]);
            }
        }
        else {
            for (int i = 0; i < modules.length; i++) {
                modules[i].run(swerveSignal.getSpeed(i), swerveSignal.getAngle(i));
                modules[i].displayNumbers(DriveConstants.POD_NAMES[i]);
            }
        }
    }

    /**sets autonomous values from the path data file in field relative */
    public void setAutoValues(double xVelocity, double yVelocity, double xOffset, double yOffset) {
        SmartDashboard.putNumber("Offset X Power", xOffset * DriveConstants.TRANSLATION_P);
        SmartDashboard.putNumber("Offset Y Power", yOffset * DriveConstants.TRANSLATION_P);
        // accel of 0 because currently not using acceleration for power since
        xPower = swerveHelper.getAutoPower(xVelocity, 0) + (xOffset) * DriveConstants.TRANSLATION_P;
        yPower = swerveHelper.getAutoPower(yVelocity, 0) + (yOffset) * DriveConstants.TRANSLATION_P;
    }

    /**sets the autonomous heading controller to a new target */
    public void setAutoHeading(double headingTarget) {
        rotTarget = headingTarget;
    }

    /**
     * Resets the gyro, and sets it the input number of degrees
     * Used for starting the match at a non-0 angle
     * @param degrees the current value the gyro should read
     */
    public void setGyro(double degrees) {
        resetState();
        setToAuto();

        // Make degrees clockwise
        gyro.setYaw((360-degrees)%360);
    }

    public double getGyroAngle() {
        return (360 - gyro.getYaw()+360)%360;
    }  

    public double getFieldYaw(){
        return Math.toRadians(360-getGyroAngle());
    }

    // Magnitude of robot speed for vision confidence
    public double robotSpeed() {
        speeds = DriveConstants.kinematics.toChassisSpeeds(new SwerveModuleState[]
        {modules[0].moduleState(), modules[1].moduleState(), modules[2].moduleState(), modules[3].moduleState()});
        return Math.sqrt(speeds.vxMetersPerSecond * speeds.vxMetersPerSecond + speeds.vyMetersPerSecond
         * speeds.vyMetersPerSecond + speeds.omegaRadiansPerSecond);
    }
    public Rotation2d odoAngle(){
        return new Rotation2d(getFieldYaw());
    }
    public SwerveModulePosition[] odoPosition(){
        return new SwerveModulePosition[]{modules[0].odoPosition(), modules[1].odoPosition(), modules[2].odoPosition(), modules[3].odoPosition()};
    }
    public void setOdo(Pose2d starting){
        this.odometry = new SwerveDriveOdometry(DriveConstants.kinematics, odoAngle(), odoPosition(), starting);
    }
    public Pose2d returnPose(){
        return odometry.getPoseMeters();
    }
    public double getRotTarget(){
        return rotTarget;
    }
    public void setVisionAuto(boolean isOn){
        this.isVision = isOn;
    }
    public void setAutoObject(boolean isOn){
        this.isAutoObject = isOn;
    }
    public void setAutoAlign(boolean isOn){
        this.autoAlign = isOn;
    }
}
