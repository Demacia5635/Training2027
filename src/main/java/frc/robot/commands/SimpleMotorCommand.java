package frc.robot.commands;

import edu.wpi.first.units.measure.Power;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;


public class SimpleMotorCommand extends Command{
private final double power;
private SimpleMotorSubsystem subsystem;
private final double duration;
private Timer timer = new Timer();
private double startTime=0;
public SimpleMotorCommand( SimpleMotorSubsystem subsystem, double power, double duration) {
    this.power = power;
    this.subsystem = subsystem;
    this.duration = duration;
    addRequirements(subsystem);
}
@Override
public void initialize() {
    startTime = Timer.getFPGATimestamp();
    timer.restart();
    System.out.println("command started at " + startTime + " for duration " + duration +  " with power " + power);
}
@Override 
public void execute() {
    subsystem.setPower(power);
}
@Override
public boolean isFinished() {
    return Timer.getFPGATimestamp() - startTime >= duration;
}
@Override
public void end(boolean interrupted) {
    subsystem.stop();
    System.out.println("command ended at " + timer.getFPGATimestamp());
}
}