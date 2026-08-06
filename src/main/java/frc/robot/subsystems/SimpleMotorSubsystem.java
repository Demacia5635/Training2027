package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase { 
private final TalonFX motor;

public SimpleMotorSubsystem() {
    super();
    this.motor = new TalonFX( Constants.OperatorConstants.MOTOR_ID, Constants.OperatorConstants.Canbus);
}
public void setPower(double power) {
    motor.set(power);
}


public void stop() {
    setPower(0);

}
}