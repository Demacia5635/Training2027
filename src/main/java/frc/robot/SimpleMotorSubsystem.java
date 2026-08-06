package frc.robot;

import com.ctre.phoenix6.hardware.TalonFX;

public class SimpleMotorSubsystem {

   // המנועים שלנו
  private final TalonFX motor;

// Constructor
  public SimpleMotorSubsystem() {
    super();
    motor = new TalonFX(Constants.OperatorConstants.MOTOR_ID, Constants.OperatorConstants.Canbus);
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


