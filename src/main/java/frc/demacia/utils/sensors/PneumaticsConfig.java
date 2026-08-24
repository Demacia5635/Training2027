package frc.demacia.utils.sensors;

import edu.wpi.first.wpilibj.PneumaticsModuleType;

public class PneumaticsConfig {
    public final int module;
    public final PneumaticsModuleType moduleType;
    public final String name;


    public PneumaticsConfig(int module, PneumaticsModuleType moduleType,String name) {
        this.module = module;
        this.moduleType = moduleType;
        this.name = name;
    }
}