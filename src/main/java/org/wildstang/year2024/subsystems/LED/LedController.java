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

public class LedController implements Subsystem {

    private AnalogInput leftTrigger;
    private AddressableLED led;
    private AddressableLEDBuffer ledBuffer;
    private notepath notepath;

    private int port = 0;//port
    private int length = 15;//length

    private boolean isAiming = false;


    @Override
    public void update(){
        if (notepath.getCurrent() > 15.0){
            setRGB(0, 255, 255);
        } else {
            setRGB(255, 255, 0);
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
        notepath = (notepath) Core.getSubsystemManager().getSubsystem(WsSubsystems.NOTEPATH);
        
        //Outputs
        led = new AddressableLED(port);
        ledBuffer = new AddressableLEDBuffer(length);
        led.setLength(ledBuffer.getLength());
        for (int i = 0; i < length; i++){
            ledBuffer.setRGB(i, 255, 255, 255);
        }
        led.setData(ledBuffer);
        led.start();
        resetState();
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
}
