package frc.robot;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.SimpleMotorCommand;
import frc.robot.subsystems.SimpleMotorSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class RobotContainer {
    private final SimpleMotorSubsystem subsystem = new SimpleMotorSubsystem();
    private final CommandXboxController controller = new CommandXboxController(0);

    public RobotContainer() {
        configureBindings();
        configureDefaultCommands();
    }

    private void configureBindings() {
	  controller.a().onTrue(new SimpleMotorCommand (subsystem,1 , 0,10));;    }

    private void configureDefaultCommands() {
        subsystem.setDefaultCommand(new SimpleMotorCommand(subsystem, 0, 0, 0));
    }

    public Command getAutonomousCommand() {
          	return new SimpleMotorCommand (subsystem,1 , 0,10);
                            

    }
}
