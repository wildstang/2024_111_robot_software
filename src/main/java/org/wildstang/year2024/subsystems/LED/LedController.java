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
    private int initialHue = 0;
    private int initialRed = 0;
    private int initialBlue = 0;

    private int[] white = {255,255,255};
    private int[] blue = {0,0,255};
    private int[] red = {255,0,0};
    private int[] green = {0,255,0};
    private int[] orange = {255,165,0};
    private int[] cyan = {0,255,255};
    
    private int[] normal = white;
    private boolean isAuto = false;


    @Override
    public void update(){
        if (vision.front.canSeeSpeaker(vision.getAlliance()) && RandomThing.hasNote()){ 
            if (flywheel.getShooterVelocity()>5000){
                setRGB(green); 
            } else setRGB(cyan);
        } else if (RandomThing.hasNote() && !isAuto){
            setRGB(orange);
        } else {
            if (normal == white) rainbow();
            else if (normal == blue) cycleBlue();
            else if (normal == red) cycleRed();
            else setRGB(white);
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
    private void rainbow(){
        for (int i = 0; i < ledBuffer.getLength(); i++){
            ledBuffer.setHSV(i, (initialHue + (i*180/ledBuffer.getLength()))%180, 255, 128);
        }
        initialHue = (initialHue + 3) % 180;
        led.setData(ledBuffer);
    } 
    private void cycleRed(){
        for (int i = 0; i < ledBuffer.getLength(); i++){
            ledBuffer.setRGB(i, (initialRed + 255-(i*255/ledBuffer.getLength()))%255, 0, 0);
        }
        initialRed = (initialRed + 5) % 255;
    }
    private void cycleBlue(){
        for (int i = 0; i < ledBuffer.getLength(); i++){
            ledBuffer.setRGB(i, 0, 0, 255-(initialBlue + (i*255/ledBuffer.getLength()))%255);
        }
        initialBlue = (initialBlue + 5) % 255;
    }
}
