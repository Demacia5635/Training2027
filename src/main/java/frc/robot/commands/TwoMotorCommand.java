package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;


public class TwoMotorCommand extends Command {
    private final SimpleMotorSubsystem subsystem;
    private final double powerDrive;
    private final double powerSteering;

    public TwoMotorCommand(
        SimpleMotorSubsystem subsystem,
        double powerDrive,
        double powerSteering
    ) {
        this.subsystem = subsystem;
        this.powerDrive = powerDrive;
        this.powerSteering = powerSteering;

        addRequirements(subsystem);
    }

    @Override
    public void execute() {
        subsystem.setBothMotorsPower(powerDrive, powerSteering);
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.stop();
    }

}
