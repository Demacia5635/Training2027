package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.MyFirstSubsystemConstants;
import frc.robot.Constants.MySecondSubsystemConstants;

public class SimpleMotorSubsystem extends SubsystemBase {

    private final TalonFX drivemotor;
    private final TalonFX steermotor;

    public SimpleMotorSubsystem() {
         drivemotor = new TalonFX(MyFirstSubsystemConstants.MotorID,MyFirstSubsystemConstants.MotorCANbus);
         steermotor = new TalonFX(MySecondSubsystemConstants.Motor2ID,MySecondSubsystemConstants.MotorCANbus);
    }
      

    public void setPower(double power) {
        drivemotor.set(power);
    }

    public void stop() {
        setPower(0);
    }
    public void setBothMotorsPower(double powerSteering, double powerDrive) {
        drivemotor.set(powerDrive);
        steermotor.set(powerSteering);
    }
}