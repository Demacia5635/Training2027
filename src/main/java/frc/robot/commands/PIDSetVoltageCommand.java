package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimplePID;

public class PIDSetVoltageCommand extends Command {
    private final SimplePID m_simplePid;

    public PIDSetVoltageCommand(SimplePID pid) {
        m_simplePid = pid;
        addRequirements(m_simplePid);
    }

    @Override
    public void execute() {
        double targetVoltage = SmartDashboard.getNumber("Target voltage", 0.0);
        m_simplePid.setVoltage(targetVoltage);
    }
}
