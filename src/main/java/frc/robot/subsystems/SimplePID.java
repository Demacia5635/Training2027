package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants.SimplePIDConstants;;

public class SimplePID extends SubsystemBase {
    private TalonFXMotor motor;
    
    public SimplePID() {
        TalonFXConfig CONFIG = new TalonFXConfig(SimplePIDConstants.CANBUS_ID, SimplePIDConstants.CANBUS, SimplePIDConstants.NAME)
            .withPID(0.0, 0.0, 0.0, SimplePIDConstants.KS, SimplePIDConstants.KV, 0.0, 0.0);
    
        motor = new TalonFXMotor(CONFIG);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("SimplePID/Velocity", getVelocity());
    }

    public double getVelocity() {
        return motor.getCurrentVelocity();
    }

    public void setVoltage(double voltage) {
        motor.setVoltage(voltage);
    }
}
