package frc.robot.commands;

import frc.robot.Chassis;
import frc.robot.Constants;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class DriveCommand extends Command {
  private final Chassis chassisDriveAndSteer;
  private final CommandXboxController xboxController;

  // הבנאי מקבל את השאסי ואת השלט
  public DriveCommand(Chassis chassisDriveAndSteer, CommandXboxController xboxController) {
    this.chassisDriveAndSteer = chassisDriveAndSteer;
    this.xboxController = xboxController;

    // דרישה למערכת השאסי (Chassis Subsystem)
    addRequirements(chassisDriveAndSteer);
  }

  @Override
  public void execute() {
    double forwardSpeedMetersPerSecond = -xboxController.getLeftY() * Constants.MAX_DRIVE_SPEED_METERS_PER_SECOND;
    double lateralSpeedMetersPerSecond = -xboxController.getLeftX() * Constants.MAX_DRIVE_SPEED_METERS_PER_SECOND;
    double rotationalSpeedRadiansPerSecond = -xboxController.getRightX()
        * Constants.MAX_ANGULAR_SPEED_RADIANS_PER_SECOND;
    ChassisSpeeds desiredChassisSpeeds = new ChassisSpeeds(
        forwardSpeedMetersPerSecond,
        lateralSpeedMetersPerSecond,
        rotationalSpeedRadiansPerSecond);

    chassisDriveAndSteer.setVelocities(desiredChassisSpeeds);
  }

  @Override
  public void end(boolean interrupted) {
    chassisDriveAndSteer.setVelocities(new ChassisSpeeds(0, 0, 0));
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}