package org.wildstang.year2024.subsystems.targeting;

import java.util.List;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;

public class VisionConsts {

    // Should be the right values from onshape
    public static Pose3d cameraPose = new Pose3d(-11.8,8.5,22.3, new Rotation3d(0.0,-65,11));

    public final double inToM = 1/25.4;
    public final double mToIn = 25.4;

    public final double blueSpeakerX = 9.5;//in
    public final double blueSpeakerY = 218.42;//in
    public final double redSpeakerX = 641.7;//in
    public final double redSpeakerY = 218.42;//in

    //TODO: what are the actual pipeline indices?
    public final int notePipelineIndex = 0;
    public final int ATPipelineIndex = 0;

    /*
     * April Tag IDs:
     * - Stage: 11, 12, 13, 14, 15, 16
     * - Speaker: 3, 4, 7, 8
     * - Amp: 5, 6
     */
    public final List<Integer> stageATs = List.of(11, 12, 13, 14, 15, 16);
    public final List<Integer> speakerATs = List.of(3, 4, 7, 8);
    public final List<Integer> ampATs = List.of(5, 6);

}