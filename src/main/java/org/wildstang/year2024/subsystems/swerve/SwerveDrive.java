package org.wildstang.year2024.subsystems.swerve;

import com.ctre.phoenix.sensors.Pigeon2;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.CANConstants;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.targeting.WsVision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
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
    private boolean isFieldCentric;

    /**Direction to face */
    private double rotTarget;

    private double shootOffset;
    private boolean autoOverride;
    private boolean isBlue = true;
    private boolean autoTag = false;
    private boolean isVision = false;
    private boolean isCurrentLow = false;
    
    private final double mToIn = 39.37;

    //private final AHRS gyro = new AHRS(SerialPort.Port.kUSB);
    private final Pigeon2 gyro = new Pigeon2(CANConstants.GYRO);
    public SwerveModule[] modules;
    private SwerveSignal swerveSignal;
    private WsSwerveHelper swerveHelper = new WsSwerveHelper();
    private SwerveDriveOdometry odometry;
    private Timer autoTimer = new Timer();

    private WsVision vision;

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

        //get x and y speeds
        xPower = swerveHelper.scaleDeadband(leftStickX.getValue(), DriveConstants.DEADBAND);
        yPower = swerveHelper.scaleDeadband(leftStickY.getValue(), DriveConstants.DEADBAND);
        
        //reset gyro
        if (source == select && select.getValue()) {
            gyro.setYaw(0.0);
            if (DriverStation.getAlliance().isPresent()){
                isBlue = DriverStation.getAlliance().get()== Alliance.Blue;
            }
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

        //get rotational joystick
        rotSpeed = rightStickX.getValue()*Math.abs(rightStickX.getValue());
        rotSpeed = swerveHelper.scaleDeadband(rotSpeed, DriveConstants.DEADBAND);
        //if the rotational joystick is being used, the robot should not be auto tracking heading
        if (rotSpeed != 0) {
            rotLocked = false;
        }
        if (leftTrigger.getValue() > 0.15 && vision.front.TargetInView()){
            rotLocked = true;
            //rotTarget = vision.front.turnToTarget(isBlue);
            isVision = true;
            xPower *= 0.7;
            yPower *= 0.7;
            rotSpeed *= 0.7;
        } else isVision = false;
        
        //assign thrust
        // thrustValue = 1 - DriveConstants.DRIVE_THRUST + DriveConstants.DRIVE_THRUST * Math.abs(rightTrigger.getValue());
        // xSpeed *= thrustValue;
        // ySpeed *= thrustValue;
        // rotSpeed *= thrustValue;
        if (leftBumper.getValue()){
            xPower *= 0.25;
            yPower *= 0.25;
            rotSpeed *= 0.25;
        }
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
        if (source == dpadLeft && dpadLeft.getValue()) shootOffset -= 2.5;
        if (source == dpadRight && dpadRight.getValue()) shootOffset += 2.5;

    }
 
    @Override
    public void init() {
        initInputs();
        initOutputs();
        resetState();
        gyro.setYaw(0.0);
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
        vision = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);
        odometry = new SwerveDriveOdometry(new SwerveDriveKinematics(new Translation2d(0.2794, 0.33), new Translation2d(0.2794, -0.33),
            new Translation2d(-0.2794, 0.33), new Translation2d(-0.2794, -0.33)), odoAngle(), odoPosition(), new Pose2d());
    }
    
    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        odometry.update(odoAngle(), odoPosition());

        if (driveState == driveType.CROSS) {
            //set to cross - done in inputupdate
            this.swerveSignal = swerveHelper.setCross();
            drive();
        }
        if (driveState == driveType.TELEOP) {
            if (rotLocked){
                //if rotation tracking, replace rotational joystick value with controller generated one
                if (isVision) rotTarget = shootOffset + vision.front.turnToTarget(isBlue, vision.isStage());
                rotSpeed = swerveHelper.getRotControl(rotTarget, getGyroAngle());
                if (isSnake) {
                    if (Math.abs(rotSpeed) < 0.05) {
                        rotSpeed = 0;
                    }
                    else {
                        rotSpeed *= 4;
                        if (Math.abs(rotSpeed) > 1) rotSpeed = 1.0 * Math.signum(rotSpeed);
                    }
                    
                } 
            }
            this.swerveSignal = swerveHelper.setDrive(xPower, yPower, rotSpeed, getGyroAngle());
            SmartDashboard.putNumber("FR signal", swerveSignal.getSpeed(0));
            drive();
        }
        if (driveState == driveType.AUTO) {
            if (isVision && vision.front.TargetInView()) {
                rotTarget = shootOffset + vision.front.turnToTarget(isBlue, vision.isStage());
                rotSpeed = swerveHelper.getRotControl(rotTarget, getGyroAngle());
            } else {
                //get controller generated rotation value
                // Capped at .2
                rotSpeed = Math.max(-0.2, Math.min(0.2, swerveHelper.getRotControl(rotTarget, getGyroAngle())));
            }
            
            // Pre generated power values in set auto
            this.swerveSignal = swerveHelper.setDrive(xPower, yPower, rotSpeed, getGyroAngle());
            drive();        
        } 
        if (driveState == driveType.OBJECT) {
            if (vision.back.TargetInView()) {
                rotSpeed = swerveHelper.getRotControl(0, vision.back.getNoteAngle());
                this.swerveSignal = swerveHelper.setDrive(0, Math.max(yPower, vision.back.getNoteDistance() * DriveConstants.TRANSLATION_P) , rotSpeed, -vision.back.getNoteAngle());
            }
        }
        SmartDashboard.putNumber("Gyro Reading", getGyroAngle());
        SmartDashboard.putNumber("X Power", xPower);
        SmartDashboard.putNumber("Y Power", yPower);
        SmartDashboard.putNumber("rotSpeed", rotSpeed);
        SmartDashboard.putString("Drive mode", driveState.toString());
        SmartDashboard.putBoolean("rotLocked", rotLocked);
        SmartDashboard.putNumber("Rotation target", rotTarget);
        SmartDashboard.putNumber("Odo X", odometry.getPoseMeters().getX());
        SmartDashboard.putNumber("Odo Y", odometry.getPoseMeters().getY());
        SmartDashboard.putNumber("arm rotation offset", shootOffset);
        SmartDashboard.putBoolean("Alliance Color", DriverStation.getAlliance().isPresent());
    }
    
    @Override
    public void resetState() {
        xPower = 0;
        yPower = 0;
        rotSpeed = 0;
        setToTeleop();
        rotLocked = false;
        rotTarget = 0.0;
        autoOverride = false;
        autoTag = false;

        isFieldCentric = true;
        isSnake = false;
        shootOffset = 0;
    }

    @Override
    public String getName() {
        return "Swerve Drive";
    }

    /** resets the drive encoders on each module */
    public void resetDriveEncoders() {
        for (int i = 0; i < modules.length; i++) {
            modules[i].resetDriveEncoders();
        }
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

    /**sets autonomous values from the path data file in alliance relative */
    public void setAutoValues(double xVelocity, double yVelocity, double xOffset, double yOffset) {
        SmartDashboard.putNumber("Auto Velocity X", xVelocity);
        SmartDashboard.putNumber("Auto Velocity Y", yVelocity);
        // accel of 0 because currently not using acceleration for power since 
        xPower = swerveHelper.getAutoPower(xVelocity, 0) + xOffset * DriveConstants.TRANSLATION_P;
        yPower = swerveHelper.getAutoPower(yVelocity, 0) + yOffset * DriveConstants.TRANSLATION_P;
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
        if (!isFieldCentric) return 0;
        return (359.99 - gyro.getYaw()+360)%360;
    }  
    public Rotation2d odoAngle(){
        return new Rotation2d(Math.toRadians(360-getGyroAngle()));
    }
    public SwerveModulePosition[] odoPosition(){
        return new SwerveModulePosition[]{modules[0].odoPosition(), modules[1].odoPosition(), modules[2].odoPosition(), modules[3].odoPosition()};
    }
    public void setOdo(Pose2d starting){
        this.odometry.resetPosition(odoAngle(), odoPosition(), starting);
        autoTimer.start();
    }
    public Pose2d returnPose(){
        return odometry.getPoseMeters();
    }
    public double getRotTarget(){
        return rotTarget;
    }
    public void setAutoTag(boolean isOn, boolean isBlue){
        autoTag = isOn;
        this.isBlue = isBlue;
    }
    public void setVisionAuto(boolean isOn){
        this.isVision = isOn;
    }
    public void setAlliance(boolean isBlue){
        this.isBlue = isBlue;
        vision.setAlliance(isBlue);
    }
}
