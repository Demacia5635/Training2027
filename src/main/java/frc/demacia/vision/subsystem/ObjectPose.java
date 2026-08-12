// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.vision.subsystem;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.vision.Camera;

// Subsystem that tracks and calculates the position of a vision target (object) on the field
public class ObjectPose extends SubsystemBase {
  private Translation2d robotToObject;
  private Translation2d cameraToObject;
  private Translation2d OriginToObject;

  private NetworkTable Table;
  private Field2d field;
  private Field2d robotfield;


  private double camToObjectYaw;
  private double camToObjectPitch;

  
  private Supplier<Rotation2d> getRobotAngle;
  private Supplier<Pose2d> robotCurrentPose;


  private Camera camera;
  private Pose2d objectPose;

/**
   * Constructor - Initializes the object tracker with camera configuration and robot position suppliers.
   * Sets up the NetworkTable connection to receive vision data from the camera.
   */
  public ObjectPose(Camera camera, Supplier<Rotation2d> getRobotAngle,Supplier<Pose2d> robotCurrentPose) {
    this.getRobotAngle = getRobotAngle;
    this.robotCurrentPose = robotCurrentPose;
    field = new Field2d();
    robotfield = new Field2d();



    this.camera = camera;
    Table = NetworkTableInstance.getDefault().getTable(camera.getTableName());

     SmartDashboard.putData("fieldObject" + camera.getName(), field);
     SmartDashboard.putData("fieldrobot" + camera.getName(), robotfield);

  }

  /**
   * Periodic method called every robot loop (~20ms).
   * Reads the latest vision data (pitch, yaw) from NetworkTables and updates the object's field position
   * if a valid target is detected.
   */
  @Override
  public void periodic() {
    camToObjectPitch = Table.getEntry("ty").getDouble(0.0)+ camera.getPitch();
    camToObjectYaw = (-Table.getEntry("tx").getDouble(0.0)) + camera.getYaw();
    if(Table.getEntry("tv").getDouble(0.0) != 0){
      objectPose = new Pose2d(getOriginToObject(), getRobotAngle.get());
      field.setRobotPose(objectPose);
      // robotfield.setRobotPose(robotCurrentPose.get());
    }
    
  }

   /**
   * Returns the last calculated field pose of the tracked object.
   * @return Pose2d containing the object's position and rotation on the field
   */
  public Pose2d getPose2d(){
    if(objectPose == null){
      return Pose2d.kZero;
    }
    return objectPose;
  }

  /**
   * Calculates the straight-line distance from the camera to the detected object.
   * Uses trigonometry with the camera height, mount angle, and target angle to compute the distance.
   * @return Distance from camera to object in the same units as camera height
   */
  public double getDistcameraToObject(){
    double alpha = camToObjectPitch;
    alpha = Math.toRadians(alpha);
    double distX =  camera.getHeight()*(Math.tan(alpha));
    double distFinal = distX /Math.cos(Math.toRadians(camToObjectYaw));
    return Math.abs(distFinal);
  }

   /**
   * Calculates the translation vector from the robot's center to the detected object.
   * First calculates camera-to-object translation, then adds the camera's offset from robot center.
   * @return Translation2d from robot center to object in robot coordinates
   */
  public Translation2d getRobotToObject(){
    cameraToObject = new Translation2d(getDistcameraToObject(),Rotation2d.fromDegrees(camToObjectYaw));
    robotToObject = new Translation2d(camera.getRobotToCamPosition().getX(), camera.getRobotToCamPosition().getY()).plus(cameraToObject);
    return robotToObject;
  }

  /**
   * Calculates the translation vector from the field origin to the detected object.
   * Rotates the robot-to-object vector by the robot's field angle, then adds the robot's field position.
   * @return Translation2d from field origin to object in field coordinates 0.775
   */
  public Translation2d getOriginToObject(){
    if(robotCurrentPose.get() != null){
      robotToObject = getRobotToObject().rotateBy(getRobotAngle.get());
      OriginToObject = robotToObject.plus(robotCurrentPose.get().getTranslation());
    }
    else{
      return Translation2d.kZero;
    }
    return OriginToObject;
  }

  /**
   * Configures the Shuffleboard/SmartDashboard display for this subsystem.
   * Adds the X and Y coordinates of the tracked object to the dashboard.
   */
  @Override
  public void initSendable(SendableBuilder builder) {
      builder.addDoubleProperty("object pos X:", this::getX, null);
      builder.addDoubleProperty("object pos Y:", this::getY, null);
  }

  
  /**
   * Returns the X coordinate of the object on the field.
   * @return X position in field coordinates
   */
  public double getX(){
    return this.OriginToObject.getX();
  }

  /**
   * Returns the Y coordinate of the object on the field.
   * @return Y position in field coordinates
   */
  public double getY(){
    return this.OriginToObject.getY();
  }

  
}