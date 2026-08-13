// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.vision;

import static frc.demacia.vision.utils.VisionConstants.TAG_HEIGHT;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;

import frc.demacia.utils.chassis.Chassis;
import frc.demacia.utils.log.LogEntryBuilder.LogLevel;
import frc.demacia.utils.log.LogManager;
import frc.demacia.vision.utils.VisionConstants;

public class TagPose {
  // NetworkTables communication for each camera
  private NetworkTable Table;

  private NetworkTableEntry cropEntry;
  private NetworkTableEntry pipeEntry;

  private Field2d field;

  // Vision processing variables
  private double camToTagYaw;
  private double camToTagPitch;
  private double id;
  private Camera camera;
  private double confidence;

  private double dist;
  private double alpha;
  private double height;

  // vector for camera
  private Translation2d cameraToTag;

  // vector for robot
  private Translation2d robotToTag;
  private Translation2d originToRobot;

  // vector for tag
  private Translation2d origintoTag;

  // robot pose
  public Pose2d pose = new Pose2d();

  private double latency;

  private boolean isUpsidedown = false;

  @SuppressWarnings("unchecked")
  public TagPose(Camera camera) {
    confidence = 0;
    this.camera = camera;
    Table = NetworkTableInstance.getDefault().getTable(camera.getTableName());
    latency = 0;
    field = new Field2d();
    pipeEntry = Table.getEntry("pipeline");
    // LogManager.addEntry(camera.getName()+"dist", this::getDistFromCamera).withLogLevel(LogLevel.LOG_AND_NT_NOT_IN_COMP).build();
    // LogManager.addEntry(camera.getName()+"dist ty", this::getDistanceFromTy).withLogLevel(LogLevel.LOG_AND_NT_NOT_IN_COMP).build();

    SmartDashboard.putData("field-tag " + camera.getName(), field);
    SmartDashboard.putData("setTo3d " + camera.getName(),
        new InstantCommand(() -> setDimension(true)).ignoringDisable(true));
    SmartDashboard.putData("setTo2d " + camera.getName(),
        new InstantCommand(() -> setDimension(false)).ignoringDisable(true));
    SmartDashboard.putData("chassis/reset gyro by camera " + camera.getName(),
        Commands.sequence(
            new InstantCommand(() -> changePipeline(5)).ignoringDisable(true),
            new InstantCommand(() -> Chassis.getInstance().setYaw(getRobotAngle())).ignoringDisable(true),
            new InstantCommand(() -> changePipeline(0)).ignoringDisable(true)).ignoringDisable(true));

  }

  public TagPose(Camera camera, boolean isUpsidedown) {
    this(camera);
    this.isUpsidedown = isUpsidedown;
  }

  public void setDimension(boolean is3D) {

    Table.getEntry("pipeline").setNumber(is3D ? 1 : 0);
  }

  public Rotation2d get3dAngle() {
    double[] botpose_orb_wpired = Table.getEntry("botpose").getDoubleArray(new double[12]);
    return new Rotation2d(Math.toRadians(botpose_orb_wpired[5]));
  }

  private void changePipeline(int id) {
    pipeEntry.setDouble(id);
  }

  public void updateValues() {
    cropEntry = Table.getEntry("crop");
    pipeEntry = Table.getEntry("pipeline");
    camToTagPitch = (isUpsidedown ? -1 : 1) * Table.getEntry("ty").getDouble(0.0);
    camToTagYaw = (isUpsidedown ? 1 : -1) * Table.getEntry("tx").getDouble(0.0);
    id = (int) Table.getEntry("tid").getDouble(0.0);
    // if (camera.getIsOnTurret()) {
    // }

  }

  public Pose2d getRobotPose2d() {
    if (isSeeTag()) {
      if (camera.getIsCroping()) {
        crop();
      }

      if (id > 0 && id < VisionConstants.TAG_HEIGHT.length) {
        pose = new Pose2d(getOriginToRobot(), Chassis.getInstance().getGyroAngle());
        field.setRobotPose(pose);
        confidence = getConfidence();
      }
    } else {
      cropStop();
      pose = null;
    }
    // if (wantedPip != Table.getEntry("getpipe").getDouble(0.0)) {
    // pipeEntry.setDouble(wantedPip);
    // }
    return pose;
  }

  /**
   * Calculates robot position relative to field origin
   * Uses known AprilTag position and measured vector to tag
   * * @return Translation2d representing robot position on field
   */
  public Translation2d getOriginToRobot() {

    origintoTag = VisionConstants.O_TO_TAG[(int) this.id == -1 ? 0 : (int) this.id];

    height = VisionConstants.TAG_HEIGHT[(int) this.id];
    if (origintoTag != null) {

      originToRobot = origintoTag.minus(getRobotToTagFieldRel());

      return originToRobot;
    }
    return new Translation2d();

  }

