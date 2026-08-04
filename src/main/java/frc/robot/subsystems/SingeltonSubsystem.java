package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * A singleton subsystem that ensures only one instance of the subsystem exists.
 * References to this subsystem should be obtained through the getInstance() static method.
 */
public class SingeltonSubsystem extends SubsystemBase {  
    
    // The single instance of the subsystem
    private static SingeltonSubsystem instance = null;

    /**
     * Returns the single instance of the SingeltonSubsystem. If the instance does not exist, it creates a new one.
     * @return the reference to the single instance of SingeltonSubsystem
     */
    public static SingeltonSubsystem getInstance() {
        if (instance == null) {
            new SingeltonSubsystem();
        }
        return instance;
    }

    /**
     *  private constructor to prevent external instantiation. This ensures that the subsystem can only be created through the getInstance() method.
     */
    private SingeltonSubsystem() {
        super();
        instance = this;    
    }


}
