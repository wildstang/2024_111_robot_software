package org.wildstang.year2024.subsystems.LED;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.notepath.Notepath;
import org.wildstang.year2024.subsystems.targeting.WsVision;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;

public class LedController implements Subsystem {

    private AnalogInput leftTrigger;
    private AddressableLED led;
    private AddressableLEDBuffer ledBuffer;
    private Notepath notepath;
    private Timer timer =  new Timer();

    private int port = 0;//port
    private int length = 45;//length

    private int[] white = {255,255,255};
    private int[] orange = {255,128,0};


    @Override
    public void update(){
        if (notepath.hasNote()){
            setRGB(orange);
        } else {
            setRGB(white);
        }
        led.setData(ledBuffer);
        led.start();
    }

    @Override
    public void inputUpdate(Input source) {

    }

    @Override
    public void init() {
        leftTrigger = (AnalogInput) WsInputs.DRIVER_LEFT_TRIGGER.get();
        leftTrigger.addInputListener(this);
        notepath = (Notepath) Core.getSubsystemManager().getSubsystem(WsSubsystems.NOTEPATH);
        
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
}
