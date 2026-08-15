package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SimpleMotorSub2 extends SubsystemBase {
  private final TalonFX motor;

  // Constructor
  public SimpleMotorSub2() {
    super();
    motor = new TalonFX(Constants.SubsystemConstants.MotorID, Constants.SubsystemConstants.MotorCANbus);
  }

  // Simple power -1 to 1
  public void setPower(double power) {
    motor.set(power);
  }

  // stop
  public void stop() {
    setPower(0);
  }
}
