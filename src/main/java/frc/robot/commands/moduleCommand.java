package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class moduleCommand extends SequentialCommandGroup {
    public moduleCommand(SimpleMotorSubsystem subsystem) {
        addCommands(
                new YuvalSteer( 0, 0, subsystem),
                new YuvalSteer( 90, 0, subsystem),
                new YuvalSteer( 135, 100, subsystem),
                new YuvalSteer( 0, -1, subsystem)
                );
    }
}