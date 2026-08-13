package frc.demacia.utils.sensors;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Compressor;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.log.Log.LogLevel;

public class Pneumatics extends Compressor {
    PneumaticsConfig config;
    String name;
	
    public Pneumatics(PneumaticsConfig config) {
        super(config.module, config.moduleType);
        this.config = config;
        this.name= config.name;
        addLog();
        Log.log(name + " Pneumatics initialized");
    }

    @SuppressWarnings("unchecked")
    private void addLog() {
        Log.putData(name + ": Compressor State, Pressure Switch", 
            new Supplier[]{
                this::getCompressorState,
                this::getPressureSwitch
            }
            , LogLevel.LOG_ONLY, "", false);
 
        Log.putData(name + ": Current, Analog Voltage, Analog Pressure", 
            new Supplier[]{
                this::getCurrent,
                this::getAnalogVoltage,
                this::getAnalogPressure
            }
        , LogLevel.LOG_ONLY, "", false);
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



