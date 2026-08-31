import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.demacia.utils.sensors.Cancoder;

public class SwerveModule {
    private TalonMotor steerMotor;
    private TalonMotor driveMotor;
    private Cancoder cancoder;
    public String name;

    public SwerveModule(SwerveModuleConfig config) {
        steerMotor = new TalonMotor(config.STEER_CONFIG);
        driveMotor = new TalonMotor(config.DRIVE_CONFIG);
        cancoder = new Cancoder(config.CANCODER_CONFIG);
        this.name = config.name;
        steerMotor.setposition(getabsoluteAngle() - config.STEER_OFFSET);
    }

    public void setNaturalMode(boolean isbrake) {
        driveMotor.setNaturalMode(isbrake);
        steerMotor.setNaturalMode(isbrake);
    }

    public void setSteerPower(double power) {
        steerMotor.set(power);
    }

    public double getabsoluteAngle() {
        return cancoder.getCurrentAbsPosition();
    }

    public void setDrivePower(double power) {
        driveMotor.set(power);
    }

    public void setSteerVelocity(double VelocityRadsPerSecound) {
        steerMotor.setVelocity(VelocityRadsPerSecound);
    }

    public void setDriveVelocity(double VelocityMetersPerSecond) {
        driveMotor.setVelocity(VelocityMetersPerSecond);
    }

    public void setSteerPosition(double positionRadians) {
        steerMotor.setPositionVoltage(positionRadians);
    }

    public double getSteerAngle() {
        return steerMotor.getPosition().getValueAsDouble();
    }

    public Rotation2d getSteerRotation() {
        return new Rotation2d(getSteerAngle());
    }

    public double getSteerVel() {
        return steerMotor.getcurrentvelocity();
    }

    public double getDriveVel() {
        return driveMotor.getcurrentvelocity();
    }

    public void setstate(SwerveModuleState state) {
        double wantedAngle = state.angle.getRadians();
        double diff = wantedAngle - steerMotor.getCurrentPosition();
        double Vel = state.speedMetersPerSecond;
        diff = MathUtil.angleModulus(diff);
        if (diff > 0.5 * Math.PI) {
            Vel = -Vel;
            diff = diff - Math.PI;
        } else if (diff < -0.5 * Math.PI) {
            Vel = -Vel;
            diff = diff + Math.PI;
        }
        setSteerPosition(steerMotor.getCurrentPosition() + diff);
        setDriveVelocity(Vel);
    }

    public SwerveModulePosition getModulePosition() {
        return new SwerveModulePosition(driveMotor.getCurrentPosition(),
                Rotation2d.fromRadians(steerMotor.getCurrentPosition()));
    }
    public SwerveModuleState getstate(){
        return new SwerveModuleState(getDriveVel(),getSteerRotation());
    }
    public void stop(){
        steerMotor.stopMotor();
        driveMotor.stopMotor();
    }
}
