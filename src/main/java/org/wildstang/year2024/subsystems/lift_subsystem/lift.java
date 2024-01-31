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
    //inputs_outputs

private WsSpark liftMAX;
private DigitalInput left_raize_AMP,trap_start_BT,trap_select_BT;


private Timer timer = new Timer();


private static final double Lift_AMP_Time = 2.0;
private boolean isLifting = false;
private boolean isOperating = false;



@Override
public void init(){
    left_raize_AMP = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_SHOULDER);
    left_raize_AMP.addInputListener(this);
    trap_start_BT = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_START);
    trap_start_BT.addInputListener(this);
    trap_select_BT = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_SELECT);
    trap_select_BT.addInputListener(this);

    liftMAX = (WsSpark) Core.getOutputManager().getOutput(WsOutputs.LIFT1);
    liftMAX.setCurrentLimit(40, 40, 0);

    
    
} 
@Override
public void resetState(){
    timer.reset();
    timer.start();
    
} 
@Override  
public void update(){
    if(isOperating && timer.hasElapsed(Lift_AMP_Time)){
        liftMAX.setSpeed(isLifting ? 1.0 : -1.0);
    } else { 
        liftMAX.setSpeed(0);
        isOperating = false;
        timer.stop();
    }
    
}

@Override
public void inputUpdate(Input source){
    if (left_raize_AMP.getValue()) {
        if (!isOperating){
            isOperating = true;
            isLifting = true;
           resetState();
        }
    }
    else{
        if(isOperating && isLifting){
            isLifting = false;
          resetState();
        }
        
    }

}
@Override
public void selfTest(){
    
}

@Override
public String getName(){
    return "lift";
    
}

}
