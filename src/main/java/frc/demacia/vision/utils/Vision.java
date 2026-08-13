// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.vision.utils;

// import static frc.demacia.vision.utils.VisionConstants.TAG_ANGLE;

import java.util.ArrayList;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.demacia.utils.chassis.Chassis;
import frc.demacia.vision.TagPose;
// import frc.robot.RobotCommon.*;

/** Add your docs here. */
public class Vision {

    private ArrayList<TagPose> tags;

    public Vision(TagPose[] poses) {

        this.tags = new ArrayList<>();
        for (TagPose p : poses) {
            tags.add(p);
        }
    }

    public Vision(ArrayList<TagPose> tags) {
        this.tags = tags;
    }

    public Vision() {
        this.tags = new ArrayList<>();
    }

    public void addTag(TagPose tag) {
        tags.add(tag);
    }

    public boolean isSeeTagWithDistance() {

        for (TagPose tag : tags) {
            if (tag.isSeeTag() && tag.getDistFromCamera() < 3)
                return true;
        }
        return false;
    }

    // TODO: Remove Incorrect located tags
    private boolean isTagHub(int tagId) {
        return tagId == 2
            || tagId == 3
            || tagId == 4
            || tagId == 5
            || tagId == 8
            || tagId == 9
            || tagId == 10
            || tagId == 11
            || tagId == 18
            || tagId == 19
            || tagId == 20
            || tagId == 21
            || tagId == 24
            || tagId == 25
            || tagId == 26
        ;
    }

    public boolean isSeeTag() {
        for(TagPose t : tags){
            if(t.isSeeTag()) return true;
        }
        return false;
    }
    

    private double getCollectedConfidence() {
        double confidence = 0;
        for (TagPose tag : tags) {
            if (tag.getRobotPose2d() != null) {
                confidence += tag.getPoseEstemationConfidence();
            }
        }
        return confidence;
    }

    private double normalizeConfidence(double confidence) {
        return getCollectedConfidence() == 0 ? 0 : confidence * (1d / getCollectedConfidence());
    }

    public Pose2d getPoseEstimation() {
        double x = 0;
        double y = 0;
        double confidence = 0;
        for (TagPose tag : tags) {
            Pose2d pose2d = tag.getRobotPose2d();
            if (pose2d == null)
                continue;
            confidence = normalizeConfidence(tag.getPoseEstemationConfidence());
            x += pose2d.getX() * confidence;
            y += pose2d.getY() * confidence;

        }
        return new Pose2d(x, y, Chassis.getInstance().getGyroAngle());
    }

    public void updateValues() {
        for (TagPose tag : tags) {
            tag.updateValues();
        }
    }
    
    public Rotation2d getRobotAngle(){
        for (TagPose tag : tags) {
            if (tag.getRobotPose2d() != null) {
                return Rotation2d.fromDegrees(tag.getAngle());
            }
        }
        return null;
    }

    // public Rotation2d getRotationEstimation() {
    // Integer bestCam = getBestCamera();
    // return bestCam != null ? Rotation2d.fromDegrees(tags.get(bestCam).getAngle())
    // : null;
    // }

    // public double getVisionTimestamp() {
    // double timestamp = 0;
    // for (TagPose tag : tags) {
    // if (tag.getRobotPose2d() != null) {
    // timestamp += tag.getTimestamp() *
    // normalizeConfidence(tag.getPoseEstemationConfidence());
    // }
    // }
    // return timestamp;
    // }

    // private Integer getBestCamera() {
    // Integer bestCamera = null;
    // double highestConfidence = 0.0;

    // for (int i = 0; i < tags.size(); i++) {
    // double currentConfidence = tags.get(i).getPoseEstemationConfidence();

    // if (currentConfidence > highestConfidence && (currentConfidence > 0.1)) {
    // highestConfidence = currentConfidence;
    // bestCamera = i;
    // }
    // }

    // return bestCamera;
    // }

    // public Rotation2d getVisionEstimatedAngle() {
    // return getBestCamera() != null ? tags.get(getBestCamera()).getRobotAngle() :
    // null;
    // }

    // public double getVisionConfidence() {
    // return Math.max(getCollectedConfidence(), 1);
    // }

    // public Rotation2d get2dAngle() {
    // // Ensure that 'tags' is not empty or has a valid index before accessing
    // getRobotToTagFieldRel
    // if (tags != null && tags.size() > 3 && tags[0].getRobotToTagFieldRel() !=
    // null
    // && tags[3].getRobotToTagFieldRel() != null
    // && tags[0].getTagId() == tags[3].getTagId()) {
    // return tags[0].getRobotToTagFieldRel().minus(tags[3].getRobotToTagFieldRel())
    // .getAngle().rotateBy(Rotation2d.fromDegrees(90))
    // .plus(TAG_ANGLE[tags[3].getTagId()]);
    // } else {
    // return null; // If the tags array is not correctly initialized, return null.
    // }
    // }

    // public void set3D(boolean is3D){
    // for (TagPose tag : tags) {
    // tag.set3D(is3D);
    // }
    // }

    // public double get3DAngle() {
    // Integer bestCamera = getBestCamera();
    // return bestCamera != null ? tags[bestCamera].getAngle() : 0;
    // }
}