package frc.demacia.utils.mechanisms;

import java.util.HashMap;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.LookUpTable;
import frc.demacia.utils.log.LogManager;
import frc.demacia.utils.motors.MotorInterface;
import frc.demacia.utils.sensors.SensorInterface;

/**
 * A base class for robot mechanisms (subsystems) that manage a collection of motors and sensors.
 * <p>
 * This class provides common functionality for:
 * <ul>
 * <li>Storing motors and sensors by name for easy retrieval.</li>
 * <li>Controlling all motors at once (stop, set power, set neutral mode).</li>
 * <li>Automatically creating SmartDashboard buttons for switching Neutral Modes (Brake/Coast).</li>
 * <li>Performing electronics checks on hardware.</li>
 * </ul>
 * </p>
 */
public class BaseMechanism extends SubsystemBase{
    /** The name of the mechanism (used for logging and dashboard) */
    protected String name;
    /** Map of motors belonging to this mechanism, keyed by their name */
    protected HashMap<String, MotorInterface> motors;
    /** Map of sensors belonging to this mechanism, keyed by their name */
    protected HashMap<String, SensorInterface> sensors;

    protected MotorInterface[] motorArray;

    protected boolean hasCalibrated = true;

    LookUpTable lookUpTable;
    DoubleSupplier distance;

    /**
     * Constructs a new BaseMechanism.
     * Initializes the motor and sensor maps and creates debug buttons on the Dashboard.
     * @param name The name of the subsystem
     * @param motors Array of motors to register
     * @param sensors Array of sensors to register
     */
    public BaseMechanism(String name, MotorInterface[] motors, SensorInterface[] sensors) {
        this.name = name;
        setName(name);
        motorArray = motors;
        // Initialize motors map
        this.motors = new HashMap<>();
        for (MotorInterface motor : motors) {
            this.motors.put(motor.getName(), motor);
        }
        
        // Initialize sensors map
        this.sensors = new HashMap<>();
        for (SensorInterface sensor : sensors) {
            this.sensors.put(sensor.getName(), sensor);
        }

        // Create individual Brake/Coast buttons for each motor
        for (String motorName : this.motors.keySet()) {
            SmartDashboard.putData(getName() + "/" + motorName + "/set brake", 
                new InstantCommand(() -> setNeutralMode(motorName, true)).ignoringDisable(true));
            SmartDashboard.putData(getName() + "/" + motorName + "/set coast", 
                new InstantCommand(() -> setNeutralMode(motorName, false)).ignoringDisable(true));
        }

        // Create global Brake/Coast buttons for the whole mechanism
        SmartDashboard.putData(getName() + "/set coast all", 
                new InstantCommand(() -> setNeutralModeAll(false)).ignoringDisable(true));
        SmartDashboard.putData(getName() + "/set brake all", 
                new InstantCommand(() -> setNeutralModeAll(true)).ignoringDisable(true));
        
        SmartDashboard.putData(name, this);
    }

    /**
     * @return The name of the mechanism
     */
    public String getName(){
        return name;
    }
    
    /**
     * Marks that this mechanism requires calibration.
     * Sets the calibration status to false.
     */
    public void withCalibration(){
        hasCalibrated = false;
    }

    /**
     * @return true if the mechanism is calibrated and ready for control, false otherwise.
     */
    public boolean getCalibration(){
        return hasCalibrated;
    }

    /**
     * Sets the calibration status of the mechanism.
     * @param hasCalibrated true if calibrated, false otherwise.
     */
    public void setCalibration(boolean hasCalibrated){
        this.hasCalibrated = hasCalibrated;
    }

    /**
     * Attaches a lookup table and a distance source to the mechanism.
     * @param lookUpTable The table for interpolation.
     * @param distance A supplier for the input value (e.g., limelight distance).
     */
    public void withLookUpTable(LookUpTable lookUpTable, DoubleSupplier distance){
        this.lookUpTable = lookUpTable;
        this.distance = distance;
    }

    /**
     * Interpolates all values from the lookup table based on current distance.
     * @return Array of interpolated values, or empty array if table not set.
     */
    public double[] getLookUpTableValues(){
        if (lookUpTable == null){
            LogManager.log("you didn't set the lookUpTable");
            return new double[0];
        }
        return lookUpTable.get(distance.getAsDouble());
    }

    /**
     * Gets a specific interpolated value from the lookup table.
     * @param i The index of the output value.
     * @return The interpolated value at index i.
     */
    public double getLookUpTableValue(int i){
        if (lookUpTable == null){
            LogManager.log("you didn't set the lookUpTable");
            return 0;
        }
        return lookUpTable.get(distance.getAsDouble())[i];
    }

    /**
     * Stops all motors in this mechanism.
     */
    public void stopAll(){
        if (motors == null) return;
        for (MotorInterface motor : motors.values()){
            motor.stop();
        }
    }

