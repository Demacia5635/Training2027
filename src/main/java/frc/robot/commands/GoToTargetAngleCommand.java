package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class GoToTargetAngleCommand extends Command {
    private final SimpleMotorSubsystem subsystem;

    public GoToTargetAngleCommand(SimpleMotorSubsystem subsystem) {
        this.subsystem = subsystem;
        addRequirements(subsystem);
    }

    @Override
    public void execute() {
        double target = SmartDashboard.getNumber("3rd cmd Target", 0.0);
        
        subsystem.setPosition(target);
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.setSteerPower(0);
    }
}