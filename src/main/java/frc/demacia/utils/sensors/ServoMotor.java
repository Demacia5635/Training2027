package frc.demacia.utils.sensors;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Servo;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.log.Log.LogLevel;

public class ServoMotor extends Servo {

    ServoMotorConfig config;
    String name;
    public ServoMotor (ServoMotorConfig config) {
        super(config.id);
        this.config = config;
        name=config.name;
        addLog();
        Log.log(name + " ServoMotor initialized");
    }

    @SuppressWarnings("unchecked")
    private void addLog() {
        Log.putData(name + ": Position, Angle", 
            new Supplier[]{
                this::getPosition,
                this::getAngle
            }
        , LogLevel.LOG_ONLY, "", false);
    }

    @Override
    public double getPosition() {
        return super.getPosition();
    }
    public void setPosition(double position) {
        super.setPosition(position);
    }

    public void setAngle(double angle) {
        super.setAngle(angle);
    }
    public double getAngle() {
        return super.getAngle();
    }
}
    
