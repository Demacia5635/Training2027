package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.commands.DriveVelocityPIDCommand_V1_FINAL;
import frc.robot.commands.name_V1_FINAL;
import frc.robot.commands.SteerPIDCommand_V1_FINAL;
import frc.robot.subsystems.SUBSYS_name_V1_FINAL;

public class RobotContainer {

  private final SUBSYS_name_V1_FINAL subsystem = new SUBSYS_name_V1_FINAL();

  public RobotContainer() {

    configureDashboard();
  }

  private void configureDashboard() {

    SmartDashboard.putNumber(
        "Manual/Drive Power",
        0.0);

    SmartDashboard.putNumber(
        "Manual/Steer Power",
        0.0);

    SmartDashboard.putNumber(
        "Steer PID/Target Angle (deg)",
        0.0);

    SmartDashboard.putNumber(
        "Drive PID/Target Velocity (mps)",
        0.0);

    SmartDashboard.putData(
        "Commands/Run Manual Motor Test",
        new name_V1_FINAL(
            subsystem,
            30.0));

    SmartDashboard.putData(
        "Commands/Run Steer PID",
        new SteerPIDCommand_V1_FINAL(
            subsystem));

    SmartDashboard.putData(
        "Commands/Run Drive Velocity PID",
        new DriveVelocityPIDCommand_V1_FINAL(
            subsystem));
  }

  public Command getAutonomousCommand() {

    return null;
  }
}