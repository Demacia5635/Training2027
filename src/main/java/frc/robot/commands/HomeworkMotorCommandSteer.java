package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.HomeworkMotorSubsystemConstants;
import frc.robot.subsystems.HomeworkMotorSubsystem;

public class HomeworkMotorCommandSteer extends Command {
    private final HomeworkMotorSubsystem MOTOR_SUBSYSTEM;
    private double wantedPosition;
   public HomeworkMotorCommandSteer(HomeworkMotorSubsystem motorSubsystem, double wantedPosition) {
    this.MOTOR_SUBSYSTEM = motorSubsystem;
    this.wantedPosition = wantedPosition;
    addRequirements(motorSubsystem);
   }


   @Override
   public void initialize(){
   }

   @Override
   public void execute(){
      MOTOR_SUBSYSTEM.steerSetPower(MOTOR_SUBSYSTEM.getSteerPos() > wantedPosition ? HomeworkMotorSubsystemConstants.motorPower : -(HomeworkMotorSubsystemConstants.motorPower));
      }
   

   @Override
   public boolean isFinished(){
     return Math.abs(MOTOR_SUBSYSTEM.getSteerPos() - wantedPosition) <= 3;
   }

   @Override
   public void end(boolean interrupted){
    MOTOR_SUBSYSTEM.steerStop();
   }
}
