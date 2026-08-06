package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

public class SimpleMotorSubsystemCommand extends Command {
  private  SimpleMotorSubsystem subsystem;
  private final double power;
  private final double duration;
  private double startTime = 0;
  private Timer timer = new Timer();


 public SimpleMotorSubsystem(SimpleMotorSubsystem subsystem, double power, double duration) {
    this.subsystem = subsystem;
    this.power = power;
    this.duration = duration;
    addRequirements(subsystem);

    
  @Override
  public void initialize() {
    startTime = Timer.getFPGATimestamp();
    timer.restart();
    System.out.println("Command started at: " + startTime +
      "seconds for " + duration + " seconds with power: " + power);
  }
}
}

  

  


