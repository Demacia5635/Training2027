package frc.demacia.utils.sensors;

import edu.wpi.first.wpilibj.Compressor;
import frc.demacia.utils.log.LogManager;
import frc.demacia.utils.log.LogEntryBuilder.LogLevel;

public class Pneumatics extends Compressor {
    PneumaticsConfig config;
    String name;
	
    public Pneumatics(PneumaticsConfig config) {
        super(config.module, config.moduleType);
        this.config = config;
        this.name= config.name;
        addLog();
        LogManager.log(name + " Pneumatics initialized");
    }

    @SuppressWarnings("unchecked")
    private void addLog() {
        LogManager.addEntry(name + ": Compressor State, Pressure Switch",  
            () -> getCompressorState(),
            () -> getPressureSwitch()
        ).withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP).build();
        LogManager.addEntry(name + ": Current, Analog Voltage, Analog Pressure",  
            () -> getCurrent(),
            () -> getAnalogVoltage(),
            () -> getAnalogPressure()
        ).withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP).build();
    }

    public String getName() {
        return config.name;   
    }
    public boolean getCompressorState() {
        return super.isEnabled();
    }
    public boolean getPressureSwitch() {
        return super.getPressureSwitchValue();
    }
    public double getCurrent() {
        return super.getCurrent();
    }
    public double getAnalogVoltage() {
        return super.getAnalogVoltage();
    }
    public double getAnalogPressure() {
        return super.getPressure();
    }
    public void disableCompressor() {
        super.disable();
    }
    public void enableCompressorDigital() {
        super.enableDigital();
    }
    public void enableCompressorAnalog(double minPressure, double maxPressure) {
        super.enableAnalog(minPressure, maxPressure);
    }
    public void enableHybrid(double minPressure, double maxPressure) {
        super.enableHybrid(minPressure, maxPressure);
    }

}



