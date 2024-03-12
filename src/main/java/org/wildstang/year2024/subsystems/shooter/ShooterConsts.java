package org.wildstang.year2024.subsystems.shooter;

public class ShooterConsts {
    public static final double MAX_ANGLE = 180.0;
    public static final double MIN_ANGLE = 0;
    public static final double FEED_ANGLE = 0;
    public static final double IDLE_SPEED = 0.3;
    public static final double CYCLE_SPEED = 0.5;
    // Array containing arrays where the elements are a distance the corresponding and angle
    public static final double[][] SHOOTER_POSIIONS = {{}};
    public static final double P = 0.1;
    public static final double I = 0.0;
    public static final double D = 0.0;
    public static final double SPIN_RATIO = 65.0 / 100.0;
    public static final double SUBWOOFER_ANGLE = 175;
    // Degrees increment by dPad
    public static final double ANGLE_INCREMENT = 2.5;
}
