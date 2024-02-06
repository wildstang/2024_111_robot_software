package org.wildstang.year2024.subsystems.lift_subsystem;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.year2024.robot.WsInputs;
import org.wildstang.year2024.robot.WsOutputs;
import edu.wpi.first.wpilibj.Timer;

public class lift implements Subsystem {
    private WsSpark liftMAX;
    private DigitalInput left_raize_AMP, trap_start_BT, trap_select_BT;
    private Timer timer = new Timer();
    private Timer trapTimer = new Timer();

    private static final double LIFT_AMP_TIME = 2.0;
    private static final double TRAP_TIME = 3.0;
    private double liftDuration = 0;
    private boolean isOperating = false;
    private boolean isLifting = false;
    private boolean isSelectPressed = false;
    private boolean isStartPressed = false;
    private boolean isTrapOperating = false;

    @Override
    public void init() {
        left_raize_AMP = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_SHOULDER);
        trap_start_BT = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_START);
        trap_select_BT = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_SELECT);
        liftMAX = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.LIFT1);
        liftMAX.setCurrentLimit(40, 40, 0);
        liftMAX.initClosedLoop(3.0,0.0,0.0,0.0);

        left_raize_AMP.addInputListener(this);
        trap_start_BT.addInputListener(this);
        trap_select_BT.addInputListener(this);
    }

    @Override
    public void inputUpdate(Input source) {
        if (source == left_raize_AMP) {
            if (left_raize_AMP.getValue() && !isOperating) {
                isLifting = true;
                isOperating = true;
                timer.reset();
                timer.start();
            } else if (!left_raize_AMP.getValue() && isOperating && isLifting) {
                isLifting = false;
                liftDuration = timer.get();
                timer.reset();
                timer.start();
            }
        } else if (source == trap_select_BT || source == trap_start_BT) {
            isSelectPressed = trap_select_BT.getValue();
            isStartPressed = trap_start_BT.getValue();

            if (isSelectPressed && isStartPressed && !isTrapOperating) {
                isTrapOperating = true;
                trapTimer.reset();
                trapTimer.start();
            }
        }
    }

    @Override
    public void update() {
        if (isOperating) {
            if ((isLifting && timer.hasElapsed(LIFT_AMP_TIME)) || (!isLifting && timer.hasElapsed(liftDuration))) {
                resetState();
            } else {
                liftMAX.setSpeed(isLifting ? 1.0 : -1.0);
            }
        }

        if (isTrapOperating && trapTimer.hasElapsed(TRAP_TIME)) {
            resetState();
        } else if (isTrapOperating) {
            liftMAX.setSpeed(1.0);
        }
    }

    @Override
    public void selfTest() {
        
    }

    @Override
    public void resetState() {
        
        timer.reset();
        trapTimer.reset();

        
        isOperating = false;
        isLifting = false;
        isTrapOperating = false;
        isSelectPressed = false;
        isStartPressed = false;

        
        liftMAX.setSpeed(0);
    }

    @Override
    public String getName() {
        return "lift";
    }
}
