package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class HomeworkMotorSubsystem extends SubsystemBase {
    TalonFX motor1 = new TalonFX(Constants.HomeworkMotorSubsystemConstants.motor1ID, Constants.HomeworkMotorSubsystemConstants.motor1CANBUS);
    TalonFX motor2 = new TalonFX(Constants.HomeworkMotorSubsystemConstants.motor2ID, Constants.HomeworkMotorSubsystemConstants.motor2CANBUS);

    public HomeworkMotorSubsystem(){
        super();
    }

    public void motor1SetPower(double power){
        motor1.set(power);
    }

    public void motor2SetPower(double power){
        motor2.set(power);
    }

    public void motor1Stop(){
        motor1.set(0);
    }

     public void motor2Stop(){
        motor2.set(0);
    }
}
