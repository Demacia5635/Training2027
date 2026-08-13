package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.MyFirstSubsystemConstants;
import frc.robot.Constants.MySecondSubsystemConstants;

public class SimpleMotorSubsystem extends SubsystemBase {

    private final TalonFX driveMotor;
    private final TalonFX steerMotor;

    public SimpleMotorSubsystem() {
         driveMotor = new TalonFX(MyFirstSubsystemConstants.MotorID,MyFirstSubsystemConstants.MotorCANbus);
         steerMotor = new TalonFX(MySecondSubsystemConstants.Motor2ID,MySecondSubsystemConstants.MotorCANbus);
         
    }
      

    public void setPower(double power) {
        driveMotor.set(power);
    }

    public void stop() {
        setPower(0);
    }
    public void setBothMotorsPower(double powerSteering, double powerDrive) {
        driveMotor.set(powerDrive);
        steerMotor.set(powerSteering);
    }
    public double getAngleDegrees() {
        double rotations = steerMotor.getPosition().getValueAsDouble();
        return rotations * 360.0;
    }
       
    @Override
    public void periodic() {
        SmartDashboard.putNumber("motor angle", getAngleDegrees());
    }
}
