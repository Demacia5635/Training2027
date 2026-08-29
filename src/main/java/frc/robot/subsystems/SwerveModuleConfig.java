package frc.robot.subsystems;


import edu.wpi.first.math.geometry.Translation2d;
import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.sensors.CancoderConfig;

public class SwerveModuleConfig {
  public final String name;
  public final TalonFXConfig steerConfig;
  public final TalonFXConfig driveConfig;
  public final CancoderConfig cancoderConfig;
  public final double steerOffset;
  Translation2d position;

  public SwerveModuleConfig(
      String name,
      TalonFXConfig steerConfig,
      TalonFXConfig driveConfig,
      CancoderConfig cancoderConfig,
      Translation2d position,
      double steerOffset) {
    this.name = name;
    this.position = position;
    this.steerConfig = steerConfig;
    this.driveConfig = driveConfig;
    this.cancoderConfig = cancoderConfig;
    this.steerOffset = steerOffset;
  }
}