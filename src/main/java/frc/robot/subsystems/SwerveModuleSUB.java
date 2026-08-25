package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.sendable.SendableBuilder;
import frc.demacia.utils.chassis.SwerveModuleConfig;
import frc.demacia.utils.motors.TalonFXMotor; // או התאמה ל-SparkMax לפי הצורך
import frc.demacia.utils.sensors.Cancoder;

public class SwerveModuleSUB {
    // משתנים רגילים (לא static!) כדי לזהות כל מודול בנפרד
    public final TalonFXMotor steerMotor;
    public final TalonFXMotor driveMotor;
    public final Cancoder cancoder;
    public final String name;
    private final double steerOffset;

    public SwerveModuleSUB(frc.robot.subsystems.SwerveModuleConfig backLeft) {
        // שימוש בהגדרות ספציפיות מתוך ה-config של המודול הנוכחי
        steerMotor = new TalonFXMotor(SwerveModuleConfig.STEER_CONFIG); 
        driveMotor = new TalonFXMotor(SwerveModuleConfig.DRIVE_CONFIG);
        cancoder = new Cancoder(SwerveModuleConfig.CANCODER_CONFIG);
        
        name = backLeft.name;
        steerOffset = SwerveModuleConfig.STEER_OFFSET;
        
        steerMotor.setEncoderPosition(getAbsoluteAngle() - steerOffset);
    }

    public void setNeutralMode(boolean isBrake) {
        driveMotor.setNeutralMode(isBrake);
        steerMotor.setNeutralMode(isBrake);
    }

    // --- שליטה ידנית (Power / Velocity / Position) ---
    public void setSteerPower(double power) {
        steerMotor.set(power);
    }

    public void setDrivePower(double power) {
        driveMotor.set(power);
    }

    public void setSteerVelocity(double velocityRadsPerSecond) {
        steerMotor.setVelocity(velocityRadsPerSecond);
    }

    public void setDriveVelocity(double velocityMetersPerSecond) {
        driveMotor.setVelocity(velocityMetersPerSecond);
    }

    public void setSteerPosition(double positionRadians) {
        steerMotor.setPositionVoltage(positionRadians);
    }

    //  הבאת כל הגלגלים לאותו הכיוון / כיול כיוון 
    public void setSteerAngle(Rotation2d angle) {
        double currentPos = steerMotor.getCurrentPosition();
        double targetAngle = angle.getRadians();
        double diff = MathUtil.angleModulus(targetAngle - currentPos);
        setSteerPosition(currentPos + diff);
    }

    // קבלת נתונים (Getters) 
    public double getSteerAngle() {
        return steerMotor.getCurrentPosition();
    }

    public Rotation2d getSteerRotation() {
        return new Rotation2d(getSteerAngle());
    }

    public double getSteerVel() {
        return steerMotor.getCurrentVelocity();
    }

    public double getDriveVel() {
        return driveMotor.getCurrentVelocity();
    }

    public double getAbsoluteAngle() {
        return cancoder.getCurrentAbsPosition();
    }

    public SwerveModuleState getState() {
        return new SwerveModuleState(getDriveVel(), getSteerRotation());
    }

    public void setState(SwerveModuleState state) {
        @SuppressWarnings("deprecation")
        SwerveModuleState optimizedState = SwerveModuleState.optimize(state, getSteerRotation());
        
        double wantedAngle = optimizedState.angle.getRadians();
        double currentPos = steerMotor.getCurrentPosition();
        double diff = MathUtil.angleModulus(wantedAngle - currentPos);
        
        setSteerPosition(currentPos + diff);
        setDriveVelocity(optimizedState.speedMetersPerSecond);
    }

    public void InitSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("SwerveModule");
        builder.addDoubleProperty("Abs Encoder", this::getAbsoluteAngle, null);
        builder.addDoubleProperty("Steer Vel", this::getSteerVel, null);
        builder.addDoubleProperty("Drive Vel", this::getDriveVel, null);
        builder.addDoubleProperty("Steer Angle", this::getSteerAngle, null);
    }

    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(getDriveVel(), getSteerRotation());
    }
}
