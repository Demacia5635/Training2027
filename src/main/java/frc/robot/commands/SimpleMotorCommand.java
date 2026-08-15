package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SimpleMotorCommand extends Command {
  private final SimpleMotorSubsystem subsystem;
  private final double power1;
  private final double power2;
  private final double duration;
  private double startTime = 0;
  private Timer timer = new Timer();

  public SimpleMotorCommand(SimpleMotorSubsystem subsystem, double power1, double power2, double duration) {
    this.subsystem = subsystem;
    this.power1 = power1;
    this.power2 = power2;
    this.duration = duration;
    addRequirements(subsystem);
  }

  @Override
  public void initialize() {
    startTime = Timer.getFPGATimestamp();
    timer.restart();
    System.out.println("Init Command started at: " + startTime +
      "seconds for " + duration + " seconds with power 1: " + power1 + " seconds with power 2: " + power2);
  }

  @Override
  public void execute() {
      System.out.println("execute Command started at: " + startTime +
      "seconds for " + duration + " seconds with power 1: " + power1 + " seconds with power 2: " + power2);
    subsystem.setPower(power1, power2);
  }

  @Override
  public boolean isFinished() {
    return Timer.getFPGATimestamp() >=
      startTime + duration || timer.hasElapsed(duration);
    //    return timer.hasElapsed(duration);
  }

  @Override
  public void end(boolean interrupted) {
    subsystem.stop();
    System.out.println("Command ended at: " +
      Timer.getFPGATimestamp());
  }
}