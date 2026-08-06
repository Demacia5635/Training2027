package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;


public class SimpleMotorSubsystem extends SubsystemBase {
    private final TalonFX motor;


    public SimpleMotorSubsystem() {
        super();
        motor = new TalonFX(Constants.OperatorConstants.MOTOR_ID,Constants.OperatorConstants.MOTOR_CANBUS);
    }



    // set motor power
    public void setPower(double power) {
        motor.set(power);
    }


    // stop the motor
    public void stop() {
        setPower(0);
    }
}