  /**
   * Calculates vector from robot center to detected AprilTag
   * Accounts for camera offset from robot center
   * * @return Translation2d representing vector to tag
   */
  public Translation2d getRobotToTagFieldRel() {
    // Convert camera measurements to vector
    cameraToTag = new Translation2d(getDistFromCamera(),
        Rotation2d.fromDegrees(camToTagYaw + camera.getYaw()));
    // Add camera offset to get robot center to tag vector
    robotToTag = (camera.getRobotToCamPosition().toTranslation2d()
        .plus(cameraToTag)).rotateBy(Chassis.getInstance().getGyroAngle());
    return robotToTag;
  }

  // public double getDistFromCamera() {

  //   alpha = Math.abs(camToTagPitch + camera.getPitch()) * Math.abs(Math.cos(Math.toRadians(camToTagYaw + camera.getYaw())));
  //   dist = (Math.abs(height - camera.getHeight())) / (Math.tan(Math.toRadians(alpha)));
  //   return dist;
  // }
  public double getDistanceFromTy(){
    if(id < 0){
      return 0.0;
    }
    double deltaHeight = TAG_HEIGHT[(int)id] - camera.getHeight();
    double alpha = Math.toRadians(camera.getPitch() + camToTagPitch);
    double distance = Math.abs(deltaHeight / Math.tan(alpha));

    return distance;
  }
  public double getDistFromCamera() {
    if(id < 0){
      return 0.0;
    }

    double deltaHeight = TAG_HEIGHT[(int)id] - camera.getHeight();
    double alpha = Math.toRadians(camera.getPitch() + camToTagPitch);
    double distance = Math.abs(deltaHeight / Math.tan(alpha)) / Math.cos((Math.toRadians(camToTagYaw)));


    return distance;    
  }

  private void crop() {
    double YawCrop = getYawCrop();
    double PitchCrop = getPitchCrop();
    double[] crop = { YawCrop - getCropOfset(), YawCrop + getCropOfset(), PitchCrop - getCropOfset(),
        PitchCrop + getCropOfset() };
    cropEntry.setDoubleArray(crop);
  }

  private double getCropOfset() {
    double crop = getDistFromCamera() * VisionConstants.CROP_CONSTAT;
    return MathUtil.clamp(crop, VisionConstants.MIN_CROP, VisionConstants.MAX_CROP);
  }

  private double getYawCrop() {
    double TagYaw = ((-camToTagYaw) + camera.getYaw()) / 31.25;
    return TagYaw + Chassis.getInstance().getChassisSpeedsFieldRel().vyMetersPerSecond * VisionConstants.PREDICT_Y
        + Chassis.getInstance().getChassisSpeedsFieldRel().omegaRadiansPerSecond * VisionConstants.PREDICT_OMEGA;
  }

  private double getPitchCrop() {
    double TagPitch = camToTagPitch / 24.45;
    return TagPitch + Chassis.getInstance().getChassisSpeedsFieldRel().vxMetersPerSecond * VisionConstants.PREDICT_X;
  }

  private void cropStop() {
    double[] crop = { -1, 1, -1, 1 };
    cropEntry.setDoubleArray(crop);
  }

  private double getConfidence() {
    // Get the current distance to tag
    double currentDist = getDistFromCamera();

    // If we're within reliable range, give high confidence
    if (currentDist <= VisionConstants.BEST_RELIABLE_DISTANCE) {
      return 1.0;
    }

    // Calculate how far we are into the falloff range (0 to 1)
    double normalizedDist = (currentDist - VisionConstants.BEST_RELIABLE_DISTANCE)
        / ((VisionConstants.WORST_RELIABLE_DISTANCE) - VisionConstants.BEST_RELIABLE_DISTANCE);

    // Apply cubic falloff function
    return Math.pow(1 - normalizedDist, 3);
  }

  public double getPoseEstemationConfidence() {
    return this.confidence;
  }

  public Camera getCamera() {
    return camera;
  }

  public double getCamToTagYaw() {
    return camToTagYaw;
  }

  public double getTimestamp() {
    return latency;
  }

  public int getTagId() {
    return (int) Table.getEntry("tid").getDouble(0.0);
  }

  public double getAngle() {
    return Table.getEntry("botpose").getDoubleArray(new double[] { 0, 0, 0, 0, 0,
        0 })[5];
  }

  public Rotation2d getRobotAngle() {
    return null;
  }

  public boolean getIsObjectCamera() {
    return camera.getIsObjectCamera();
  }

  public boolean isSeeTag() {
    return Table.getEntry("tv").getDouble(0.0) >= 0.1;
  }

  public Translation2d getCameraToTag() {
    return cameraToTag = new Translation2d(getDistFromCamera(),
        Rotation2d.fromDegrees(camToTagYaw + camera.getYaw()));
  }

}
