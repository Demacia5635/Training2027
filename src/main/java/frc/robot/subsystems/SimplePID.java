package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants.SimplePIDConstants;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SimplePID extends SubsystemBase {
    private TalonFXMotor motor;
    private double targetVelocity = 0.0;
    
    public SimplePID() {
        motor = new TalonFXMotor(SimplePIDConstants.CONFIG);
        SmartDashboard.putData("pid", this);
    }

    public void setVelocity(double velocity) {
        targetVelocity = velocity;
        motor.setVelocity(velocity);
    }

    public double getVelocity() {
        return motor.getCurrentVelocity();
    }

    public void setVoltage(double voltage) {
        motor.setVoltage(voltage);
    }
    
    public double getVoltage() {
        return motor.getCurrentVoltage();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("SimplePID Dashboard");
        // builder.addDoubleProperty("Target Voltage", this::getVoltage, this::setVoltage);
        builder.addDoubleProperty("Motor Velocity", this::getVelocity, null);
        builder.addDoubleProperty("Motor Velocity", this::getVelocity, null);
        builder.addDoubleProperty("Target Velocity", () -> targetVelocity, this::setVelocity);
    }
}
