package org.wildstang.year2024.subsystems.targeting;

import java.util.List;

public class VisionConsts {

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

    public static final double backMountHeight = -1;
    public static final double backMountAngle = -1;

}