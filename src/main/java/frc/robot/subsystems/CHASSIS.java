

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.Pigeon2;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.chassis.SwerveModuleConfig;

public class CHASSIS extends SubsystemBase {
  private final SwerveModuleSUB frontLeft;
  private final SwerveModuleSUB frontRight;
  private final SwerveModuleSUB backLeft;
  private final SwerveModuleSUB backRight;
  private final SwerveModuleSUB[] modules;

  private final Pigeon2 gyro;

  private final SwerveDriveKinematics kinematics;
  private final SwerveDriveOdometry odometry;

  public CHASSIS() {
    frontLeft = new SwerveModuleSUB(SwerveModuleConfig.FRONT_LEFT);
    frontRight = new SwerveModuleSUB(SwerveModuleConfig.FRONT_RIGHT);
    backLeft = new SwerveModuleSUB(SwerveModuleConfig.BACK_LEFT);
    backRight = new SwerveModuleSUB(SwerveModuleConfig.BACK_RIGHT);

    modules = new SwerveModuleSUB[] { frontLeft, frontRight, backLeft, backRight };

    gyro = new Pigeon2(0);
    resetGyro();

    double trackWidth = 0.6;
    double wheelBase = 0.6;

    // FIXED: Removed the quote marks around "-trackWidth"
    kinematics = new SwerveDriveKinematics(
        new Translation2d(wheelBase / 2.0, trackWidth / 2.0),
        new Translation2d(wheelBase / 2.0, -trackWidth / 2.0),
        new Translation2d(-wheelBase / 2.0, trackWidth / 2.0),
        new Translation2d(-wheelBase / 2.0, -trackWidth / 2.0)
    );

    odometry = new SwerveDriveOdometry(
        kinematics,
        getRotation2d(),
        getModulePositions()
    );
  }

  @Override
  public void periodic() {
    odometry.update(getRotation2d(), getModulePositions());
  }

  public void drive(ChassisSpeeds fieldRelativeSpeeds) {
    ChassisSpeeds robotRelativeSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
        fieldRelativeSpeeds,
        getRotation2d()
    );

    SwerveModuleState[] states = kinematics.toSwerveModuleStates(robotRelativeSpeeds);
    SwerveDriveKinematics.desaturateWheelSpeeds(states, 4.5);

    for (int i = 0; i < modules.length; i++) {
      modules[i].setState(states[i]);
    }
  }

  public Rotation2d getRotation2d() {
    return gyro.getRotation2d();
  }
  

  public Pose2d getPose() {
    return odometry.getPoseMeters();
  }

  public void resetGyro() {
    gyro.setYaw(0);
  }

  public void resetOdometry(Pose2d pose) {
    odometry.resetPosition(getRotation2d(), getModulePositions(), pose);
  }

  private SwerveModulePosition[] getModulePositions() {
    return new SwerveModulePosition[] {
        frontLeft.getPosition(),
        frontRight.getPosition(),
        backLeft.getPosition(),
        backRight.getPosition()
    };
  }}




