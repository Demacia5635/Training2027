// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.vision.utils;

import org.ejml.simple.SimpleMatrix;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

import frc.demacia.vision.Camera;
import frc.demacia.vision.TagPose;

/**
 * Constants and configuration values for AprilTag vision processing.
 * This class contains the physical layout and properties of AprilTags on the
 * field,
 * as well as camera mounting parameters for the 2026 REEFSCAPE season.
 */
public class VisionConstants {

        // Heights of different AprilTag groups from the ground (in meters)
        private static final double OUTPOST_TAG_HEIGHT = inchToMeter(35.0); // Outpost tags (human player stations)
        private static final double HUB_TAG_HEIGHT = inchToMeter(44.25); // Hub scoring location tags
        private static final double DEPOT_TAG_HEIGHT = inchToMeter(21.75); // Depot perimeter tags

    public static final double BEST_RELIABLE_DISTANCE = 1; // meters - high confidence range
    public static final double WORST_RELIABLE_DISTANCE = 4; // meters - Low confidence range

    public static final double BEST_RELIABLE_SPEED = 1; // meter a second - high confidence range
    public static final double WORST_RELIABLE_SPEED = 3; // meter a second - Low confidence range

        /**
         * Array of AprilTag positions relative to field origin (0,0).
         * Each Translation2d represents X,Y coordinates in meters.
         * Index corresponds to AprilTag ID (0 is unused).
         * Coordinates are converted from inches to meters using inchToMeter().
         * 
         * Updated for 2026 REEFSCAPE season.
         */
        public static final Translation2d[] O_TO_TAG = {
                        null, // 0 - unused
                        new Translation2d(inchToMeter(467.637), inchToMeter(292.314)), // 1
                        new Translation2d(inchToMeter(469.111), inchToMeter(182.6)), // 2
                        new Translation2d(inchToMeter(445.349), inchToMeter(172.844)), // 3
                        new Translation2d(inchToMeter(445.349), inchToMeter(158.844)), // 4
                        new Translation2d(inchToMeter(469.111), inchToMeter(135.088)), // 5
                        new Translation2d(inchToMeter(467.637), inchToMeter(25.374)), // 6
                        new Translation2d(inchToMeter(470.586), inchToMeter(25.374)), // 7
                        new Translation2d(inchToMeter(483.111), inchToMeter(135.088)), // 8
                        new Translation2d(inchToMeter(492.881), inchToMeter(144.844)), // 9
                        new Translation2d(inchToMeter(492.881), inchToMeter(158.844)), // 10
                        new Translation2d(inchToMeter(483.111), inchToMeter(182.6)), // 11
                        new Translation2d(inchToMeter(470.586), inchToMeter(292.314)), // 12
                        new Translation2d(inchToMeter(650.918), inchToMeter(291.469)), // 13
                        new Translation2d(inchToMeter(650.918), inchToMeter(274.469)), // 14
                        new Translation2d(inchToMeter(650.904), inchToMeter(170.219)), // 15
                        new Translation2d(inchToMeter(650.904), inchToMeter(153.219)), // 16
                        new Translation2d(inchToMeter(183.586), inchToMeter(25.374)), // 17
                        new Translation2d(inchToMeter(182.111), inchToMeter(135.088)), // 18
                        new Translation2d(inchToMeter(205.873), inchToMeter(144.844)), // 19
                        new Translation2d(inchToMeter(205.873), inchToMeter(158.844)), // 20
                        new Translation2d(inchToMeter(182.111), inchToMeter(182.6)), // 21
                        new Translation2d(inchToMeter(183.586), inchToMeter(292.314)), // 22
                        new Translation2d(inchToMeter(180.637), inchToMeter(292.314)), // 23
                        new Translation2d(inchToMeter(168.111), inchToMeter(182.6)), // 24
                        new Translation2d(inchToMeter(158.341), inchToMeter(172.844)), // 25
                        new Translation2d(inchToMeter(158.341), inchToMeter(158.844)), // 26
                        new Translation2d(inchToMeter(168.111), inchToMeter(135.088)), // 27
                        new Translation2d(inchToMeter(180.637), inchToMeter(25.374)), // 28
                        new Translation2d(inchToMeter(0.305), inchToMeter(26.219)), // 29
                        new Translation2d(inchToMeter(0.305), inchToMeter(43.219)), // 30
                        new Translation2d(inchToMeter(0.318), inchToMeter(147.469)), // 31
                        new Translation2d(inchToMeter(0.318), inchToMeter(164.469)) // 32
        };

