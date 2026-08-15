package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {
  private final TalonFX motor1;
  private final TalonFX motor2;

// Constructor
  public SimpleMotorSubsystem () {
    super();
    motor1 = new TalonFX(Constants.SubsystemConstants.MotorID1, Constants.SubsystemConstants.MotorCANbus);
    motor2 = new TalonFX(Constants.SubsystemConstants.MotorID2, Constants.SubsystemConstants.MotorCANbus);
  }
// Simple power -1 to 1
  public void setPower(double power1, double power2) {
   motor1.set(power1);
   motor2.set(power2);
  }
// stop
  public void stop() {
    setPower(0, 0);
  }
}