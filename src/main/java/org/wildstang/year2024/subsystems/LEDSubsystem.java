package org.wildstang.year2024.subsystems;

//input
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.subsystems.targeting.WsVision;

//output
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

//utiity
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
    //TODO: Have to wait for vision to work
    private boolean canSeeStage;
    private boolean canSeeAmp;
    private boolean canSeeSpeaker;
    private boolean canSeeNote;

    //colors
    private final Color cyan = new Color(0, 255, 255);
    private final Color white = new Color(255, 255, 255);
    private final Color purple = new Color(255, 0, 255);
    private final Color green = new Color(0, 255, 0);
    private final Color orange = new Color(255, 165, 0);
    private final Color red = new Color(255, 0, 0);
    private final Color yellow = new Color(255, 255, 0);


    @Override
    public void init() {

        //led
        led = new AddressableLED(port);
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

        //figure out the state of things
        robotHasNote = WsVision.backSeesNote();

        //update the LED based on the state of things
        if (robotHasNote) {

            if (intakeButtonHeld) {

                setColor(cyan);

            } else if (isEndgame) {

                if (canSeeStage) {

                    //TODO
                    //rainbow!! yippee!

                } else {

                    setColor(white);

                }

            } else if (canSeeAmp) {

                setColor(purple);

            } else if (canSeeSpeaker) {

                setColor(green);

            } else {

                setColor(orange);

            }
        } else if (canSeeNote) {

            setColor(red);

        } else {

            setColor(yellow);

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

    /**
     * Set the color of the LED buffer.
     *
     * @param  color  the color to set
     */
    private void setColor(Color color) {

        for (int i = 0; i < ledBuffer.getLength(); i++) {

            ledBuffer.setRGB(i, (int) color.red, (int) color.green, (int) color.blue);

        }

    }
}