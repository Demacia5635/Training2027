package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.HomeworkMotorSubsystemConstants;
import frc.robot.subsystems.HomeworkMotorSubsystem;

public class HomeworkMotorCommand extends Command {
    private final HomeworkMotorSubsystem MOTOR_SUBSYSTEM;
    private double wantedSteerPosition;
    private double wantedDrivePosition;
    private boolean changeDrive;
    private boolean changeSteer;
   public HomeworkMotorCommand(HomeworkMotorSubsystem motorSubsystem, double wantedSteerPosition, double wantedDrivePosition, boolean changeSteer, boolean changeDrive) {
    this.MOTOR_SUBSYSTEM = motorSubsystem;
    this.wantedSteerPosition = wantedSteerPosition;
    this.wantedDrivePosition = wantedDrivePosition;
    this.changeDrive = changeDrive;
    this.changeSteer = changeSteer;
    addRequirements(motorSubsystem);
   }


   @Override
   public void initialize(){
   }

   @Override
   public void execute(){
   if (changeDrive) {
        double currentDrive = MOTOR_SUBSYSTEM.getDrivePos();
        double targetDrive = wantedDrivePosition;
        double driveError = targetDrive - currentDrive;

        if (Math.abs(driveError) <= 0.05) {
            MOTOR_SUBSYSTEM.driveStop();
        } else {
            MOTOR_SUBSYSTEM.driveSetPower(driveError > 0 ? HomeworkMotorSubsystemConstants.motorPower : -HomeworkMotorSubsystemConstants.motorPower);
        }
    }

    if (changeSteer) {
        double currentSteer = MOTOR_SUBSYSTEM.getSteerPosAsDegrees();
        double steerError = wantedSteerPosition - ((180 / Math.PI) * currentSteer);

        if (Math.abs(steerError) <= 2.0) {
            MOTOR_SUBSYSTEM.steerStop();
        } else {
            MOTOR_SUBSYSTEM.steerSetPower(steerError > 0 ? HomeworkMotorSubsystemConstants.motorPower : -HomeworkMotorSubsystemConstants.motorPower);
        }
    }
}
   @Override
   public boolean isFinished(){
      boolean driveFinished = !changeDrive;
      boolean steerFinished = !changeSteer;

            if(changeDrive){
               double currentDrive = MOTOR_SUBSYSTEM.getDrivePos();
               double targetDrive = (2 * Math.PI * wantedDrivePosition) / HomeworkMotorSubsystemConstants.DRIVE_CIRCUMFERENCE;
               driveFinished = Math.abs(targetDrive - currentDrive) <= 0.05;
            }

            if (changeSteer) {
                double currentSteer = MOTOR_SUBSYSTEM.getSteerPosAsDegrees();
                steerFinished = Math.abs(wantedSteerPosition - currentSteer) <= 2.0;
    }

         return steerFinished && driveFinished;

   }

   @Override
   public void end(boolean interrupted){
    MOTOR_SUBSYSTEM.driveStop();
    MOTOR_SUBSYSTEM.steerStop();
   }
}