    /**
     * Stops a specific motor by name.
     * @param motorName The name of the motor to stop
     */
    public void stop(String motorName){
        if (isValidMotor(motorName)){
            motors.get(motorName).setDuty(0);
        }
    }

    /**
     * Stops a specific motor by index.
     * @param motorIndex The index of the motor to stop
     */
    public void stop(int motorIndex){
        if (isValidMotor(motorIndex)){
            motorArray[motorIndex].setDuty(0);
        }
    }

    /**
     * Sets the duty cycle (power) for all motors.
     * @param power The power to set [-1.0, 1.0]
     */
    public void setPowerAll(double power) {
        if (motors == null || !hasCalibrated) return;
        for (MotorInterface motor : motors.values()){
            motor.setDuty(power);
        }
    }

    /**
     * Sets the duty cycle (power) for a specific motor.
     * @param motorName The name of the motor
     * @param power The power to set [-1.0, 1.0]
     */
    public void setPower(String motorName, double power){
        if (isValidMotor(motorName) && hasCalibrated){
            motors.get(motorName).setDuty(power);
        }
    }

    /**
     * Sets the duty cycle (power) for a specific motor.
     * @param motorIndex The index of the motor
     * @param power The power to set [-1.0, 1.0]
     */
    public void setPower(int motorIndex, double power){
        if (isValidMotor(motorIndex) && hasCalibrated){
            motorArray[motorIndex].setDuty(power);
        }
    }

    /**
     * Sets the Voltage for a specific motor.
     * @param motorName The name of the motor
     * @param voltage The Voltage to set
     */
    public void setVoltage(String motorName, double voltage){
        if (isValidMotor(motorName) && hasCalibrated){
            motors.get(motorName).setVoltage(voltage);
        }
    }

    /**
     * Sets the Voltage for a specific motor.
     * @param motorIndex The index of the motor
     * @param voltage The Voltage to set
     */
    public void setVoltage(int motorIndex, double voltage){
        if (isValidMotor(motorIndex) && hasCalibrated){
            motorArray[motorIndex].setVoltage(voltage);
        }
    }

    /**
     * Sets the Velocity for a specific motor.
     * @param motorName The name of the motor
     * @param velocity The Velocity to set
     */
    public void setVelocity(String motorName, double velocity){
        if (isValidMotor(motorName) && hasCalibrated){
            motors.get(motorName).setVelocity(velocity);
        }
    }

    /**
     * Sets the Velocity for a specific motor.
     * @param motorIndex The index of the motor
     * @param velocity The Velocity to set
     */
    public void setVelocity(int motorIndex, double velocity){
        if (isValidMotor(motorIndex) && hasCalibrated){
            motorArray[motorIndex].setVelocity(velocity);
        }
    }

    /**
     * Sets the position using PositionVoltage for a specific motor.
     * @param motorName The name of the motor
     * @param position The position to set
     */
    public void setPositionVoltage(String motorName, double position){
        if (isValidMotor(motorName) && hasCalibrated){
            motors.get(motorName).setPositionVoltage(position);
        }
    }

    /**
     * Sets the position using PositionVoltage for a specific motor.
     * @param motorIndex The index of the motor
     * @param position The position to set
     */
    public void setPositionVoltage(int motorIndex, double position){
        if (isValidMotor(motorIndex) && hasCalibrated){
            motorArray[motorIndex].setPositionVoltage(position);
        }
    }

    /**
     * Sets the Motion for all motors.
     * @param motion The Motion to set
     */
    public void setMotionAll(double motion) {
        if (motors == null || !hasCalibrated) return;
        for (MotorInterface motor : motors.values()){
            motor.setMotion(motion);
        }
    }

    /**
     * Sets the Motion for a specific motor.
     * @param motorName The name of the motor
     * @param motion The Motion to set
     */
    public void setMotion(String motorName, double motion){
        if (isValidMotor(motorName) && hasCalibrated){
            motors.get(motorName).setMotion(motion);
        }
    }

    /**
     * Sets the Motion for a specific motor.
     * @param motorIndex The index of the motor
     * @param motion The Motion to set
     */
    public void setMotion(int motorIndex, double motion){
        if (isValidMotor(motorIndex) && hasCalibrated){
            motorArray[motorIndex].setMotion(motion);
        }
    }

    /**
     * Sets the Angle for all motors.
     * @param angle The Angle to set
     */
    public void setAngleAll(double angle) {
        if (motors == null || !hasCalibrated) return;
        for (MotorInterface motor : motors.values()){
            motor.setAngle(angle);
        }
    }

