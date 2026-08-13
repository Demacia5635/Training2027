package frc.demacia.utils.sensors;

import edu.wpi.first.wpilibj.Servo;
import frc.demacia.utils.log.LogManager;
import frc.demacia.utils.log.LogEntryBuilder.LogLevel;

public class ServoMotor extends Servo {

    ServoMotorConfig config;
    String name;
    public ServoMotor (ServoMotorConfig config) {
        super(config.id);
        this.config = config;
        name=config.name;
        addLog();
        LogManager.log(name + " ServoMotor initialized");
    }

    @SuppressWarnings("unchecked")
    private void addLog() {
        LogManager.addEntry(name + ": Position, Angle", 
            () -> getPosition(),
            () -> getAngle()
        ).withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP).build();
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
    
