package frc.robot.commands;



import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.HomeworkMotorSubsystem;

public class HomeworkMotorCommand extends Command {
    private final HomeworkMotorSubsystem MOTOR_SUBSYSTEM;
    private final double DRIVE_POWER;
    private final double STEER_POWER;
    private final double DRIVE_DURATION;
    private final double STEER_DURATION;
    private double startTime = 0;
    private Timer timer = new Timer();

   public HomeworkMotorCommand(HomeworkMotorSubsystem motorSubsystem, double drivePower, double steerPower, double driveDuration, double steerDuration) {
    this.MOTOR_SUBSYSTEM = motorSubsystem;
    this.DRIVE_POWER = drivePower;
    this.STEER_POWER = steerPower;
    this.DRIVE_DURATION = driveDuration;
    this.STEER_DURATION = steerDuration;
    addRequirements(motorSubsystem);
   }


   @Override
   public void initialize(){
        startTime = Timer.getFPGATimestamp();
        timer.restart();
        System.out.println("Motor 1 started at + " + startTime + "seconds, for " + DRIVE_DURATION + " seconds, with " + DRIVE_POWER);
        System.out.println("Motor 2 started at + " + startTime + "seconds, for " + STEER_DURATION + " seconds, with " + STEER_POWER);
   }

   @Override
   public void execute(){
    MOTOR_SUBSYSTEM.driveSetPower(DRIVE_POWER);
    MOTOR_SUBSYSTEM.steerSetPower(STEER_POWER);
   }

   @Override
   public boolean isFinished(){
    return Timer.getFPGATimestamp() >= startTime + (DRIVE_DURATION > STEER_DURATION ? DRIVE_DURATION : STEER_DURATION);
   }

   @Override
   public void end(boolean interrupted){
    MOTOR_SUBSYSTEM.driveStop();
    MOTOR_SUBSYSTEM.steerStop();
    System.out.println("Ended at " + Timer.getFPGATimestamp() + "seconds.");

   }
}
