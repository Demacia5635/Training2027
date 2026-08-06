package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {

    TalonFX motor = new TalonFX(Constants.SimpleMotorSubsystemConstants.motorID, Constants.SimpleMotorSubsystemConstants.motorCANBUS);

    public SimpleMotorSubsystem(){
        super();
    }

    public void setPower(double power){
        motor.set(power);
    }
    
    public void stop(){
        setPower(0);
    }
}
