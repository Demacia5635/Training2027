package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class HomeworkMotorSubsystem extends SubsystemBase {
    TalonFX driveMotor = new TalonFX(Constants.HomeworkMotorSubsystemConstants.DRIVER_ID, Constants.HomeworkMotorSubsystemConstants.DRIVER_CANBUS);
    TalonFX steerMotor = new TalonFX(Constants.HomeworkMotorSubsystemConstants.STEER_ID, Constants.HomeworkMotorSubsystemConstants.STEER_CANBUS);

    public HomeworkMotorSubsystem(){
        super();
    }

    public void driveSetPower(double power){
        driveMotor.set(power);
    }

    public void steerSetPower(double power){
        steerMotor.set(power);
    }

    public void driveStop(){
        driveMotor.set(0);
    }

     public void steerStop(){
        steerMotor.set(0);
    }
}
