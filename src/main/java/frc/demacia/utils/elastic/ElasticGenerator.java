package frc.demacia.utils.elastic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.Pair;
import edu.wpi.first.net.WebServer;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.demacia.utils.chassis.Chassis;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.mechanisms.BaseMechanism;
import frc.demacia.utils.mechanisms.StateBaseMechanism;
import frc.demacia.utils.motors.MotorInterface;
import frc.demacia.utils.sensors.SensorInterface;
import frc.demacia.vision.subsystem.Camera;
import frc.demacia.utils.sensors.Cancoder;

public class ElasticGenerator {
    private static ElasticGenerator instance;

    private static final String pathOnRobot = "/home/lvuser/elastic_layouts";

    private static final int MAX_COLS = 10;
    private static final int MAX_ROWS = 4;

    private List<MotorInterface> allMotors = new ArrayList<>();
    private List<SensorInterface> allSensors = new ArrayList<>();
    private List<Camera> allTags = new ArrayList<>();
    private List<BaseMechanism> mechanisms = new ArrayList<>();
    private List<Pair<BaseMechanism, MotorInterface>> powerCmds = new ArrayList<>();
    private List<Pair<BaseMechanism, MotorInterface>> autoCalibration = new ArrayList<>();

    private Cancoder[] chassisCancoders = new Cancoder[4];

    private ElasticGenerator() {
        SmartDashboard.putData("elastic/Generate Layout", new InstantCommand(this::generateAndPublishLayout).ignoringDisable(true));
        
        File dir;
        if (RobotBase.isSimulation()) {
            dir = Filesystem.getDeployDirectory();
        } else {
            dir = new File(pathOnRobot);
        }
        try {
            WebServer.start(5800, dir.getPath());
        } catch (Exception e) {
            Log.log("Failed to start WebServer for Elastic: " + e.getMessage());
        }
    }

    public static ElasticGenerator getInstance() {
        if (instance == null) {
            instance = new ElasticGenerator();
        }
        return instance;
    }

    public void registerMotor(MotorInterface motor) {
        if (!allMotors.contains(motor)) {
            allMotors.add(motor);
        }
    }

    public void registerSensor(SensorInterface sensor) {
        if (!allSensors.contains(sensor)) {
            allSensors.add(sensor);
        }
    }

    public void registerTag(Camera tagPose) {
        if (!allTags.contains(tagPose)) {
            allTags.add(tagPose);
        }
    }

    public void registerMechanism(BaseMechanism mech) {
        if (!mechanisms.contains(mech)) {
            mechanisms.add(mech);
        }
    }

    public void registerPowerCommand(BaseMechanism mech, MotorInterface motor) {
        powerCmds.add(new Pair<>(mech, motor));
    }

    public void registerAutoCalibration(BaseMechanism mech, MotorInterface motor) {
        autoCalibration.add(new Pair<>(mech, motor));
    }

