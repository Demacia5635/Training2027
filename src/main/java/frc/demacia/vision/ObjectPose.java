// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.vision;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// Subsystem that tracks and calculates the position of a vision target (object) on the field
public class ObjectPose{
  private Translation2d robotToObject;
  private Translation2d cameraToObject;
  private Translation2d OriginToObject;

  // Previous cycle tracking
  private Pose2d previousObjectPose;
  private boolean hasPreviousTarget = false;

  private NetworkTable Table;
  private Field2d field;
  private Field2d robotfield;

  private double camToObjectYaw;
  private double camToObjectPitch;

  private Supplier<Rotation2d> getRobotAngle;
  private Supplier<Pose2d> robotCurrentPose;

  private Camera camera;
  private Pose2d objectPose;

  // Deadzone and tracking parameters
  private static final double DEADZONE_DISTANCE = 0.5; // meters - adjust based on your robot size
  private static final double MAX_TARGET_JUMP = 1.0; // meters - max distance target can move between frames

  /**
   * Constructor - Initializes the object tracker with camera configuration and robot position suppliers.
   * Sets up the NetworkTable connection to receive vision data from the camera.
   */
  public ObjectPose(Camera camera, Supplier<Rotation2d> getRobotAngle, Supplier<Pose2d> robotCurrentPose) {
    this.getRobotAngle = getRobotAngle;
    this.robotCurrentPose = robotCurrentPose;
    field = new Field2d();
    robotfield = new Field2d();

    this.camera = camera;
    Table = NetworkTableInstance.getDefault().getTable(camera.getTableName());

    SmartDashboard.putData("fieldObject" + camera.getName(), field);
    SmartDashboard.putData("fieldrobot" + camera.getName(), robotfield);
    
    // SmartDashboard.putNumber("tx", camera.getX());
    // SmartDashboard.putNumber("ty", camera.getY());
  }

  /**
   * Main function that decides which pose to use (current or previous) based on:
   * 1. Whether current target is visible
   * 2. Whether current target is in deadzone
   * 3. Whether previous target is still valid
   * 
   * @return The chosen Pose2d to use for object tracking
   */
  private Pose2d chooseObjectPose() {
    double currentCamToObjectPitch = Table.getEntry("ty").getDouble(0.0) + camera.getPitch();
    double currentCamToObjectYaw = (-Table.getEntry("tx").getDouble(0.0)) + camera.getYaw();
    boolean hasCurrentTarget = Table.getEntry("tv").getDouble(0.0) != 0;

    // Case 1: No current target detected
    if (!hasCurrentTarget) {
      if (hasPreviousTarget) {
        SmartDashboard.putString("Target Status", "Using Previous (Lost)");
        return previousObjectPose;
      } else {
        SmartDashboard.putString("Target Status", "No Target");
        return Pose2d.kZero;
      }
    }

    // Case 2: Current target detected - calculate its pose
    camToObjectPitch = currentCamToObjectPitch;
    camToObjectYaw = currentCamToObjectYaw;
    
    Translation2d currentOriginToObject = getOriginToObject();
    Pose2d currentObjectPose = new Pose2d(currentOriginToObject, getRobotAngle.get());
    double currentDistance = getRobotToObject().getNorm();

    // Check if current target is in deadzone
    boolean currentInDeadzone = currentDistance < DEADZONE_DISTANCE;

    if (currentInDeadzone && hasPreviousTarget) {
      // Current is in deadzone, check if previous is better
      double previousDistance = previousObjectPose.getTranslation()
          .getDistance(robotCurrentPose.get().getTranslation());
      boolean previousInDeadzone = previousDistance < DEADZONE_DISTANCE;

      // Check if it's likely the same target (didn't jump too far)
      double positionJump = currentOriginToObject.getDistance(previousObjectPose.getTranslation());
      boolean isSameTarget = positionJump < MAX_TARGET_JUMP;

      if (!previousInDeadzone && isSameTarget) {
        // Previous is NOT in deadzone and is the same target - use previous
        SmartDashboard.putString("Target Status", "Using Previous (Current in Deadzone)");
        return previousObjectPose;
      }
    }

    // Case 3: Use current target (it's good or we have no better option)
    if (currentInDeadzone) {
      SmartDashboard.putString("Target Status", "Current (In Deadzone)");
    } else {
      SmartDashboard.putString("Target Status", "Current (Good)");
    }

    // Update previous for next cycle
    previousObjectPose = currentObjectPose;
    hasPreviousTarget = true;

    SmartDashboard.putNumber("Distance to Target", currentDistance);
    
    return currentObjectPose;
  }

  /**
   * Periodic method called every robot loop (~20ms).
   * Reads the latest vision data (pitch, yaw) from NetworkTables and updates the object's field position
   * if a valid target is detected.
   */
  public void update(){
    objectPose = chooseObjectPose();
    
    if (!objectPose.equals(Pose2d.kZero)) {
      field.setRobotPose(objectPose);
    }
  }

  /**
   * Returns the last calculated field pose of the tracked object.
   * @return Pose2d containing the object's position and rotation on the field
   */
  public Pose2d getPose2d() {
    if (objectPose == null) {
      return Pose2d.kZero;
    }
    return objectPose;
  }

  /**
   * Calculates the straight-line distance from the camera to the detected object.
   * Uses trigonometry with the camera height, mount angle, and target angle to compute the distance.
   * @return Distance from camera to object in the same units as camera height
   */
  public double getDistcameraToObject() {
    double alpha = camToObjectPitch;
    alpha = Math.toRadians(alpha);
    double distX = camera.getHeight() * (Math.tan(alpha));
    double distFinal = distX / Math.cos(Math.toRadians(camToObjectYaw));
    return Math.abs(distFinal);
  }

  /**
   * Calculates the translation vector from the robot's center to the detected object.
   * First calculates camera-to-object translation, then adds the camera's offset from robot center.
   * @return Translation2d from robot center to object in robot coordinates
   */
  public Translation2d getRobotToObject() {
    cameraToObject = new Translation2d(getDistcameraToObject(), Rotation2d.fromDegrees(camToObjectYaw));
    robotToObject = new Translation2d(camera.getRobotToCamPosition().getX(), camera.getRobotToCamPosition().getY()).plus(cameraToObject);
    return robotToObject;
  }

  /**
   * Calculates the translation vector from the field origin to the detected object.
   * Rotates the robot-to-object vector by the robot's field angle, then adds the robot's field position.
   * @return Translation2d from field origin to object in field coordinates 0.775
   */
  public Translation2d getOriginToObject() {
    if (robotCurrentPose.get() != null) {
      robotToObject = getRobotToObject().rotateBy(getRobotAngle.get());
      OriginToObject = robotToObject.plus(robotCurrentPose.get().getTranslation());
    } else {
      return Translation2d.kZero;
    }
    return OriginToObject;
  }

  /**
   * Returns the X coordinate of the object on the field.
   * @return X position in field coordinates
   */
  public double getX() {
    return this.OriginToObject.getX();
  }

  /**
   * Returns the Y coordinate of the object on the field.
   * @return Y position in field coordinates
   */
  public double getY() {
    return this.OriginToObject.getY();
  }
}