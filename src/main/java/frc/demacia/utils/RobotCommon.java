package frc.demacia.utils;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotCommon implements Sendable {
    private static boolean isRed = true;
    private static boolean isComp = false;

    static {
        SmartDashboard.putData("RC", new RobotCommon());
    }

    public static void init() {}

    public static boolean getIsRed(){
        return isRed;
    }

    public static void setIsRed(boolean newIsRed){
        isRed = newIsRed;
    }

    public static boolean getIsComp(){
        return isComp;
    }

    public static void setIsComp(boolean newIsComp){
        isComp = newIsComp;
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("RobotCommon");
        builder.addBooleanProperty("is red", () -> getIsRed(), (val) -> setIsRed(val));
        builder.addBooleanProperty("is comp", () -> getIsComp(), (val) -> setIsComp(val));
    }
}