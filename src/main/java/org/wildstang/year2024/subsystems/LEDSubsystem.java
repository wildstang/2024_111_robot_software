package org.wildstang.year2024.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsAnalogInput;
import org.wildstang.hardware.roborio.inputs.WsDigitalInput;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.year2024.robot.WsInputs;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This subsystem controls the LEDs on the robot.
 * Based on the state of the robot and the game, \
 * the LEDs will change color to help the drivers know what's going on.
 * @author foxler2010
 */
public class LEDSubsystem implements Subsystem {

    //led
    private AddressableLED led;
    private AddressableLEDBuffer ledBuffer;

    private int port = 0; //figure out correct value
    private int length = 60; //figure out correct value
    private int initalHue = 0;

    //robot sensors
    private boolean robotHasNote; //Have to wait for intake to be finished before this works

    //controller
    private WsJoystickButton intakeButton;
    private boolean intakeButtonHeld;

    //game state
    private boolean isEndgame;

    //vision
    private boolean canSeeStage;
    private boolean canSeeAmp;
    private boolean canSeeSpeaker;
    private boolean canSeeNote;


    @Override
    public void init() {

        //led
        led = new AddressableLED(port); //TODO: figure out what number this is
        ledBuffer = new AddressableLEDBuffer(length);

        led.setLength(ledBuffer.getLength());
        led.setData(ledBuffer);
        led.start();

        //intake button
        WsJoystickButton intakeButton = (WsJoystickButton) WsInputs.PLACEHOLDER.get();
        intakeButton.addInputListener(this);

    }

    @Override
    public void update() {

        if (robotHasNote) {

            if (intakeButtonHeld) {

                //cyan

            } else if (isEndgame) {

                if (canSeeStage) {

                    //rainbow!! yippee!

                } else {

                    //white

                }

            } else if (canSeeAmp) {

                //purple

            } else if (canSeeSpeaker) {

                //green

            } else {

                //orange

            }
        } else if (canSeeNote) {

            //red

        } else {

            //yellow

        }

    }

    @Override
    public void inputUpdate(Input source) {

        intakeButtonHeld = intakeButton.getValue();

    }

    @Override
    public void selfTest() {}

    @Override
    public void resetState() {}

    @Override
    public String getName() {
        return "LED Subsystem";
    }
}