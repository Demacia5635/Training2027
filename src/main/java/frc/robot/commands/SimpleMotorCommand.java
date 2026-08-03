package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SimpleMotorCommand extends Command {
  private final SimpleMotorSubsystem motorSubsystem;
  private final double power;
  private final double duration;
  private double startTime;

  public SimpleMotorCommand(SimpleMotorSubsystem motorSubsystem, double power, double duration) {
    this.motorSubsystem = motorSubsystem;
    this.power = power;
    this.duration = duration;
    addRequirements(motorSubsystem);
  }

  @Override
  public void initialize() {
    startTime = Timer.getFPGATimestamp();
  }

  @Override
  public void execute() {
      motorSubsystem.setPower(power);
  }

  @Override
  public boolean isFinished() {
      return Timer.getFPGATimestamp() > startTime + duration;
  }

  @Override
  public void end(boolean interrupted) {
    motorSubsystem.stop();
  }
    
}
