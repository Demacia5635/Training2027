package frc.robot.commands;

import frc.robot.subsystems.ModuleSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class JoystickUpdateCommand extends Command {
    private CommandXboxController m_xboxController;
    private ModuleSubsystem m_subsystem;

    public JoystickUpdateCommand(CommandXboxController xboxController, ModuleSubsystem subsystem) {
        m_xboxController = xboxController;
        m_subsystem = subsystem;
        addRequirements(subsystem);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {}

    @Override
    public void end(boolean interrupted) {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