        /**
         * Array of AprilTag rotation angles (yaw).
         * Each Rotation2d represents the angle the tag faces.
         * Index corresponds to AprilTag ID (0 is unused).
         * 
         * Updated for 2026 REEFSCAPE season.
         */
        public static final Rotation2d[] TAG_ANGLE = {
                        null, // 0 - unused
                        Rotation2d.fromDegrees(180), // 1
                        Rotation2d.fromDegrees(90), // 2
                        Rotation2d.fromDegrees(180), // 3
                        Rotation2d.fromDegrees(180), // 4
                        Rotation2d.fromDegrees(270), // 5
                        Rotation2d.fromDegrees(180), // 6
                        Rotation2d.fromDegrees(0), // 7
                        Rotation2d.fromDegrees(270), // 8
                        Rotation2d.fromDegrees(0), // 9
                        Rotation2d.fromDegrees(0), // 10
                        Rotation2d.fromDegrees(90), // 11
                        Rotation2d.fromDegrees(0), // 12
                        Rotation2d.fromDegrees(180), // 13
                        Rotation2d.fromDegrees(180), // 14
                        Rotation2d.fromDegrees(180), // 15
                        Rotation2d.fromDegrees(180), // 16
                        Rotation2d.fromDegrees(0), // 17
                        Rotation2d.fromDegrees(270), // 18
                        Rotation2d.fromDegrees(0), // 19
                        Rotation2d.fromDegrees(0), // 20
                        Rotation2d.fromDegrees(90), // 21
                        Rotation2d.fromDegrees(0), // 22
                        Rotation2d.fromDegrees(180), // 23
                        Rotation2d.fromDegrees(90), // 24
                        Rotation2d.fromDegrees(180), // 25
                        Rotation2d.fromDegrees(180), // 26
                        Rotation2d.fromDegrees(270), // 27
                        Rotation2d.fromDegrees(180), // 28
                        Rotation2d.fromDegrees(0), // 29
                        Rotation2d.fromDegrees(0), // 30
                        Rotation2d.fromDegrees(0), // 31
                        Rotation2d.fromDegrees(0) // 32
        };

        /**
         * Array of AprilTag heights from the ground.
         * Each value corresponds to the Z-coordinate from the CSV.
         * Index corresponds to AprilTag ID (0 is unused).
         * 
         * Updated for 2026 REEFSCAPE season.
         */
        public static final double[] TAG_HEIGHT = {
                        0, // 0 - unused
                        OUTPOST_TAG_HEIGHT, // 1 - 35" (Outpost)
                        HUB_TAG_HEIGHT, // 2 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 3 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 4 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 5 - 44.25" (Hub)
                        OUTPOST_TAG_HEIGHT, // 6 - 35" (Outpost)
                        OUTPOST_TAG_HEIGHT, // 7 - 35" (Outpost)
                        HUB_TAG_HEIGHT, // 8 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 9 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 10 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 11 - 44.25" (Hub)
                        OUTPOST_TAG_HEIGHT, // 12 - 35" (Outpost)
                        DEPOT_TAG_HEIGHT, // 13 - 21.75" (Depot)
                        DEPOT_TAG_HEIGHT, // 14 - 21.75" (Depot)
                        DEPOT_TAG_HEIGHT, // 15 - 21.75" (Depot)
                        DEPOT_TAG_HEIGHT, // 16 - 21.75" (Depot)
                        OUTPOST_TAG_HEIGHT, // 17 - 35" (Outpost)
                        HUB_TAG_HEIGHT, // 18 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 19 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 20 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 21 - 44.25" (Hub)
                        OUTPOST_TAG_HEIGHT, // 22 - 35" (Outpost)
                        OUTPOST_TAG_HEIGHT, // 23 - 35" (Outpost)
                        HUB_TAG_HEIGHT, // 24 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 25 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 26 - 44.25" (Hub)
                        HUB_TAG_HEIGHT, // 27 - 44.25" (Hub)
                        OUTPOST_TAG_HEIGHT, // 28 - 35" (Outpost)
                        DEPOT_TAG_HEIGHT, // 29 - 21.75" (Depot)
                        DEPOT_TAG_HEIGHT, // 30 - 21.75" (Depot)
                        DEPOT_TAG_HEIGHT, // 31 - 21.75" (Depot)
                        DEPOT_TAG_HEIGHT // 32 - 21.75" (Depot)
        };

