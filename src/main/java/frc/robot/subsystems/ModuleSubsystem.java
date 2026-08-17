package frc.robot.subsystems;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.demacia.utils.sensors.Cancoder;
import frc.robot.Constants.ModuleSubsystemConstants;

public class ModuleSubsystem extends SubsystemBase {
    private TalonFXMotor steerMotor;
    private TalonFXMotor driveMotor;
    private Cancoder     cancoder;

    public ModuleSubsystem() {
        steerMotor = new TalonFXMotor(ModuleSubsystemConstants.STEER_CONFIG);
        driveMotor = new TalonFXMotor(ModuleSubsystemConstants.DRIVE_CONFIG);
        cancoder   = new Cancoder(ModuleSubsystemConstants.CANCODER_CONFIG);
    }

    // velocity, position, power.
    // meter/drive, degrees in steer.

    public double getDriveVelocity() {
        return driveMotor.getCurrentVelocity();
    }

    public double getDrivePosition() {
        return driveMotor.getCurrentPosition();
    }

    public double getDrivePower() {
        return driveMotor.get();
    }

    public double getSteerVelocity() {
        return steerMotor.getCurrentVelocity();
    }

    public double getSteerDegrees() {
        return steerMotor.getCurrentAngle() * 180.0 / Math.PI;
    }

    public double getSteerPower() {
        return steerMotor.get();
    }

    public double getAbsDegrees() {
        return cancoder.getCurrentAbsPosition() * 180.0 / Math.PI;
    }

    // Cancoder functions ...

    public void setDrivePower(double power) {
        driveMotor.setDuty(power);
    }

    public void setSteerPower(double power) {
        steerMotor.setDuty(power);
    }

    public void setSteerAngle(double angle) {
        steerMotor.setAngle(angle);
    }
    
    void calibrate() {
        driveMotor.setEncoderPosition(0);
        steerMotor.setEncoderPosition(0);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("ModuleSubsystem");
        builder.addDoubleProperty("Drive Velocity", this::getDriveVelocity, null);
        builder.addDoubleProperty("Drive Position", this::getDrivePosition, null);
        builder.addDoubleProperty("Drive Power", this::getDrivePower, null);
        builder.addDoubleProperty("Steer Velocity", this::getSteerVelocity, null);
        builder.addDoubleProperty("Steer Degrees", this::getSteerDegrees, null);
        builder.addDoubleProperty("Steer Power", this::getSteerPower, null);
        builder.addDoubleProperty("Cancoder Absolute Degree", this::getAbsDegrees, null);
    }
}
