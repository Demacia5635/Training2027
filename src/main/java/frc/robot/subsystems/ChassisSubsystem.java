// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
public class ChassisSubsystem extends SubsystemBase {

  SwerveModuleSubsystem FRswerve;
  SwerveModuleSubsystem FLswerve;
  SwerveModuleSubsystem BRswerve;
  SwerveModuleSubsystem BLswerve;
  public ChassisSubsystem() {
    this.FRswerve = new SwerveModuleSubsystem(Constants.FRSTEER_CONFIG, Constants.FRDRIVE_CONFIG, Constants.FRCANCODER_CONFIG);
    this.FLswerve = new SwerveModuleSubsystem(Constants.FLSTEER_CONFIG, Constants.FLDRIVE_CONFIG, Constants.FLCANCODER_CONFIG);
    this.BRswerve = new SwerveModuleSubsystem(Constants.BRSTEER_CONFIG, Constants.BRDRIVE_CONFIG, Constants.BRCANCODER_CONFIG);
    this.BLswerve = new SwerveModuleSubsystem(Constants.BLSTEER_CONFIG, Constants.BLDRIVE_CONFIG, Constants.BLCANCODER_CONFIG);
  }
  public void resetAllAngles(){
    FRswerve.resetAngle();
    FLswerve.resetAngle();
    BRswerve.resetAngle();
    BLswerve.resetAngle();
  }

}
