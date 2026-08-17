package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants.SimplePIDConstants;

import edu.wpi.first.util.sendable.SendableBuilder;

public class SimplePID extends SubsystemBase {
    private TalonFXMotor motor;
    
    public SimplePID() {
        motor = new TalonFXMotor(SimplePIDConstants.CONFIG);
        SmartDashboard.putData("pid", this);
    }

    public double getVelocity() {
        return motor.getCurrentVelocity();
    }

    public void setVoltage(double voltage) {
        motor.setVoltage(voltage);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("SimplePID Dashboard");
        builder.addDoubleProperty("Target Voltage", null, this::setVoltage);
        builder.addDoubleProperty("Motor Velocity", this::getVelocity, null);
    }
}
