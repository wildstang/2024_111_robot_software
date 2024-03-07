package org.wildstang.year2024.subsystems.LED;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsSubsystems;
import org.wildstang.year2024.subsystems.Notepath.notepath;
import org.wildstang.year2024.subsystems.targeting.WsVision;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;

public class LedController implements Subsystem {

    private AnalogInput leftTrigger;
    private AddressableLED led, led2;
    private AddressableLEDBuffer ledBuffer, ledBuffer2;
    private notepath notepath;
    private Timer timer =  new Timer();

    private int port = 0;//port
    private int port2 = 1;
    private int length = 45;//length
    private int length2 = 20;


    @Override
    public void update(){
        if (notepath.getCurrent() > 25.0 && timer.hasElapsed(0.05)){
            setRGB(255, 128, 0);
        } else {
            setRGB(0, 255, 0);
            if (notepath.getCurrent() < 25.0) timer.reset();
        }
        // led.setData(ledBuffer);
        led2.setData(ledBuffer2);
        // led.start();
        led2.start();
    }

    @Override
    public void inputUpdate(Input source) {

    }

    @Override
    public void init() {
        leftTrigger = (AnalogInput) WsInputs.DRIVER_LEFT_TRIGGER.get();
        leftTrigger.addInputListener(this);
        notepath = (notepath) Core.getSubsystemManager().getSubsystem(WsSubsystems.NOTEPATH);
        
        //Outputs
        // led = new AddressableLED(port);
        led2 = new AddressableLED(port2);
        // ledBuffer = new AddressableLEDBuffer(length);
        ledBuffer2 = new AddressableLEDBuffer(length2);
        // led.setLength(ledBuffer.getLength());
        led2.setLength(ledBuffer2.getLength());
        setRGB(255, 255, 255);
        // led.setData(ledBuffer);
        led2.setData(ledBuffer2);
        // led.start();
        led2.start();
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
            // ledBuffer.setRGB(i, red, green, blue);
        }
        for (int i = 0; i < length2; i++){
            ledBuffer2.setRGB(i, red, green, blue);
        }
    }
}
