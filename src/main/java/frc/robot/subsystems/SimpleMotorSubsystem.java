package frc.robot.subsystems;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;
import frc.demacia.utils.motors.TalonFXConfig;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {
  private static final double KP = 0;
  private static final int ID = 0;
  private static final Canbus CAN_BUS = null;
  private static final String NAME = null;
  private static final double KI = 0;
  private static final double KD = 0;
  private static final double KS = 0;
  private static final double KV = 0;
  private static final double KA = 0;
  private static final double KG = 0;
  private final TalonFX motor1;
  private final TalonFX motor2;

  // Constructor
  public SimpleMotorSubsystem() {
    super();
    motor1 = new TalonFX(Constants.SubsystemConstants.DRIVE_ID1, Constants.SubsystemConstants.MotorCANbus);
    SmartDashboard.putData("Subsystem 1", this);
    motor2 = new TalonFX(Constants.SubsystemConstants.STEER_ID2, Constants.SubsystemConstants.MotorCANbus);
  }

  public static final TalonFXConfig CONFIG = new TalonFXConfig(ID, CAN_BUS, NAME)
      .withPID(KP, KI, KD, KS, KV, KA, KG);

  // Simple power -1 to 1
  public void setPower(double power1, double power2) {
    motor1.set(power1);
    motor2.set(power2);
  }

  // stop
  public void stop() {
    setPower(0, 0);
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Motor 1 Speed", motor1.get());
    SmartDashboard.putNumber("Motor 2 Speed", motor2.get());

  }

  public void setPosition(double position) {
    motor1.set(position);
    motor2.set(position);
  }

  public void setVelocity(double velocity) {
    motor1.set(velocity);
    motor2.set(velocity);
  }

  public double getVelocity() {
    return motor1.get();
  }

  public double getPosition() {
    return motor1.get();
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("position", this::getPosition, this::setPosition);
    builder.addDoubleProperty("Velocity", this::getVelocity, this::setVelocity);
  }

}