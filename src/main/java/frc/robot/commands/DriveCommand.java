package frc.robot.commands;

import frc.demacia.utils.controller.CommandController;
import frc.demacia.utils.controller.CommandController;
import frc.robot.Chassis;
import frc.robot.Constants;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class DriveCommand extends Command {
  private final Chassis chassisDriveAndSteer;
  private final CommandController Controller;

  public DriveCommand(Chassis chassis, CommandController Controller) {
    this.chassisDriveAndSteer = chassis;
    this.Controller = Controller;
    addRequirements(chassis);

  }

  @Override
  public void execute() {
    double vx = -Controller.getLeftY() * Constants.MAX_DRIVE_SPEED_METERS_PER_SECOND;
    double vy = -Controller.getLeftX() * Constants.MAX_DRIVE_SPEED_METERS_PER_SECOND;
    double omga = -Controller.getRightX() * Constants.MAX_ANGULAR_SPEED_RADIANS_PER_SECOND;

    ChassisSpeeds Speeds = new ChassisSpeeds(
        vx, // vx
        vy, // vy
        omga// omga
    );

    chassisDriveAndSteer.setVelocities(Speeds);
    // System.out.println("vx" + vx);
    // System.out.println("vy" + vy);
    // System.out.println("omega" + omga);
    // System.out.println("y" + Controller.getLeftY());
    // System.out.println("x" + Controller.getLeftX());
    // System.out.println("righx" + Controller.getRightX());
   // System.out.println("chassis speed" + Speeds);
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