package org.wildstang.year2024.subsystems;

import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.year2024.robot.WsInputs;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

/**
 * This subsystem controls the LEDs on the robot.
 * Based on the state of the robot and the game,
 * the LEDs will change color to help the drivers know what's going on.
 * @author foxler2010
 */
public class LEDSubsystem implements Subsystem {

    //led
    private AddressableLED led;
    private AddressableLEDBuffer ledBuffer;

    private int port = 0; //TODO: figure out correct value
    private int length = 60; //TODO: figure out correct value
    private int initialHue = 0; //TODO: what hue should it be?

    //robot sensors
    private boolean robotHasNote; //TODO: Have to wait for intake to be finished before this works

    //controller
    private WsJoystickButton intakeButton;
    private boolean intakeButtonHeld;

    //game state
    private boolean isEndgame; //TODO: implement a timer or something so that it works

    //vision
    private boolean canSeeStage;
    private boolean canSeeAmp;
    private boolean canSeeSpeaker;
    private boolean canSeeNote;

    //colors
    private Color cyan = new Color(0, 255, 255);
    private Color white = new Color(255, 255, 255);
    private Color purple = new Color(255, 0, 255);
    private Color green = new Color(0, 255, 0);
    private Color orange = new Color(255, 165, 0);
    private Color red = new Color(255, 0, 0);
    private Color yellow = new Color(255, 255, 0);


    @Override
    public void init() {

        //led
        led = new AddressableLED(port); //TODO: figure out what number this is
        ledBuffer = new AddressableLEDBuffer(length);

        led.setLength(ledBuffer.getLength());
        led.setData(ledBuffer);
        led.start();

        //intake button
        WsJoystickButton intakeButton = (WsJoystickButton) WsInputs.OPERATOR_DPAD_DOWN.get(); //TODO: change to actual button once we decide which one it is.
        intakeButton.addInputListener(this);

    }

    /**
     * Update the LED buffer based on various conditions and states.
     */
    @Override
    public void update() {

        if (robotHasNote) {

            if (intakeButtonHeld) {

                //cyan
                for (int i = 0; i < ledBuffer.getLength(); i++) {

                    ledBuffer.setRGB(i, (int) cyan.red, (int) cyan.green, (int) cyan.blue);

                }

            } else if (isEndgame) {

                if (canSeeStage) {

                    //TODO
                    //rainbow!! yippee!

                } else {

                    //white
                    for (int i = 0; i < ledBuffer.getLength(); i++) {

                        ledBuffer.setRGB(i, (int) white.red, (int) white.green, (int) white.blue);

                    }

                }

            } else if (canSeeAmp) {

                //purple
                for (int i = 0; i < ledBuffer.getLength(); i++) {

                    ledBuffer.setRGB(i, (int) purple.red, (int) purple.green, (int) purple.blue);

                }

            } else if (canSeeSpeaker) {

                //green
                for (int i = 0; i < ledBuffer.getLength(); i++) {

                    ledBuffer.setRGB(i, (int) green.red, (int) green.green, (int) green.blue);

                }

            } else {

                //orange
                for (int i = 0; i < ledBuffer.getLength(); i++) {

                    ledBuffer.setRGB(i, (int) orange.red, (int) orange.green, (int) orange.blue);

                }

            }
        } else if (canSeeNote) {

            //red
            for (int i = 0; i < ledBuffer.getLength(); i++) {

                ledBuffer.setRGB(i, (int) red.red, (int) red.green, (int) red.blue);

            }

        } else {

            //yellow
            for (int i = 0; i < ledBuffer.getLength(); i++) {

                ledBuffer.setRGB(i, (int) yellow.red, (int) yellow.green, (int) yellow.blue);

            }

        }

    }

    @Override
    public void inputUpdate(Input source) {

        intakeButtonHeld = intakeButton.getValue();

    }

    @Override
    public void selfTest() {}

    /**
     * Resets the state of the LED by:
     * - Setting the LED buffer to the initial hue.
     * - Making sure that the LED uses the buffer for data.
     * - Stopping and starting the LED.
     */
    @Override
    public void resetState() {

        for (int i = 0; i < ledBuffer.getLength(); i++) {

            ledBuffer.setHSV(i, initialHue, 255, 255);

        }

        led.setData(ledBuffer);
        led.stop();
        led.start();

    }

    @Override
    public String getName() {
        return "LED Subsystem";
    }
}