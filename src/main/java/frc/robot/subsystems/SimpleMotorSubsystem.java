package frc.robot.subsystems;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants;
import frc.robot.Constants.myfirstsubsystemConstants;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.configs.Slot0Configs;

public class SimpleMotorSubsystem extends SubsystemBase {

  private final TalonFXMotor driveMotor;
  private final TalonFXMotor steerMotor;
  private final PositionVoltage positionVoltage = new PositionVoltage(0);
  
  public double targetSteerPosition;
  public double targetDrivePosition;

  public SimpleMotorSubsystem() {
    super();
    driveMotor = new TalonFXMotor(Constants.driverConstants.DRIVE_CONFIG);
    steerMotor = new TalonFXMotor(Constants.driverConstants.STEER_CONFIG);
    Slot0Configs slot0 = new Slot0Configs();
    slot0.kP = 2.4; // Set the proportional gain for position control+
    steerMotor.getConfigurator().apply(slot0);
    SmartDashboard.putData("subsystem", this);
  }

  public void setDrivePower(double drivePower) {
    driveMotor.set(drivePower);
  }

  public void setSteerPosition(double targetPosition) {
    steerMotor.setPositionVoltage(targetPosition);
  }

  public void setDrivePosition(double targetdrivePosition) {
    driveMotor.setPositionVoltage(targetdrivePosition);
  }

  public double getDrivePower() {
    return driveMotor.get();
  }

  public double getSteerPower() {
    return steerMotor.get();
  }

  public double getSteerAngle() {
    return steerMotor.getPosition().getValueAsDouble();
  }

  @Override
  public void periodic() {
    // SmartDashboard.putNumber("Steer Position", getSteerAngle());
    // SmartDashboard.putNumber("Steer Velocity",
    // steerMotor.getVelocity().getValueAsDouble());
    // SmartDashboard.putNumber("Drive Position", getDrivePosition());
    // SmartDashboard.putNumber("Drive Velocity",
    // driveMotor.getVelocity().getValueAsDouble());
    // SmartDashboard.putNumber("set drive position ", setDrivePosition(0));
  }


  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("Steer Position!!!!", this::getSteerAngle, (value) -> targetSteerPosition = value);
    builder.addDoubleProperty("Drive Vel!!!!", this::getDrivePosition, (value) -> targetDrivePosition = value); 
  }

  public void setSteerVelocity(double targetVelocity) {
    steerMotor.setVelocity(targetVelocity);
  }
  public void setDriveVelocity(double targetVelocity) {
    driveMotor.setVelocity(targetVelocity);
  }
  public void setSteerPower(double power) {
    steerMotor.set(power);
  }

  public void stop() {
    setSteerPower(0);
    setDrivePower(0);
  }

  public double getDrivePosition() {
    return driveMotor.getPosition().getValueAsDouble();
  }
}
