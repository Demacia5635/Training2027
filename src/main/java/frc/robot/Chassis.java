package frc.robot;

import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Chassis extends SubsystemBase {
    private SwerveModule[] modules;
    private Pigeon2 gyro;
    private SwerveDrivePoseEstimator poseEstimator;
    private Field2d field;
    private SwerveDriveKinematics kinematicsFix;

    public Chassis() {
        modules = new SwerveModule[] {
                new SwerveModule(Constants.CONFIG_STEER_FRONT_LEFT, Constants.CONFIG_DRIVE_FRONT_LEFT,
                        Constants.CONFIG_CANCODER_FRONT_LEFT),
                new SwerveModule(Constants.CONFIG_STEER_FRONT_RIGHT, Constants.CONFIG_DRIVE_FRONT_RIGHT,
                        Constants.CONFIG_CANCODER_FRONT_RIGHT),
                new SwerveModule(Constants.CONFIG_STEER_BACK_LEFT, Constants.CONFIG_DRIVE_BACK_LEFT,
                        Constants.CONFIG_CANCODER_BACK_LEFT),
                new SwerveModule(Constants.CONFIG_STEER_BACK_RIGHT, Constants.CONFIG_DRIVE_BACK_RIGHT,
                        Constants.CONFIG_CANCODER_BACK_RIGHT)
        };
        gyro = new Pigeon2(Constants.GYRO_ID);
    }

    public void setVelocities(ChassisSpeeds speeds) {
        speeds = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, getGyroAngle());
        SwerveModuleState[] states = kinematicsFix.toSwerveModuleStates(speeds);
        setModuleStates(states);
    }

    @Override
    public void periodic() {
        poseEstimator.update(getGyroAngle(), getModulePositions());
        field.setRobotPose(poseEstimator.getEstimatedPosition());
    }

    public Rotation2d getGyroAngle() {
        return Rotation2d.fromDegrees(gyro.getYaw().getValueAsDouble());
    }

    public SwerveModulePosition[] getModulePositions() {
        return new SwerveModulePosition[] {
            modules[0].getPosition(),
            modules[1].getPosition(),
            modules[2].getPosition(),
            modules[3].getPosition()
        };
    }

    public void setModuleStates(SwerveModuleState[] states) {
    for (int i = 0; i < modules.length; i++) {
        modules[i].setVelocityDrive(states[i].speedMetersPerSecond);
        modules[i].setSteerPosition(states[i].angle.getRadians());
    }
}
}
