package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.myfirstsubsystemConstants;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.configs.Slot0Configs;

public class SimpleMotorSubsystem extends SubsystemBase {

  private final TalonFX driveMotor;
  private final TalonFX steerMotor;
  private final PositionVoltage positionVoltage = new PositionVoltage(0);

  public SimpleMotorSubsystem() {
    super();
    driveMotor = new TalonFX(Constants.myfirstsubsystemConstants.motoridDrive,
        Constants.myfirstsubsystemConstants.motorcanbus);
    steerMotor = new TalonFX(Constants.myfirstsubsystemConstants.motoridSteer,
        Constants.myfirstsubsystemConstants.motorcanbus);
    Slot0Configs slot0 = new Slot0Configs();
    slot0.kP = 2.4; // Set the proportional gain for position control
    steerMotor.getConfigurator().apply(slot0);
  }

  public void setDrivePower(double drivePower) {
    driveMotor.set(drivePower);
  }

  public void setPosition(double targetPosition) {
    steerMotor.setControl(positionVoltage.withPosition(targetPosition));
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
    SmartDashboard.putNumber("position", getSteerAngle());
    double targetAngle = SmartDashboard.getNumber("3rd cmd Target", 0.0);
    SmartDashboard.putNumber("3rd Cmd error", targetAngle - getSteerAngle());
    SmartDashboard.putNumber("Steer Position", getSteerAngle());
    SmartDashboard.putNumber("Steer Velocity", steerMotor.getVelocity().getValueAsDouble());
    SmartDashboard.putNumber("Drive Position", driveMotor.getPosition().getValueAsDouble());
    SmartDashboard.putNumber("Drive Velocity", driveMotor.getVelocity().getValueAsDouble());
}

  public void setSteerPower(double power) {
    steerMotor.set(power);
  }

  public void stop() {
    setSteerPower(0);
    setDrivePower(0);
  }

  public void setDrivePosition(double targetRotations) {
    driveMotor.setControl(new PositionVoltage(targetRotations));
  }

  public double getDrivePosition() {
    return driveMotor.getPosition().getValueAsDouble();
  }
}
