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

    public HomeworkMotorCommand(HomeworkMotorSubsystem motorSubsystem, double wantedSteerPosition,
            double wantedDrivePosition, boolean changeSteer, boolean changeDrive) {
        this.MOTOR_SUBSYSTEM = motorSubsystem;
        this.wantedSteerPosition = wantedSteerPosition;
        this.wantedDrivePosition = wantedDrivePosition;
        this.changeDrive = changeDrive;
        this.changeSteer = changeSteer;
        addRequirements(motorSubsystem);
    }

    @Override
    public void initialize() {
        System.out.println("initialized");
    }

    @Override
    public void execute() {
        if (changeDrive) {
            double currentDrive = MOTOR_SUBSYSTEM.getDrivePos();
            double targetDrive = wantedDrivePosition;
            double driveError = targetDrive - currentDrive;

            if (Math.abs(driveError) <= HomeworkMotorSubsystemConstants.driveTolerance) {
                MOTOR_SUBSYSTEM.driveStop();
                System.out.println("Drive finished");
            } else {
                MOTOR_SUBSYSTEM.driveSetPower(
                        (driveError > 0 ? Math.min(0.1 * driveError + 0.014763611212129784, HomeworkMotorSubsystemConstants.motorPower)
                                : Math.max(0.1 * driveError - 0.014763611212129784, -HomeworkMotorSubsystemConstants.motorPower)));
                System.out.println("Drive error: " + driveError + ", Drive power: "
                        + (driveError > 0 ? Math.min(0.1 * driveError + 0.014763611212129784, HomeworkMotorSubsystemConstants.motorPower)
                                : Math.max(0.1 * driveError - 0.014763611212129784, -HomeworkMotorSubsystemConstants.motorPower)));
            }
        }

        if (changeSteer) {
            double currentSteer = MOTOR_SUBSYSTEM.getSteerPos();
            double steerError = wantedSteerPosition - (currentSteer);

            if (Math.abs(steerError) <= HomeworkMotorSubsystemConstants.steerTolerance) {
                MOTOR_SUBSYSTEM.steerStop();
                System.out.println("Steer finished");
            } else {
                MOTOR_SUBSYSTEM.steerSetPower((steerError > 0
                        ? Math.min(0.01 * steerError + 0.010959841558622941,
                                HomeworkMotorSubsystemConstants.motorPower)
                        : Math.max(0.01 * steerError - 0.010959841558622941,
                                -HomeworkMotorSubsystemConstants.motorPower)));
                System.out.println("Steer error: " + steerError + ", Steer power: "
                        + (steerError > 0
                                ? Math.min(0.01 * steerError + 0.010959841558622941,
                                        HomeworkMotorSubsystemConstants.motorPower)
                                : Math.max(0.01 * steerError - 0.010959841558622941,
                                        -HomeworkMotorSubsystemConstants.motorPower)));

            }
        }

    }

    @Override
    public boolean isFinished() {
        boolean driveFinished = true;
        boolean steerFinished = true;

        if (changeDrive) {
            double currentDrive = MOTOR_SUBSYSTEM.getDrivePos();
            double targetDrive = wantedDrivePosition;
            driveFinished = Math.abs(targetDrive - currentDrive) <= HomeworkMotorSubsystemConstants.driveTolerance;
            System.out.println("Drive " + driveFinished);
        }

        if (changeSteer) {
            double currentSteer = MOTOR_SUBSYSTEM.getSteerPos();
            steerFinished = Math.abs(wantedSteerPosition - currentSteer) <= HomeworkMotorSubsystemConstants.steerTolerance;
            System.out.println("Steer " + steerFinished);
        }

        return steerFinished && driveFinished;

    }

    @Override
    public void end(boolean interrupted) {
        MOTOR_SUBSYSTEM.driveStop();
        MOTOR_SUBSYSTEM.steerStop();
        System.out.println("ended");
    }
}
