package frc.robot.subsystems;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.myfirstsubsystemConstants;

public class SimpleMotorSubsystem extends SubsystemBase{

private final TalonFX motor;
  public SimpleMotorSubsystem() {
    super();
    motor = new TalonFX(Constants.myfirstsubsystemConstants.motorId, Constants.myfirstsubsystemConstants.motorcanbus);
  }
  public void setpower(double power) {
    motor.set(power);
  }
  public void stop() {
    setpower(0);
  }
}
