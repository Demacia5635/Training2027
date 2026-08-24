package frc.robot;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SimpleMotorSubsystem extends SubsystemBase {

    // Motor ID 2 - steering
    private final TalonFX turnMotor;

    // Motor ID 1 - drive wheel
    private final TalonFX driveMotor;

    public SimpleMotorSubsystem() {

        turnMotor = new TalonFX(
            Constants.OperatorConstants.MOTOR_ID,
            Constants.OperatorConstants.Canbus
        );

        driveMotor = new TalonFX(
            Constants.OperatorConstants.MOTOR_2_ID,
            Constants.OperatorConstants.Canbus
        );
    }

    // Used by SimpleMotorCommand - controls only the turning motor
    public void setPower(double power) {
        turnMotor.set(power);
    }

    // Used by TwoMotorCommand - controls both motors
    public void setTwoPowers(double turnPower, double drivePower) {
        turnMotor.set(turnPower);
        driveMotor.set(drivePower);
    }

    // Stop both motors
    public void stop() {
        turnMotor.set(0);
        driveMotor.set(0);
    }
}