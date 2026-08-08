package frc.robot.commands;


import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.HomeworkMotorSubsystem;

public class HomeworkMotorCommand extends Command {
    private final HomeworkMotorSubsystem motorSubsystem;
    private final double motor1Power;
    private final double motor2Power;
    private final double motor1Duration;
    private final double motor2Duration;
    private double startTime = 0;
    private Timer timer = new Timer();

   public HomeworkMotorCommand(HomeworkMotorSubsystem motorSubsystem, double motor1Power, double motor2Power, double motor1Duration, double motor2Duration) {
    this.motorSubsystem = motorSubsystem;
    this.motor1Power = motor1Power;
    this.motor2Power = motor2Power;
    this.motor1Duration = motor1Duration;
    this.motor2Duration = motor2Duration;
    addRequirements(motorSubsystem);
   }


   @Override
   public void initialize(){
        startTime = Timer.getFPGATimestamp();
        timer.restart();
        System.out.println("Motor 1 started at + " + startTime + " for " + motor1Duration + " with " + motor1Power);
        System.out.println("Motor 2 started at + " + startTime + " for " + motor2Duration + " with " + motor2Power);
   }

   @Override
   public void execute(){
    motorSubsystem.motor1SetPower(motor1Power);
    motorSubsystem.motor2SetPower(motor2Power);
   }

   @Override
   public boolean isFinished(){
    return Timer.getFPGATimestamp() >= startTime + motor1Duration && Timer.getFPGATimestamp() >= startTime + motor2Duration;
   }

   @Override
   public void end(boolean interrupted){
    motorSubsystem.motor1Stop();
    motorSubsystem.motor2Stop();
    System.out.println("Ended at " + Timer.getFPGATimestamp() + "seconds.");

   }
}
