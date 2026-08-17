package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimplePID;

public class PIDSetVelocityCommand extends Command {
    private final SimplePID m_simplePid;

    public PIDSetVelocityCommand(SimplePID pid) {
        m_simplePid = pid;
        addRequirements(m_simplePid);
    }

    @Override
    public void execute() {
        double targetVelocity = SmartDashboard.getNumber("Target velocity", 0.0);
        m_simplePid.setVelocity(targetVelocity);
    }
}
