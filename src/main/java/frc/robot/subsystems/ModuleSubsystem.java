package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ModuleConstants;

public class ModuleSubsystem extends SubsystemBase {
    private final TalonFX driveMotor;
    private final TalonFX steerMotor;
    private final CANcoder absoluteEncoder;

    private final SimpleMotorFeedforward driveFeedforward;
    private final DutyCycleOut dutyCycleRequest = new DutyCycleOut(0);
    private final VelocityVoltage velocityRequest = new VelocityVoltage(0);
    private final PositionVoltage positionRequest = new PositionVoltage(0);

    public ModuleSubsystem() {
        driveMotor = new TalonFX(ModuleConstants.DRIVE_ID);
        steerMotor = new TalonFX(ModuleConstants.STEER_ID);
        absoluteEncoder = new CANcoder(ModuleConstants.CANCODER_ID);

        TalonFXConfiguration driveConfig = new TalonFXConfiguration();
        TalonFXConfiguration steerConfig = new TalonFXConfiguration();

        driveConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive; 
        steerConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive; 

        driveConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        steerConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        driveConfig.Slot0.kP = ModuleConstants.DRIVE_kP;
        driveConfig.Slot0.kI = ModuleConstants.DRIVE_kI;
        driveConfig.Slot0.kD = ModuleConstants.DRIVE_kD;

        steerConfig.Slot0.kP = ModuleConstants.STEER_kP;
        steerConfig.Slot0.kI = ModuleConstants.STEER_kI;
        steerConfig.Slot0.kD = ModuleConstants.STEER_kD;

        driveMotor.getConfigurator().apply(driveConfig);
        steerMotor.getConfigurator().apply(steerConfig);

        driveFeedforward = new SimpleMotorFeedforward(ModuleConstants.DRIVE_kS, ModuleConstants.DRIVE_kV, 0);

        resetToAbsolute();
        
        SmartDashboard.putData("Module Subsystem", this);
    }


    public double getDrivePositionMeters() {
        return driveMotor.getPosition().refresh().getValueAsDouble() * ModuleConstants.DRIVE_ROTATIONS_TO_METERS;
    }

    public double getDriveVelocityMetersPerSec() {
        return driveMotor.getVelocity().refresh().getValueAsDouble() * ModuleConstants.DRIVE_ROTATIONS_TO_METERS;
    }

    public double getDriveAppliedOutput() {
        return driveMotor.getDutyCycle().refresh().getValueAsDouble();
    }

    public double getSteerAngleDegrees() {
        return steerMotor.getPosition().refresh().getValueAsDouble() * ModuleConstants.STEER_ROTATIONS_TO_DEGREES;
    }

    public double getSteerVelocityDegreesPerSec() {
        return steerMotor.getVelocity().refresh().getValueAsDouble() * ModuleConstants.STEER_ROTATIONS_TO_DEGREES;
    }

    public double getSteerAppliedOutput() {
        return steerMotor.getDutyCycle().refresh().getValueAsDouble();
    }

    public double getAbsoluteEncoderDegrees() {
        double rawAngle = absoluteEncoder.getAbsolutePosition().refresh().getValueAsDouble() * 360.0;
        double calibratedAngle = rawAngle - ModuleConstants.CANCODER_OFFSET_DEGREES;
        
        return Math.IEEEremainder(calibratedAngle, 360.0);
    }

    public void resetToAbsolute() {
        double absoluteRotations = getAbsoluteEncoderDegrees() / ModuleConstants.STEER_ROTATIONS_TO_DEGREES;
        steerMotor.setPosition(absoluteRotations);
    }


    public void setDrivePower(double power) {
        driveMotor.setControl(dutyCycleRequest.withOutput(power));
    }

    public void setSteerPower(double power) {
        steerMotor.setControl(dutyCycleRequest.withOutput(power));
    }

    public void setDriveVelocity(double velocityMetersPerSec) {
        double targetRotationsPerSec = velocityMetersPerSec / ModuleConstants.DRIVE_ROTATIONS_TO_METERS;
        
        double feedforwardVoltage = driveFeedforward.calculate(velocityMetersPerSec);
        
        driveMotor.setControl(velocityRequest.withVelocity(targetRotationsPerSec).withFeedForward(feedforwardVoltage));
    }

    public void setSteerAngle(double targetAngleDegrees) {
        double targetRotations = targetAngleDegrees / ModuleConstants.STEER_ROTATIONS_TO_DEGREES;
        steerMotor.setControl(positionRequest.withPosition(targetRotations));
    }

    public void stopModule() {
        setDrivePower(0);
        setSteerPower(0);
    }

    @Override
    public void periodic() {
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Subsystem");
        builder.addDoubleProperty("Drive Position (m)", this::getDrivePositionMeters, null);
        builder.addDoubleProperty("Drive Velocity (m_s)", this::getDriveVelocityMetersPerSec, null);
        builder.addDoubleProperty("Drive Power", this::getDriveAppliedOutput, null);
        
        builder.addDoubleProperty("Steer Angle (deg)", this::getSteerAngleDegrees, null);
        builder.addDoubleProperty("Steer Velocity (deg_s)", this::getSteerVelocityDegreesPerSec, null);
        builder.addDoubleProperty("Steer Power", this::getSteerAppliedOutput, null);
        
        builder.addDoubleProperty("Absolute Encoder (deg)", this::getAbsoluteEncoderDegrees, null);
    }
}
