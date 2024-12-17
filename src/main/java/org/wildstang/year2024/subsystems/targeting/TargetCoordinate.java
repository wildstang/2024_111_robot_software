package org.wildstang.year2024.subsystems.targeting;

import org.wildstang.framework.core.Core;

public class TargetCoordinate{
        private double blueX, blueY, redX, redY;

        public TargetCoordinate(double i_blueX, double i_blueY, double i_redX, double i_redY){
            blueX = i_blueX;
            blueY = i_blueY;
            redX = i_redX;
            redY = i_redY;
        }
        public double getX(){
            return Core.isBlue() ? blueX : redX;
        }
        public double getY(){
            return Core.isBlue() ? blueY : redY;
        }
    }
