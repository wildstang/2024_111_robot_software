package org.wildstang.year2024.subsystems.LED;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.shooter.shooter;
import org.wildstang.year2024.subsystems.targeting.WsVision;
import org.wildstang.year2024.subsystems.theFolder.theClass;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;

public class LedController implements Subsystem {

    private AnalogInput rightTrigger;
    private AddressableLED led;
    private AddressableLEDBuffer ledBuffer;
    private theClass RandomThing;
    private shooter flywheel;
    private WsVision vision;
    private Timer timer =  new Timer();

    private int port = 0;//port
    private int length = 45;//length

    private int[] white = {255,255,255};
    private int[] blue = {0,0,255};
    private int[] red = {255,0,0};
    private int[] green = {0,255,0};
    private int[] orange = {255,165,0};
    
    private int[] normal = white;
    private boolean isAuto = false;


    @Override
    public void update(){
        if (flywheel.getShooterVelocity()>5000 && vision.front.TargetInView()){
            setRGB(green);
        } else if (RandomThing.hasNote() && !isAuto){
            setRGB(orange);
        } else {
            setRGB(normal);
        }
        led.setData(ledBuffer);
        led.start();
    }

    @Override
    public void inputUpdate(Input source) {
        isAuto = false;

    }

    @Override
    public void init() {
        rightTrigger = (AnalogInput) WsInputs.DRIVER_RIGHT_TRIGGER.get();
        rightTrigger.addInputListener(this);
        RandomThing = (theClass) Core.getSubsystemManager().getSubsystem(WsSubsystems.THECLASS);
        flywheel = (shooter) Core.getSubsystemManager().getSubsystem(WsSubsystems.SHOOTER);
        vision = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);
        
        //Outputs
        led = new AddressableLED(port);
        ledBuffer = new AddressableLEDBuffer(length);
        led.setLength(ledBuffer.getLength());
        setRGB(255, 255, 255);
        led.setData(ledBuffer);
        led.start();
        resetState();
        timer.start();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void resetState() {
        normal = white;
    }

    @Override
    public String getName() {
        return "Led Controller";
    }

    public void setRGB(int red, int green, int blue){
        for (int i = 0; i < length; i++){
            ledBuffer.setRGB(i, red, green, blue);
        }
    }
    public void setRGB(int[] color){
        setRGB(color[0],color[1],color[2]);
    }
    public void setAlliance(boolean isBlue){
        this.normal = isBlue ? blue : red;
        isAuto = true;
    }
}