    public void generateAndPublishLayout() {
        StringBuilder json = new StringBuilder();
        
        json.append("{\n");
        json.append("  \"version\": 1.0,\n");
        json.append("  \"grid_size\": 128,\n");
        json.append("  \"tabs\": [\n");

        json.append(buildTunerTabs());
        if (Chassis.getInstance() != null) {
            json.append(buildChassisTab());
        }
        json.append(buildVisionTab());
        json.append(buildSysidTabs());
        json.append(buildMechanismTabs());

        json.append("\n  ]\n");
        json.append("}\n");

        File dir;
        if (RobotBase.isSimulation()) {
            dir = Filesystem.getDeployDirectory();
        } else {
            dir = new File(pathOnRobot);
        }
        File file = new File(dir, "Generated_Elastic_Layout.json");

        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Files.writeString(
                file.toPath(), 
                json.toString(), 
                StandardOpenOption.CREATE, 
                StandardOpenOption.TRUNCATE_EXISTING, 
                StandardOpenOption.WRITE
            );
            
            SmartDashboard.putString("elastic/Status", "Saved at: " + file.getAbsolutePath());
            Log.log("Elastic layout saved successfully at: " + file.getAbsolutePath());  
        } catch (IOException e) {
            Log.log("Failed to save Elastic layout: " + e.getMessage());
            SmartDashboard.putString("elastic/Status", "Failed to save: " + e.getMessage());
        }
    }

    private String buildTunerTabs() {
        StringBuilder sb = new StringBuilder();
        int tabIndex = 1;
        boolean firstTab = true;
        final int MOTOR_WIDTH = 5;

        int motorIndex = 0;
        int sensorIndex = 0;

        while (motorIndex < allMotors.size() || sensorIndex < allSensors.size() || (allMotors.isEmpty() && allSensors.isEmpty())) {
            if (!firstTab) sb.append(",\n");
            
            String tabName = "Tuner" + (tabIndex > 1 ? " " + tabIndex : "");
            sb.append("    {\n");
            sb.append("      \"name\": \"").append(tabName).append("\",\n");
            sb.append("      \"grid_layout\": {\n        \"layouts\": [],\n        \"containers\": [\n");
            
            List<String> widgets = new ArrayList<>();
            int row = 0;
            int col = 0;

            while (motorIndex < allMotors.size() && row < MAX_ROWS) {
                MotorInterface motor = allMotors.get(motorIndex);
                String motorPath = "/SmartDashboard/motors/" + motor.getName();
                String logManagerMotorPath = "/Log/motors/" + motor.getName();
                
                widgets.add(createWidget("Boolean Box", motor.getName(), col, row, 1, 1, logManagerMotorPath + "/is Connected", "\"data_type\": \"boolean\""));
                col++;
                widgets.add(createWidget("ComboBox Chooser", "Value Control", col, row, 1, 1, motorPath + "/Value Control Mode Chooser", ""));
                col++;
                widgets.add(createWidget("Text Display", "Values", col, row, 1, 1, motorPath + "/test Value", "\"data_type\": \"double\", \"show_submit_button\": true"));
                col++;
                widgets.add(createWidget("Number Slider", "value", col, row, 2, 1, motorPath + "/test Value", "\"data_type\": \"double\", \"update_continuously\": true"));
                col += 2;
                widgets.add(createWidget("Command", "Run", col, row, 1, 1, motorPath + "/test value command", "\"show_type\": true"));
                col++;
                widgets.add(createWidget("Text Display", "Current Values", col, row, 1, 1, logManagerMotorPath + "/current value", "\"data_type\": \"double\", \"show_submit_button\": true"));
                col++;

                if (col > MAX_COLS - MOTOR_WIDTH) { 
                    col = 0;
                    row++;
                }
                motorIndex++;
            }

            while (sensorIndex < allSensors.size() && row < MAX_ROWS) {
                SensorInterface sensor = allSensors.get(sensorIndex);
                String sensorTopic = "/SmartDashboard/sensors/" + sensor.getName() + "/is Connected";
                
                if (SmartDashboard.containsKey("sensors/" + sensor.getName() + "/is Connected")) {
                    widgets.add(createWidget("Boolean Box", sensor.getName(), col, row, 1, 1, sensorTopic, "\"data_type\": \"boolean\""));
                    col++;
                }
                
                if (col >= MAX_COLS) { 
                    col = 0;
                    row++;
                }
                sensorIndex++;
            }
            
            if (allMotors.isEmpty() && allSensors.isEmpty()) {
                widgets.add(createWidget("Text Display", "Status", 0, 0, 4, 1, "", "\"data_type\": \"string\""));
                sb.append(String.join(",\n", widgets));
                sb.append("\n        ]\n      }\n    }");
                break;
            }

            sb.append(String.join(",\n", widgets));
            sb.append("\n        ]\n      }\n    }");
            
            tabIndex++;
            firstTab = false;
        }
        return sb.toString();
    }

    private String buildChassisTab() {
        StringBuilder sb = new StringBuilder();
        sb.append(",\n");
        sb.append("    {\n");
        sb.append("      \"name\": \"Chassis\",\n");
        sb.append("      \"grid_layout\": {\n        \"layouts\": [],\n        \"containers\": [\n");
        
        sb.append(createWidget("Field", "Field", 0, 0, 2, 4, "/SmartDashboard/chassis/field", "\"field_rotation\": 90.0"));
        
        String gyroName = Chassis.getInstance().gyro.getName();
        if (Chassis.getInstance().gyro != null) {
            sb.append(",\n");
            sb.append(createWidget("Large Text Display", gyroName, 2, 0, 2, 2, "/SmartDashboard/sensors/" + gyroName + "/yaw Degree", "\"data_type\": \"double\""));
        }

        sb.append(",\n");
        sb.append(createWidget("Command", "Reset Gyro", 4, 0, 2, 1, "/SmartDashboard/chassis/reset gyro", "\"show_type\": true"));
        sb.append(",\n");
        sb.append(createWidget("Command", "Reset 180", 4, 1, 2, 1, "/SmartDashboard/chassis/reset gyro 180", "\"show_type\": true"));

        sb.append(",\n");
        sb.append(createWidget("Command", "Reset Odometry", 8, 0, 2, 1, "/SmartDashboard/chassis/reset odmetry", "\"show_type\": true"));

        chassisCancoders = Chassis.getInstance().getCancoders();
        if (chassisCancoders != null) {
            for (Cancoder cancoder : chassisCancoders) {
                if (cancoder.getName().contains("Front Left") || cancoder.getName().contains("FrontLeft") || cancoder.getName().contains("FL")) {
                    sb.append(",\n");
                    sb.append(createWidget("Text Display", "Front Left Abs", 2, 2, 1, 1, "/SmartDashboard/sensors/" + chassisCancoders[0].getName() + "/Abs Position", "\"data_type\": \"double\""));
                }
                if (cancoder.getName().contains("Front Right") || cancoder.getName().contains("FrontRight") || cancoder.getName().contains("FR")) {
                    sb.append(",\n");
                    sb.append(createWidget("Text Display", "Front Right Abs", 3, 2, 1, 1, "/SmartDashboard/sensors/" + chassisCancoders[1].getName() + "/Abs Position", "\"data_type\": \"double\""));
                }
                if (cancoder.getName().contains("Back Left") || cancoder.getName().contains("BackLeft") || cancoder.getName().contains("BL")) {
                    sb.append(",\n");
                    sb.append(createWidget("Text Display", "Back Left Abs", 2, 3, 1, 1, "/SmartDashboard/sensors/" + chassisCancoders[2].getName() + "/Abs Position", "\"data_type\": \"double\""));
                }
                if (cancoder.getName().contains("Back Right") || cancoder.getName().contains("BackRight") || cancoder.getName().contains("BR")) {
                    sb.append(",\n");
                    sb.append(createWidget("Text Display", "Back Right Abs", 3, 3, 1, 1, "/SmartDashboard/sensors/" + chassisCancoders[3].getName() + "/Abs Position", "\"data_type\": \"double\""));
                }
            }
        }

        sb.append(",\n");
        sb.append(createWidget("Command", "Coast Chassis", 4, 2, 2, 1, "/SmartDashboard/chassis/set coast", "\"show_type\": true"));
        sb.append(",\n");
        sb.append(createWidget("Command", "Brake Chassis", 4, 3, 2, 1, "/SmartDashboard/chassis/set brake", "\"show_type\": true"));

        sb.append("\n        ]\n      }\n    }");
        return sb.toString();
    }

    private String buildVisionTab() {
        StringBuilder sb = new StringBuilder();
    
        sb.append(",\n");
        sb.append("    {\n");
        sb.append("      \"name\": \"Vision\",\n");
        sb.append("      \"grid_layout\": {\n");
        sb.append("        \"layouts\": [],\n");
        sb.append("        \"containers\": [\n");
    
        List<String> visionWidgets = new ArrayList<>();
    
        visionWidgets.add(createWidget("Field", "Quest Robot Field", 0, 0, 2, 3, "/SmartDashboard/quest/Quest Robot Field", "\"field_rotation\": 90.0"));
        visionWidgets.add(createWidget("Command", "Reset Quest Pose", 0, 3, 2, 1, "/SmartDashboard/quest/Reset Quest Pose", "\"show_type\": true"));
        visionWidgets.add(createWidget("Boolean Box", "is quest connected", 0, 4, 1, 1, "/Log/quest/is connected", "\"data_type\": \"boolean\""));
        visionWidgets.add(createWidget("Boolean Box", "is quest working", 1, 4, 1, 1, "/Log/quest/is working", "\"data_type\": \"boolean\""));

        sb.append(String.join(",\n", visionWidgets));
    
        if (allTags.isEmpty()) {
            sb.append("\n");
            sb.append("        ]\n");
            sb.append("      }\n");
            sb.append("    }");
            return sb.toString();
        }
    
        final int TAG_WIDTH = 2;
    
        int tagIndex = 0;
        int tabIndex = 1;
        
        int col = 2;
    
        while (tagIndex < allTags.size()) {
            if (tabIndex > 1) {
                sb.append(",\n");
                sb.append("    {\n");
                sb.append("      \"name\": \"Vision");
                sb.append(" ").append(tabIndex);
                sb.append("\",\n");
                sb.append("      \"grid_layout\": {\n");
                sb.append("        \"layouts\": [],\n");
                sb.append("        \"containers\": [\n");    
            }        
    
            List<String> widgets = new ArrayList<>();
    
            while (tagIndex < allTags.size() && col < MAX_COLS) {
                Camera tag = allTags.get(tagIndex);
                String tagPath = "/SmartDashboard/tags/" + tag.getName();
    
                widgets.add(createWidget("Field", tag.getName() + " Field", col, 0, 2, 3, tagPath + "/field-tag " + tag.getName(), "\"field_rotation\": 90.0"));
                widgets.add(createWidget("Boolean Box", "See " + tag.getName(), col, 3, 1, 1, tagPath + "/" + tag.getName() + " see tag", "\"data_type\": \"boolean\""));

                col += TAG_WIDTH;
                tagIndex++;
            }

            if (!visionWidgets.isEmpty() && tabIndex == 1) {
                sb.append(",\n");
            }
            sb.append(String.join(",\n", widgets));
            
            sb.append("\n");
            sb.append("        ]\n");
            sb.append("      }\n");
            sb.append("    }");
    
            col = 0;
            tabIndex++;
        }
    
        return sb.toString();
    }

    private String buildSysidTabs() {
        StringBuilder sb = new StringBuilder();
        int tabIndex = 1;
        boolean firstTab = true;
        int MOTOR_WIDTH = 2;

        int motorIndex = 0;

        sb.append(",\n");

        while (motorIndex < allMotors.size() || (allMotors.isEmpty())) {
            if (!firstTab) sb.append(",\n");
            
            String tabName = "Sysid" + (tabIndex > 1 ? " " + tabIndex : "");
            sb.append("    {\n");
            sb.append("      \"name\": \"").append(tabName).append("\",\n");
            sb.append("      \"grid_layout\": {\n        \"layouts\": [\n");
            
            List<String> widgets = new ArrayList<>();
            int row = 0;
            int col = 2;

            while (motorIndex < allMotors.size() && col < MAX_COLS) {
                MotorInterface motor = allMotors.get(motorIndex);
                
                String motorPath = "/SmartDashboard/motors/" + motor.getName();
                
                StringBuilder listLayout = new StringBuilder();
                double xPos = col * 128.0;
                double yPos = row * 128.0;
                double w = MOTOR_WIDTH * 128.0;
                double h = (MAX_ROWS - row) * 128.0;

                listLayout.append("          {\n");
                listLayout.append("            \"type\": \"List Layout\",\n");
                listLayout.append("            \"title\": \"").append(motor.getName()).append("\",\n");
                listLayout.append("            \"x\": ").append(xPos).append(",\n");
                listLayout.append("            \"y\": ").append(yPos).append(",\n");
                listLayout.append("            \"width\": ").append(w).append(",\n");
                listLayout.append("            \"height\": ").append(h).append(",\n");
                listLayout.append("            \"properties\": {\n");
                listLayout.append("              \"label_position\": \"TOP\"\n");
                listLayout.append("            },\n");
                listLayout.append("            \"children\": [\n");

                String commandName = powerCmds.stream()
                        .filter(pair -> pair.getSecond().equals(motor))
                        .map(pair -> pair.getFirst().getName() + " Power Command")
                        .findFirst()
                        .orElse(null);

                if (commandName != null) {
                    listLayout.append("              {\n");
                    listLayout.append("                \"title\": \"").append(commandName).append("\",\n");
                    listLayout.append("                \"x\": 0.0,\n");
                    listLayout.append("                \"y\": 0.0,\n");
                    listLayout.append("                \"width\": 128.0,\n");
                    listLayout.append("                \"height\": 128.0,\n");
                    listLayout.append("                \"type\": \"Command\",\n");
                    listLayout.append("                \"properties\": {\n");
                    listLayout.append("                  \"topic\": \"/SmartDashboard/").append(commandName).append("\",\n"); 
                    listLayout.append("                  \"show_type\": true,\n");
                    listLayout.append("                  \"maximize_button_space\": false\n");
                    listLayout.append("                }\n");
                    listLayout.append("              },\n");
                }

                String[] pidffParams = {"KP", "KI", "KD", "KS", "KV", "KA", "KG", "KSIN", "KV2"};
                for (int i = 0; i < pidffParams.length; i++) {
                    String param = pidffParams[i];
                    
                    if (!param.equals("KP") && !param.equals("KI") && !param.equals("KD")) {
                        listLayout.append("              {\n");
                        listLayout.append("                \"title\": \"USE_").append(param).append("\",\n");
                        listLayout.append("                \"x\": 0.0,\n");
                        listLayout.append("                \"y\": 0.0,\n");
                        listLayout.append("                \"width\": 128.0,\n");
                        listLayout.append("                \"height\": 128.0,\n");
                        listLayout.append("                \"type\": \"Toggle Switch\",\n");
                        listLayout.append("                \"properties\": {\n");
                        listLayout.append("                  \"topic\": \"").append(motorPath).append("/PID+FF config slot 0/USE_").append(param).append("\",\n");
                        listLayout.append("                  \"period\": 0.06,\n");
                        listLayout.append("                  \"data_type\": \"boolean\"\n");
                        listLayout.append("                }\n");
                        listLayout.append("              },\n");
                    }

                    listLayout.append("              {\n");
                    listLayout.append("                \"title\": \"").append(param).append("\",\n");
                    listLayout.append("                \"x\": 0.0,\n");
                    listLayout.append("                \"y\": 0.0,\n");
                    listLayout.append("                \"width\": 128.0,\n");
                    listLayout.append("                \"height\": 128.0,\n");
                    listLayout.append("                \"type\": \"Text Display\",\n");
                    listLayout.append("                \"properties\": {\n");
                    listLayout.append("                  \"topic\": \"").append(motorPath).append("/PID+FF config slot 0/").append(param).append("\",\n");
                    listLayout.append("                  \"period\": 0.06,\n");
                    listLayout.append("                  \"data_type\": \"double\",\n");
                    listLayout.append("                  \"show_submit_button\": true\n");
                    listLayout.append("                }\n");
                    listLayout.append("              },\n");
                }

                listLayout.append("              {\n");
                listLayout.append("                \"title\": \"Update\",\n");
                listLayout.append("                \"x\": 0.0,\n");
                listLayout.append("                \"y\": 0.0,\n");
                listLayout.append("                \"width\": 128.0,\n");
                listLayout.append("                \"height\": 128.0,\n");
                listLayout.append("                \"type\": \"Toggle Button\",\n");
                listLayout.append("                \"properties\": {\n");
                listLayout.append("                  \"topic\": \"").append(motorPath).append("/PID+FF config slot 0/Update\",\n");
                listLayout.append("                  \"period\": 0.06,\n");
                listLayout.append("                  \"data_type\": \"boolean\"\n");
                listLayout.append("                }\n");
                listLayout.append("              }\n");
                
                listLayout.append("            ]\n");
                listLayout.append("          }");

                widgets.add(listLayout.toString());

                row = 0;
                col += MOTOR_WIDTH;
                motorIndex++;
            }

            sb.append(String.join(",\n", widgets));
            sb.append("\n        ],\n");

            sb.append("        \"containers\": [\n");
            List<String> containers = new ArrayList<>();
            
            if (allMotors.isEmpty()) {
                containers.add(createWidget("Text Display", "Status", 0, 0, 4, 1, "", "\"data_type\": \"string\""));
            } else {
                containers.add(createWidget("Command", "sysid Command", 0, 0, 2, 1, "/SmartDashboard/sysID/sysidCommand", "\"show_type\": true, \"maximize_button_space\": false"));
            }

            sb.append(String.join(",\n", containers));
            sb.append("\n        ]\n");
            sb.append("      }\n");
            sb.append("    }");
            
            tabIndex++;
            firstTab = false;
        }
        return sb.toString();
    }

    private String buildMechanismTabs() {
        if (mechanisms.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();

        final int WIDGET_WIDTH = 2;

        for (int i = 0; i < mechanisms.size(); i++) {
            sb.append(",\n");
            BaseMechanism mech = mechanisms.get(i);
            
            int tabIndex = 1;
            String mechPath = "/SmartDashboard/" + mech.getName();
            int motorIndex = 0;
            int sensorIndex = 0;
            boolean firstTab = true;
            
            while (motorIndex < mech.getMotors().length || sensorIndex < mech.getSensors().length) {
                if (!firstTab) sb.append(",\n");
            
                String tabName = mech.getName() + (tabIndex > 1 ? " " + tabIndex : "");
                sb.append("    {\n");
                sb.append("      \"name\": \"").append(tabName).append("\",\n");
                sb.append("      \"grid_layout\": {\n        \"layouts\": [],\n        \"containers\": [\n");
            
                List<String> widgets = new ArrayList<>();
                int xOffset = 0;
                int yOffset = 0;

                if (tabIndex == 1 && mech instanceof StateBaseMechanism) {
                    widgets.add(createWidget("ComboBox Chooser", mech.getName() + " State Chooser", xOffset, 0, 1, 1, mechPath + "/" + mech.getName() + " State Chooser", "\"sort_options\": false"));
                    widgets.add(createWidget("Text Display", "State", xOffset + 1, 0, 1, 1, mechPath + "/" + mech.getName() + " State", "\"data_type\": \"string\", \"show_submit_button\": false"));
                    widgets.add(createWidget("Text Display", "Test Values", xOffset, 1, WIDGET_WIDTH, 1, mechPath + "/" + mech.getName() + " Test Values", "\"data_type\": \"double[]\", \"show_submit_button\": true"));
                    xOffset += WIDGET_WIDTH;
                }

                while (motorIndex < mech.getMotors().length) {
                    MotorInterface motor = mech.getMotor(motorIndex);

                    String motorName = motor.getName();
                    
                    int requiredRows = 4;
                    if (powerCmds.contains(new Pair<>(mech, motor))) requiredRows++;
                    if (autoCalibration.contains(new Pair<>(mech, motor))) requiredRows++;
                    if (NetworkTableInstance.getDefault().getTable("SmartDashboard").getSubTable(mech.getName()).containsSubTable(motorName + " Calibration Command")) requiredRows++;
                    
                    boolean needsExtraCol = requiredRows > MAX_ROWS;
                    
                    if ((!needsExtraCol && xOffset >= MAX_COLS) || (needsExtraCol && xOffset > MAX_COLS - (WIDGET_WIDTH * 2))) {
                        break;
                    }
                    
                    String baseTopic = mechPath + "/" + motorName + "/";
                    
                    widgets.add(createWidget("Text Display", motorName + " wanted", xOffset, yOffset, 1, 1, baseTopic + motorName + " wanted value", "\"data_type\": \"double\", \"show_submit_button\": false"));
                    widgets.add(createWidget("Text Display", motorName + " current", xOffset + 1, yOffset, 1, 1, baseTopic + motorName + " current Value", "\"data_type\": \"double\", \"show_submit_button\": false"));
                    yOffset++;
                    
                    if (yOffset >= MAX_ROWS) { yOffset = 0; xOffset += WIDGET_WIDTH; }
                    widgets.add(createWidget("Command", "Coast " + motorName, xOffset, yOffset, WIDGET_WIDTH, 1, baseTopic + "set coast " + motorName, "\"show_type\": true"));
                    yOffset++;
                    
                    if (yOffset >= MAX_ROWS) { yOffset = 0; xOffset += WIDGET_WIDTH; }
                    widgets.add(createWidget("Command", "Brake " + motorName, xOffset, yOffset, WIDGET_WIDTH, 1, baseTopic + "set brake " + motorName, "\"show_type\": true"));
                    yOffset++;
                    
                    if (powerCmds.contains(new Pair<>(mech, motor))) {
                        if (yOffset >= MAX_ROWS) { yOffset = 0; xOffset += WIDGET_WIDTH; }
                        widgets.add(createWidget("Command", "Power " + motorName, xOffset, yOffset, WIDGET_WIDTH, 1, baseTopic + "set power command " + motorName, "\"show_type\": true"));
                        yOffset++;
                    }

                    if (NetworkTableInstance.getDefault().getTable("SmartDashboard").getSubTable(mech.getName()).containsSubTable(motorName + " Calibration Command")) {
                        if (yOffset >= MAX_ROWS) { yOffset = 0; xOffset += WIDGET_WIDTH; }
                        widgets.add(createWidget("Command", "Calibration Command " + motorName, xOffset, yOffset, WIDGET_WIDTH, 1, baseTopic + motorName + " Calibration Command", "\"show_type\": true"));
                        yOffset++;
                    }
                    
                    if (autoCalibration.contains(new Pair<>(mech, motor))) {
                        if (yOffset >= MAX_ROWS) { yOffset = 0; xOffset += WIDGET_WIDTH; }
                        widgets.add(createWidget("Command", "Reset " + motorName, xOffset, yOffset, WIDGET_WIDTH, 1, baseTopic + motorName + " manual reset", "\"show_type\": true"));
                        yOffset++;
                    }
                    
                    if (yOffset >= MAX_ROWS) { yOffset = 0; xOffset += WIDGET_WIDTH; }
                    widgets.add(createWidget("Boolean Box", "Calibrated", xOffset, yOffset, WIDGET_WIDTH, 1, baseTopic + motorName + " has Calibrated", "\"data_type\": \"boolean\", \"true_color\": 4283215696, \"false_color\": 4294198070"));
                    yOffset++;
                    
                    xOffset += WIDGET_WIDTH;
                    yOffset = 0;
                    motorIndex++;
                }

                if (motorIndex >= mech.getMotors().length) {
                    while (sensorIndex < mech.getSensors().length) {
                        if (yOffset >= MAX_ROWS) {
                            yOffset = 0;
                            xOffset += WIDGET_WIDTH;
                        }
                        
                        if (xOffset >= MAX_COLS) {
                            break; 
                        }
                        
                        SensorInterface sensor = mech.getSensor(sensorIndex);
                        String sensorTopic = "/SmartDashboard/sensors/" + sensor.getName() + "/value";
                        
                        if (sensor instanceof frc.demacia.utils.sensors.DigitalSensorInterface) {
                            widgets.add(createWidget("Boolean Box", sensor.getName(), xOffset, yOffset, WIDGET_WIDTH, 1, sensorTopic, "\"data_type\": \"boolean\""));
                        } else {
                            widgets.add(createWidget("Text Display", sensor.getName(), xOffset, yOffset, WIDGET_WIDTH, 1, sensorTopic, "\"data_type\": \"double\", \"show_submit_button\": false"));
                        }
                        
                        yOffset++;
                        sensorIndex++;
                    }
                }

                sb.append(String.join(",\n", widgets));
                sb.append("\n        ]\n      }\n    }");
                
                tabIndex++;
                firstTab = false;
            }
        }
        return sb.toString();
    }

    private String createWidget(String type, String title, double gridX, double gridY, double gridWidth, double gridHeight, String topic, String extraProps) {
        double x = gridX * 128.0;
        double y = gridY * 128.0;
        double width = gridWidth * 128.0;
        double height = gridHeight * 128.0;

        StringBuilder w = new StringBuilder();
        w.append("          {\n");
        w.append("            \"type\": \"").append(type).append("\",\n");
        w.append("            \"title\": \"").append(title).append("\",\n");
        w.append("            \"x\": ").append(x).append(",\n");
        w.append("            \"y\": ").append(y).append(",\n");
        w.append("            \"width\": ").append(width).append(",\n");
        w.append("            \"height\": ").append(height).append(",\n");
        w.append("            \"properties\": {\n");
        w.append("              \"topic\": \"").append(topic).append("\",\n");
        w.append("              \"period\": 0.06");
        if (extraProps != null && !extraProps.isEmpty()) {
            w.append(",\n              ").append(extraProps);
        }
        w.append("\n            }\n");
        w.append("          }");
        return w.toString();
    }
}