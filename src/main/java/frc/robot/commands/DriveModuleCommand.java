package frc.robot.commands;

import java.util.function.DoubleSupplier;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ModuleSubsystem;

public class DriveModuleCommand extends Command {
    private final ModuleSubsystem module;
    private final DoubleSupplier xboxLeftY;
    private final DoubleSupplier xboxRightX;
    private final DoubleSupplier xboxRightY;

    public DriveModuleCommand(ModuleSubsystem module, DoubleSupplier xboxLeftY, DoubleSupplier xboxRightX, DoubleSupplier xboxRightY) {
        this.module = module;
        this.xboxLeftY = xboxLeftY;
        this.xboxRightX = xboxRightX;
        this.xboxRightY = xboxRightY;
        
        addRequirements(module);
    }

    @Override
    public void execute() {
        double speedInput = -MathUtil.applyDeadband(xboxLeftY.getAsDouble(), 0.1);
        double targetVelocity = speedInput * 3.0; 

        double rx = MathUtil.applyDeadband(xboxRightX.getAsDouble(), 0.1);
        double ry = -MathUtil.applyDeadband(xboxRightY.getAsDouble(), 0.1); // היפוך Y ב-Xbox

        double targetAngle = module.getSteerAngleDegrees(); 
        if (Math.hypot(rx, ry) > 0.5) {
            targetAngle = Math.toDegrees(Math.atan2(rx, ry));
        }

        module.setDriveVelocity(targetVelocity);
        module.setSteerAngle(targetAngle);

        SmartDashboard.putNumber("Drive/Target Velocity", targetVelocity);
        SmartDashboard.putNumber("Drive/Actual Velocity", module.getDriveVelocityMetersPerSec());
        SmartDashboard.putNumber("Steer/Target Angle", targetAngle);
        SmartDashboard.putNumber("Steer/Actual Angle", module.getSteerAngleDegrees());
    }

    @Override
    public void end(boolean interrupted) {
        module.stopModule();
    }
}
