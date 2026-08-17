package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants.MyFirstSubsystemConstants;
import frc.robot.Constants.MySecondSubsystemConstants;

public class SimpleMotorSubsystem extends SubsystemBase {

    private final TalonFXMotor driveMotor;
    private final TalonFX steerMotor;
    public SimpleMotorSubsystem() {
         driveMotor = new TalonFXMotor(MySecondSubsystemConstants.DRIVE_CONFIG);
         steerMotor = new TalonFXMotor(MySecondSubsystemConstants.STEER_ID,MySecondSubsystemConstants.DRIVE_CANbus);

         
    }
    



    

    public void setPower(double power) {
        driveMotor.set(power);
    }
    public void setSteerPower(double power) {
    steerMotor.set(power);
    }


    public void stopMotor() {
        setPower(0);
    }
    public void stopSteer() {
    steerMotor.set(0);
    }
    public void stopBoth() {
    driveMotor.set(0);
    steerMotor.set(0);
    }
    public void setBothMotorsPower(double powerSteering, double powerDrive) {
        driveMotor.set(powerDrive);
        steerMotor.set(powerSteering);
    }
    public double getAngleDegrees() {
        double rotations = steerMotor.getPosition().getValueAsDouble();
        return rotations * 360.0;
    }
    public double getDriveRotations() {
    return driveMotor.getPosition().getValueAsDouble();
    }
    public double getDrivePosition() {
    double motorRotations = driveMotor.getPosition().getValueAsDouble();
    double wheelDiameterMeters = 4 * 0.0254;
    double wheelCircumference = Math.PI * wheelDiameterMeters;
    double driveGearRatio = 0.25;

    return motorRotations * driveGearRatio * wheelCircumference;
    }
    public double getDriveVelocity() {
    return driveMotor.getVelocity().getValueAsDouble();
    }
    public double getSteerVelocity() {
    return steerMotor.getVelocity().getValueAsDouble();
    }
    public void setDriveVolt(double volt){
        driveMotor.setVoltage(volt);
    }
    
    //@Override
     // public void periodic() {
      //SmartDashboard.putNumber("Steer Angle", getAngleDegrees());
     //SmartDashboard.putNumber("Steer Velocity", getSteerVelocity());

     //SmartDashboard.putNumber("Drive Position", getDrivePosition());
     //SmartDashboard.putNumber("Drive Velocity", getDriveVelocity());
     

    //}


    public void stop() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }

    
}   