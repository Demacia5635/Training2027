package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.Constants;
import frc.robot.subsystems.SUBSYS_name_V1_FINAL;

public class name_V1_FINAL extends Command {

  private final SUBSYS_name_V1_FINAL subsystem;

  private final double duration;

  private final Timer timer = new Timer();

  public name_V1_FINAL(SUBSYS_name_V1_FINAL subsystem,double duration) {

    this.subsystem = subsystem;
    this.duration = duration;

    addRequirements(subsystem);
  }

  @Override
  public void initialize() {

    timer.restart();

    System.out.println(
        "Simple Motor Command started");
  }

  @Override
  public void execute() {

    double drivePower = SmartDashboard.getNumber(
        "Manual/Drive Power",
        0.0);

    double steerPower = SmartDashboard.getNumber(
        "Manual/Steer Power",
        0.0);

    drivePower = MathUtil.clamp(
        drivePower,
        -Constants.SimpleMotorConstants.POWER_LIMIT,
        Constants.SimpleMotorConstants.POWER_LIMIT);

    steerPower = MathUtil.clamp(
        steerPower,
        -Constants.SimpleMotorConstants.POWER_LIMIT,
        Constants.SimpleMotorConstants.POWER_LIMIT);

    subsystem.setDrivePower(
        drivePower);

    subsystem.setSteerPower(
        steerPower);
  }

  @Override
  public boolean isFinished() {

    return timer.hasElapsed(
        duration);
  }

  @Override
  public void end(
      boolean interrupted) {

    subsystem.stop();

    System.out.println(
        "Simple Motor Command ended");
  }
}