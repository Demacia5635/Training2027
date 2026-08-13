package frc.robot.subsystems;


import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants.PIDMotorSubsystemConstants;

public class PIDMotorSubsystem extends SubsystemBase {
    TalonFXMotor driveMotor = new TalonFXMotor(PIDMotorSubsystemConstants.CONFIG);

    PIDMotorSubsystem(){
        super();        
    }

}


