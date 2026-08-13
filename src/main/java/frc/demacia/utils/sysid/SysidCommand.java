package frc.demacia.utils.sysid;

import java.util.List;
import java.util.Map;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.log.LogReader;
import frc.demacia.utils.log.LogReader.Entry;
import frc.demacia.utils.motors.CloseLoopParam;
import frc.demacia.utils.motors.MotorInterface;

public class SysidCommand extends InstantCommand {
    public SysidCommand() {
        super(() -> {
            Map<String, List<Entry>> groupedEntries = LogReader.getGroups(true, info -> info.metadata().contains("motor"));
            
            if (groupedEntries == null || groupedEntries.isEmpty()) {
                Log.log("No motor logs found for SysID analysis.");
                return;
            }

            for (MotorInterface motor : Sysid.getMotors()) {
                String rawName = motor.getName();

                List<Entry> motorEntries = null;
                for (String groupName : groupedEntries.keySet()) {
                    if (groupName.endsWith("/" + rawName) || groupName.equals(rawName)) {
                        motorEntries = groupedEntries.get(groupName);
                        break;
                    }
                }

                if (motorEntries != null && !motorEntries.isEmpty()) {
                    Log.log("Starting SysID for motor: " + rawName);
                    
                    Sysid analyzer = new Sysid(rawName, motorEntries, motor.getSysidFlags());

                    CloseLoopParam params = analyzer.getParams();
                    double maxVelocity = analyzer.getMaxVelocity();
                    double maxAcceleration = analyzer.getMaxAcceleration();
                    double maxJerk = analyzer.getMaxJerk();

                    if (params != null) {
                        motor.setConfigPidFf(params, 0);
                        Log.log("Successfully updated PID for: " + rawName);
                    } else {
                        Log.log("Failed to calculate valid PID for: " + rawName);
                    }

                    if (maxVelocity > 0) {
                        motor.setConfigMotionParam(maxVelocity, maxAcceleration, maxJerk);
                    }
                } else {
                    Log.log("Skipping " + rawName + " - no log entries found.");
                }
            }
        });
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }
}