package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase { 
private final TalonFX SteerMotor;
private final TalonFX DriveMotor;

public SimpleMotorSubsystem() {
    super();
    this.SteerMotor = new TalonFX( Constants.OperatorConstants.SteerMotor_id, Constants.OperatorConstants.Canbus);
    this.DriveMotor = new TalonFX( Constants.OperatorConstants.DriveMotor_id, Constants.OperatorConstants.Canbus);

}
public void setSteerPower(double power) {
    SteerMotor.set(power);

}


public void stopSteer() {
    setSteerPower(0);

}

public void setDrivePower(double power) {
    SteerMotor.set(power);
    DriveMotor .set(power);

}


public void stopDrive() {
    setDrivePower(0);

}

}