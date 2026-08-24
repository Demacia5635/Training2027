package frc.robot.commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.demacia.utils.controller.CommandController;
import frc.robot.Constants;
import frc.robot.subsystems.SimpleMotorSubsystem;

/**
 * Default teleoperated control for the two-motor training mechanism.
 *
 * <p>Left-stick Y controls drive power and right-stick X controls steer power.
 * Hold the left bumper for precision mode. The {@link SimpleMotorSubsystem}
 * reads motor telemetry through Demacia's cached {@code Data} objects.
 */
public class XboxDriveSteerCommand extends Command {
    private final SimpleMotorSubsystem subsystem;
    private final CommandController controller;
    private final BooleanSupplier precisionMode;

    public XboxDriveSteerCommand(
            SimpleMotorSubsystem subsystem,
            CommandController controller,
            BooleanSupplier precisionMode) {
        this.subsystem = subsystem;
        this.controller = controller;
        this.precisionMode = precisionMode;

        addRequirements(subsystem);
    }

    @Override
    public void execute() {
        double powerLimit = precisionMode.getAsBoolean()
            ? Constants.SimpleMotorConstants.PRECISION_POWER_LIMIT
            : Constants.SimpleMotorConstants.POWER_LIMIT;

        // Xbox Y axes are negative when the stick is pushed forward.
        double driveInput = -controller.getLeftY();
        double steerInput = controller.getRightX();

        // Squaring preserves direction while making low-speed control smoother.
        driveInput = Math.copySign(driveInput * driveInput, driveInput);
        steerInput = Math.copySign(steerInput * steerInput, steerInput);

        double drivePower = MathUtil.clamp(
            driveInput * powerLimit,
            -powerLimit,
            powerLimit
        );

        double steerPower = MathUtil.clamp(
            steerInput * powerLimit,
            -powerLimit,
            powerLimit
        );

        subsystem.setDrivePower(drivePower);
        subsystem.setSteerPower(steerPower);

        SmartDashboard.putNumber("Xbox Drive Power Command", drivePower);
        SmartDashboard.putNumber("Xbox Steer Power Command", steerPower);
        SmartDashboard.putBoolean("Xbox Precision Mode", precisionMode.getAsBoolean());
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
