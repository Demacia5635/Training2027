package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class moduleCommand extends SequentialCommandGroup {
    public moduleCommand(SimpleMotorSubsystem subsystem) {
        addCommands(
                new InstantCommand(() -> subsystem.setPosition(0.25)),
                Commands.waitSeconds(1.5),

                new ParallelCommandGroup(
                        new InstantCommand(() -> subsystem.setDrivePosition(1.0)),
                        new InstantCommand(() -> subsystem.setPosition(0.375))),
                Commands.waitSeconds(2.0),

                new ParallelCommandGroup(
                        new InstantCommand(() -> subsystem.setPosition(0.0)),
                        new InstantCommand(() -> subsystem.setDrivePosition(-1.0))));
    }
}