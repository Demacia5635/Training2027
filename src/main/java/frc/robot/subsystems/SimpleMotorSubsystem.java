package frc.robot.subsystems;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.myfirstsubsystemConstants;

public class SimpleMotorSubsystem extends SubsystemBase{

private final TalonFX driveMotor;
private final TalonFX steerMotor;
  public SimpleMotorSubsystem() {
    super();
    driveMotor = new TalonFX(Constants.myfirstsubsystemConstants.motoridDrive, Constants.myfirstsubsystemConstants.motorcanbus);
    steerMotor = new TalonFX(Constants.myfirstsubsystemConstants.motoridSteer, Constants.myfirstsubsystemConstants.motorcanbus);
  }

  public void setDrivePower(double drivePower) {
    driveMotor.set(drivePower);
  }
  public void setSteerPower(double steerPower) {
    steerMotor.set(steerPower);
  }
  public void stop() {
    setSteerPower(0);
    setDrivePower(0);
  }
}