        // Scoring position offsets TODO: Update these based on 2026 Hub/Tower/Outpost
        // positions
        public static final Translation2d HUB_TAG_TO_RIGHT_SCORING = new Translation2d(-0.55, -0.160);
        public static final Translation2d HUB_TAG_TO_LEFT_SCORING = new Translation2d(-0.55, 0.160);
        public static final Translation2d INTAKE_TAG_TO_SCORING = new Translation2d(-0.85, 0.0);

        /**
         * Converts a measurement from inches to meters
         * 
         * @param inch Value in inches
         * @return Value in meters
         */
        public static double inchToMeter(double inch) {
                return inch * 0.0254;
        }
        /**
         * Converts a measurement from inches to meters
         * 
         * @param inch Value in inches
         * @return Value in meters
         */
        ;

        // Vision processing constants
        public static final double MIN_CROP = 0.5;
        public static final double MAX_CROP = 0.6;
        public static final double CROP_CONSTAT = 0.35;
        public static final double PREDICT_X = -0.1;
        public static final double PREDICT_Y = 0.15;
        public static final double PREDICT_OMEGA = 0.2;

        // Quest VR headset mounting configuration
        // TODO: Find the right offsets for your robot's Quest mounting position
        public static final Rotation3d YAW_OFFSET_QUEST = new Rotation3d(0, 0.0, 0);
        public static final double Y_OFFSET_QUEST = -0.176043;//-0.14018022
        public static final double X_OFFSET_QUEST = 0.1579;//0.3026234
        public static final double Z_OFFSET_QUEST = 0.460;//0.460


        /**
         * Transform from the center of the robot to Quest headset position in 3d
         */
        public static final Transform3d ROBOT_TO_QUEST3D = new Transform3d(
                        X_OFFSET_QUEST,
                        Y_OFFSET_QUEST,
                        Z_OFFSET_QUEST,
                        YAW_OFFSET_QUEST);

        // public static final TagPose FUEL = new TagPose(new Camera("fuel", new
        // Translation3d(0.329, -0.24325, 0.3195), -24, 0, false, true));

        // public static final TagPose LIMELIGHT4 = new TagPose(new Camera("hub", new
        // Translation3d(-0.133, 0.19, 0.35345), 30.0, 0.0, false, Rotation2d.kZero, new
        // Translation3d(0.11307,0.14305,0.18386) ));
        public static final Matrix<N3, N1> QUEST_STD = new Matrix<>(
                        new SimpleMatrix(
                                        new double[] { 0.05, 0.05, 0 }));


        
        public static class Tags {
                public static final TagPose[] TAGS_ARRAY = {
                                // new TagPose(new Camera("hub", new Translation3d(-0.133, 0.19, 0.545), 29, 0.0, false)),
                                //prev x offset 0.283 
                                // new TagPose(new Camera("hub", new Translation3d(0.27,-0.20,0.345), 19d, 2d, false, false)),
                                // new TagPose(new Camera("back", new Translation3d(-0.276, -0.195, 0.195), 31d, 195d, false, false), true)
                        };

        }
}