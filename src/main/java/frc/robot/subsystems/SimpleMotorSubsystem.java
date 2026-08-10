package frc.robot.subsystems;
import frc.robot.Constants;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SimpleMotorSubsystem extends SubsystemBase {

    private final TalonFX motor1;
    private final TalonFX motor2;

    public SimpleMotorSubsystem() {

        super();

        motor1 = new TalonFX(
            Constants.SimpleMotorConstants.Motor1ID,
            Constants.SimpleMotorConstants.MotorCANbus
        );

        motor2 = new TalonFX(
            Constants.SimpleMotorConstants.Motor2ID,
            Constants.SimpleMotorConstants.MotorCANbus
        );
    }

    public void setMotor1Power(double power) {
        motor1.set(power);
    }

    public void setMotor2Power(double power) {
        motor2.set(power);
    }

    public double getMotor1Pos() {
        return motor1.getPosition().getValueAsDouble();
    }

    public void stop() {
        motor1.set(0);
        motor2.set(0);
    }


    @Override
    public void periodic() {
        SmartDashboard.putNumber("Left Motor Speed", getMotor1Pos());
    }
}