    /**
     * Sets the Angle for a specific motor.
     * @param motorName The name of the motor
     * @param angle The Angle to set
     */
    public void setAngle(String motorName, double angle){
        if (isValidMotor(motorName) && hasCalibrated){
            motors.get(motorName).setAngle(angle);
        }
    }

    /**
     * Sets the Angle for a specific motor.
     * @param motorIndex The index of the motor
     * @param angle The Angle to set
     */
    public void setAngle(int motorIndex, double angle){
        if (isValidMotor(motorIndex) && hasCalibrated){
            motorArray[motorIndex].setAngle(angle);
        }
    }

    /**
     * Sets the neutral mode (Brake or Coast) for all motors.
     * @param isBrake true for Brake mode, false for Coast mode
     */
    public void setNeutralModeAll(boolean isBrake) {
        if (motors == null) return;
        for (MotorInterface motor : motors.values()) {
            if (motor != null) motor.setNeutralMode(isBrake);
        }
    }

    /**
     * Sets the neutral mode (Brake or Coast) for a specific motor.
     * @param motorName The name of the motor
     * @param isBrake true for Brake mode, false for Coast mode
     */
    public void setNeutralMode(String motorName, boolean isBrake){
        if (isValidMotor(motorName)){
            motors.get(motorName).setNeutralMode(isBrake);
        }
    }

    /**
     * Sets the neutral mode (Brake or Coast) for a specific motor.
     * @param motorIndex The index of the motor
     * @param isBrake true for Brake mode, false for Coast mode
     */
    public void setNeutralMode(int motorIndex, boolean isBrake){
        if (isValidMotor(motorIndex)){
            motorArray[motorIndex].setNeutralMode(isBrake);
        }
    }

    /**
     * Triggers the electronics check for all motors and sensors.
     */
    public void checkElectronicsAll() {
        if (motors == null) return;
        for (MotorInterface motor : motors.values()) {
            if (motor != null) motor.checkElectronics();
        }
        if (sensors == null) return;
        for (SensorInterface sensor : sensors.values()) {
            if (sensor != null) sensor.checkElectronics();
        }
    }

    /**
     * Checks electronics for a specific motor.
     * @param motorName The name of the motor
     */
    public void checkElectronicsMotor(String motorName){
        if (isValidMotor(motorName)){
            motors.get(motorName).checkElectronics();
        }
    }

    /**
     * Checks electronics for a specific motor.
     * @param motorIndex The index of the motor
     */
    public void checkElectronicsMotor(int motorIndex){
        if (isValidMotor(motorIndex)){
            motorArray[motorIndex].checkElectronics();
        }
    }

    /**
     * Checks electronics for a specific sensor.
     * @param sensorName The name of the sensor
     */
    public void checkElectronicsSensor(String sensorName){
        if (isValidSensor(sensorName)){
            sensors.get(sensorName).checkElectronics();
        }
    }

    /**
     * Retrieves a motor object by its name.
     * Logs an error if the motor name is invalid.
     * @param motorName The name of the motor
     * @return The MotorInterface object, or null if not found
     */
    public MotorInterface getMotor(String motorName) {
        if (!isValidMotor(motorName)){
            LogManager.log("Invalid motor: " + motorName);
            return null;
        }
        return motors.get(motorName);
    }

    /**
     * Retrieves a motor object by its index.
     * Logs an error if the motor index is invalid.
     * @param motorIndex The index of the motor
     * @return The MotorInterface object, or null if not found
     */
    public MotorInterface getMotor(int motorIndex) {
        if (!isValidMotor(motorIndex)){
            LogManager.log("Invalid motor index: " + motorIndex);
            return null;
        }
        return motorArray[motorIndex];
    }

    public MotorInterface[] getMotors() {
        return motorArray;
    }

    /**
     * Retrieves a sensor object by its name.
     * Logs an error if the sensor name is invalid.
     * @param sensorName The name of the sensor
     * @return The SensorInterface object, or null if not found
     */
    public SensorInterface getSensor(String sensorName) {
        if (!isValidSensor(sensorName)){
            LogManager.log("Invalid sensor: " + sensorName);
            return null;
        }
        return sensors.get(sensorName);
    }

    /**
     * Checks if a motor name exists in the map.
     * @param motorName The name to check
     * @return true if valid, false otherwise
     */
    protected boolean isValidMotor(String motorName) {
        return motors.containsKey(motorName);
    }

    /**
     * Checks if a motor index exists.
     * @param motorIndex The index to check
     * @return true if valid, false otherwise
     */
    protected boolean isValidMotor(int motorIndex) {
        return motorIndex >= 0 && motorIndex < motorArray.length;
    }

    /**
     * Checks if a sensor name exists in the map.
     * @param sensorName The name to check
     * @return true if valid, false otherwise
     */
    protected boolean isValidSensor(String sensorName) {
        return sensors.containsKey(sensorName);
    }
}