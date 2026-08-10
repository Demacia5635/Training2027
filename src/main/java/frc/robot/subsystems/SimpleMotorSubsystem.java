package frc.robot.subsystems;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.OperatorConstants;

import java.io.ObjectInputValidation;

import com.ctre.phoenix6.hardware.TalonFX;

public class SimpleMotorSubsystem extends SubsystemBase {

   // המנועים שלנו
  private final TalonFX STEERMOTOR;
  private final TalonFX DRIVEMOTOR;

// Constructor
  public SimpleMotorSubsystem() {
    super();
    STEERMOTOR = new TalonFX(Constants.OperatorConstants.STEERMOTOR_ID, Constants.OperatorConstants.Canbus);
    DRIVEMOTOR = new TalonFX(Constants.OperatorConstants.DRIVEMOTOR_ID, Constants.OperatorConstants.Canbus);
    
  }
// Simple power -1 to 1
  public void setPower(double power) {
   STEERMOTOR.set(power);
  }
// stop
  public void stop() {
    setPower(0);
  }















  double getMotorPosition() {
    return STEERMOTOR.getPosition().getValueAsDouble();
  }
  @Override
  public void periodic() {
     SmartDashboard.putNumber("Motor Position", getMotorPosition());
     double toRadiants = getMotorPosition() * (2 * Math.PI);
     SmartDashboard.putNumber("Motor Position (Degrees)", Math.toDegrees(toRadiants));
    }

  }



