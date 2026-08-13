package frc.robot.subsystems;


import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants.PIDMotorSubsystemConstants;

public class PIDMotorSubsystem extends SubsystemBase {
    private final TalonFXMotor driveMotor; 
    private final TalonFXMotor steerMotor;

    public PIDMotorSubsystem(){

        super();
        driveMotor = new TalonFXMotor(PIDMotorSubsystemConstants.CONFIG);
        steerMotor = new TalonFXMotor(PIDMotorSubsystemConstants.STEER_CONFIG);
    
        SmartDashboard.putData("Subsystem", this);
       
    }
   
   
    public void setDrivePosition(double pos){
        driveMotor.setPositionVoltage(pos);
    }
    
    public void setDriveVelocity(double velocity){
        driveMotor.setVelocity(velocity);
    }

 public void setSteerPosition(double pos){
        steerMotor.setPositionVoltage(pos);
    }
    
    public void setSteerVelocity(double velocity){
        steerMotor.setVelocity(velocity);
    }
@Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("Drive velocity", this::getDriveVelocity, null);
        builder.addDoubleProperty("Steer velocity", this::getSteerVelocity, null);
        builder.addDoubleProperty("pos steer", () -> getSteerPosition(), null);
        builder.addDoubleProperty("pos drive", ()-> getDrivePosition(), null);
    }
    public double getDriveVelocity(){
        return driveMotor.getCurrentVelocity();
    }

    public double getDrivePosition(){
        return Math.toDegrees(driveMotor.getPosition().getValueAsDouble());
    }

     public double getSteerVelocity(){
        return steerMotor.getCurrentVelocity();
    }

    public double getSteerPosition(){
        return Math.toDegrees(steerMotor.getPosition().getValueAsDouble());
    

    }
}


