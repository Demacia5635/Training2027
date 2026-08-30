// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Power;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {
  private final TalonFX motorSteer;
  private final TalonFX motorDrive;

  /** Creates a new SimpleMotorSubsystem. */
  public SimpleMotorSubsystem() {
    super();
    motorSteer = new TalonFX(Constants.OperatorConstants.STEER_ID, Constants.OperatorConstants.MotorCANbus);
    motorDrive = new TalonFX(Constants.OperatorConstants.DRIVE_ID, Constants.OperatorConstants.MotorCANbus);
    SmartDashboard.putData(this);
  }

  public void setPowersteer(double Power) {
    motorSteer.set(Power);
  }

  public void setPowerdrive(double Power) {
    motorDrive.set(Power);
  }

   public void setAnglesteer(double angle) {
    motorSteer.setPosition(angle);
  }

  public void setDistancedrive(double Distance) {
    motorDrive.setPosition(Distance);
  }

  public void stop() {
    motorSteer.set(0);
    motorDrive.set(0);
  }

  public void setDifrentPower(double Power) {
    motorSteer.set(Power);
    motorDrive.set(Power);
  }

  @Override
  public void initSendable(SendableBuilder builder) {
      builder.addDoubleProperty("steer angle", () -> {return motorSteer.getPosition().getValueAsDouble();}, null);
      builder.addDoubleProperty("drive distance", () -> {return motorDrive.getPosition().getValueAsDouble();}, null);
  }
}
