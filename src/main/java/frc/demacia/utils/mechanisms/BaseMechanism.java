package frc.demacia.utils.mechanisms;

import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.elastic.ElasticGenerator;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.motors.MotorInterface;
import frc.demacia.utils.sensors.SensorInterface;

/**
 * A base class for robot mechanisms (subsystems) that manage a collection of motors and sensors.
 * <p>
 * This class provides common functionality for:
 * <ul>
 * <li>Storing motors and sensors by name for easy retrieval via a single MotorNode map.</li>
 * <li>Controlling motors efficiently with specific position/power conditions.</li>
 * <li>Automatically creating SmartDashboard buttons for switching Neutral Modes.</li>
 * <li>Performing electronics checks on hardware.</li>
 * </ul>
 * </p>
 */
public class BaseMechanism extends SubsystemBase{

    /**
     * Internal class to hold motor instance, limits, current wanted value, and calibration state.
     */
    protected class MotorNode {
        public MotorInterface motor;
        public double minLimit = Double.NEGATIVE_INFINITY;
        public double maxLimit = Double.POSITIVE_INFINITY;
        
        public boolean hasCalibrated = true;
        public Runnable autoCalibration = () -> {};

        public MotorNode(MotorInterface motor) {
            this.motor = motor;
        }
    }

    /** The name of the mechanism (used for logging and dashboard) */
    protected String name;
    
    /** Map of MotorNodes belonging to this mechanism, keyed by their name */
    protected HashMap<String, MotorNode> motors;
    
    /** Map of sensors belonging to this mechanism, keyed by their name */
    protected HashMap<String, SensorInterface> sensors;

    protected String[] motorNames;
    protected String[] sensorNames;

    protected int motorsAmount;
    protected int sensorsAmount;

    /**
     * Constructs a new BaseMechanism.
     * Initializes the motor and sensor maps and creates debug buttons on the Dashboard.
     * * @param name The name of the subsystem
     * @param motors Array of motors to register
     * @param sensors Array of sensors to register
     */
    public BaseMechanism(String name, MotorInterface[] motors, SensorInterface[] sensors) {
        this.name = name;
        setName(name);
        motorsAmount =  motors == null ? 0 : motors.length;
        sensorsAmount = sensors == null ? 0 : sensors.length;
        
        // Initialize motors
        motorNames = new String[motorsAmount];
        this.motors = new HashMap<>();
        
        for (int i = 0; i < motorsAmount; i++){
            motorNames[i] = motors[i].getName();
            this.motors.put(motors[i].getName(), new MotorNode(motors[i]));
        }

        // Initialize sensors map
        sensorNames = new String[sensorsAmount];
        this.sensors = new HashMap<>();
        for (int i = 0; i < sensorsAmount; i++){
            sensorNames[i] = sensors[i].getName();
            this.sensors.put(sensors[i].getName(), sensors[i]);
        }

        // Create individual Brake/Coast buttons for each motor
        for (String motorName : motorNames) {
            SmartDashboard.putData(getName() + "/" + motorName + "/set brake " + motorName, 
                new InstantCommand(() -> setNeutralMode(motorName, true)).ignoringDisable(true));
            SmartDashboard.putData(getName() + "/" + motorName + "/set coast " + motorName, 
                new InstantCommand(() -> setNeutralMode(motorName, false)).ignoringDisable(true));
        }

        // Create global Brake/Coast buttons for the whole mechanism
        SmartDashboard.putData(getName() + "/set coast " + getName(), 
                new InstantCommand(() -> setNeutralMode(false)).ignoringDisable(true));
        SmartDashboard.putData(getName() + "/set brake " + getName(), 
                new InstantCommand(() -> setNeutralMode(true)).ignoringDisable(true));
        
        SmartDashboard.putData(name, this);
        ElasticGenerator.getInstance().registerMechanism(this);
    }

    /**
     * Creates a dashboard command to control a specific motor dynamically with a DoubleSupplier.
     * * @param motorName The name of the motor
     * @param powerSupplier The supplier for the power value
     */
    public void withPowerCommand(String motorName, DoubleSupplier powerSupplier) {
        ElasticGenerator.getInstance().registerPowerCommand(this, motors.get(motorName).motor);

        SmartDashboard.putData(getName() + "/" + motorName + "/set power command " + motorName, 
            new PowerCommand(this, motorName, powerSupplier));
    }

    /**
     * Gets the name of the mechanism.
     * * @return The name string
     */
    public String getName(){
        return name;
    }
    
    /**
     * Marks a specific motor as requiring calibration.
     * * @param motorName The name of the motor
     */
    public void withCalibration(String motorName){
        MotorNode node = motors.get(motorName);
        if (node != null) {
            node.hasCalibrated = false;
        }
    }

    /**
     * Marks a specific motor as requiring calibration by index.
     * * @param motorIndex The index of the motor
     */
    public void withCalibration(int motorIndex){
        withCalibration(motorNames[motorIndex]);
    }

    /**
     * Checks if the motors are calibrated and ready for position control.
     * @return true if calibrated, false otherwise
     */
    public boolean getIsCalibrationAll(){
        for (MotorNode node : motors.values()){
            if (node == null || !node.hasCalibrated){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a specific motor is calibrated and ready for position control.
     * * @param motorName The name of the motor
     * @return true if calibrated, false otherwise
     */
    public boolean getIsCalibration(String motorName){
        MotorNode node = motors.get(motorName);
        return node != null && node.hasCalibrated;
    }

    /**
     * Checks if a specific motor is calibrated and ready for position control by index.
     * * @param motorIndex The index of the motor
     * @return true if calibrated, false otherwise
     */
    public boolean getIsCalibration(int motorIndex){
        return getIsCalibration(motorNames[motorIndex]);
    }

    /**
     * Sets the calibration status for a specific motor.
     * * @param motorName The name of the motor
     * @param hasCalibrated true if calibrated, false otherwise
     */
    public void setCalibration(String motorName, boolean hasCalibrated){
        MotorNode node = motors.get(motorName);
        if (node != null) {
            node.hasCalibrated = hasCalibrated;
        }
    }

    /**
     * Sets the calibration status for a specific motor by index.
     * * @param motorIndex The index of the motor
     * @param hasCalibrated true if calibrated, false otherwise
     */
    public void setCalibration(int motorIndex, boolean hasCalibrated){
        setCalibration(motorNames[motorIndex], hasCalibrated);
    }

    /**
     * Sets both minimum and maximum limits for a specific motor.
     * * @param motorName The name of the motor
     * @param min The minimum allowed position
     * @param max The maximum allowed position
     */
    public void addLimit(String motorName, double min,  double max) {
        MotorNode node = motors.get(motorName);
        if (node != null) {
            node.minLimit = min;
            node.maxLimit = max;
        } else {
            Log.log("Invalid motor: " + motorName);
        }
    }

    /**
     * Sets both minimum and maximum limits for a specific motor by index.
     * * @param motorIndex The index of the motor
     * @param min The minimum allowed position
     * @param max The maximum allowed position
     */
    public void addLimit(int motorIndex, double min,  double max) {
        addLimit(motorNames[motorIndex], min,  max);
    }

    /**
     * Sets the maximum position limit for a specific motor.
     * * @param motorName The name of the motor
     * @param max The maximum allowed position
     */
    public void addLimitMax(String motorName, double max) {
        MotorNode node = motors.get(motorName);
        if (node != null) {
            node.maxLimit = max;
        } else {
            Log.log("Invalid motor: " + motorName);
        }
    }

    /**
     * Sets the maximum position limit for a specific motor by index.
     * * @param motorIndex The index of the motor
     * @param max The maximum allowed position
     */
    public void addLimitMax(int motorIndex, double max) {
        addLimitMax(motorNames[motorIndex], max);
    }

    /**
     * Sets the minimum position limit for a specific motor.
     * * @param motorName The name of the motor
     * @param min The minimum allowed position
     */
    public void addLimitMin(String motorName, double min) {
        MotorNode node = motors.get(motorName);
        if (node != null) {
            node.minLimit = min;
        } else {
            Log.log("Invalid motor: " + motorName);
        }
    }

    /**
     * Sets the minimum position limit for a specific motor by index.
     * * @param motorIndex The index of the motor
     * @param min The minimum allowed position
     */
    public void addLimitMin(int motorIndex, double min) {
        addLimitMin(motorNames[motorIndex], min);
    }

    /**
     * Registers an auto-calibration routine for a specific motor.
     * * @param motorName The name of the motor
     * @param atLimit A supplier that returns true when the mechanism hits its calibration limit
     * @param resetPos The position to set the encoder to once calibrated
     */
    public void withAutoCalibration(String motorName, BooleanSupplier atLimit, double resetPos) {
        MotorNode node = motors.get(motorName);
        if (node == null) {
            Log.log("Invalid motor for auto calibration: " + motorName);
            return;
        }
        
        node.autoCalibration = () -> {
            if (!node.hasCalibrated && atLimit.getAsBoolean()){
                node.motor.setEncoderPosition(resetPos);
                node.hasCalibrated = true;
            }
        };
        node.hasCalibrated = false;
        
        ElasticGenerator.getInstance().registerAutoCalibration(this, motors.get(motorName).motor);
    
        SmartDashboard.putData(getName() + "/" + motorName + "/" + motorName + " manual reset", new InstantCommand(() -> {
            node.motor.setEncoderPosition(resetPos);
            node.hasCalibrated = true;
            Log.log(node.hasCalibrated);
        }).ignoringDisable(true));
        }

    /**
     * Stops all motors in this mechanism and resets their wanted values.
     */
    public void stop(){
        if (motors == null) return;
        for (MotorNode node : motors.values()){
            node.motor.stop();
        }
    }

    /**
     * Stops a specific motor by name and resets its wanted value.
     * * @param motorName The name of the motor to stop
     */
    public void stop(String motorName){
        MotorNode node = motors.get(motorName);
        if (node != null) {
            node.motor.stop();
        } else {
            Log.log("Invalid motor: " + motorName);
        }
    }

    /**
     * Stops a specific motor by index and resets its wanted value.
     * * @param motorIndex The index of the motor to stop
     */
    public void stop(int motorIndex){
        stop(motorNames[motorIndex]);
    }

    /**
     * Sets the duty cycle (power) for all motors.
     * * @param power The power to set [-1.0, 1.0]
     */
    public void setPowerAll(double power) {
        if (motors == null) return;
        for (MotorNode node : motors.values()){
            node.motor.setDuty(power);
        }
    }

    /**
     * Sets the duty cycle (power) for a specific motor.
     * * @param motorName The name of the motor
     * @param power The power to set [-1.0, 1.0]
     */
    public void setPower(String motorName, double power){
        MotorNode node = motors.get(motorName);
        if (node != null) {
            node.motor.setDuty(power);
        }
    }

    /**
     * Sets the duty cycle (power) for a specific motor by index.
     * * @param motorIndex The index of the motor
     * @param power The power to set [-1.0, 1.0]
     */
    public void setPower(int motorIndex, double power){
        setPower(motorNames[motorIndex], power);
    }

    /**
     * Sets the voltage for a specific motor.
     * * @param motorName The name of the motor
     * @param voltage The voltage to set
     */
    public void setVoltage(String motorName, double voltage){
        MotorNode node = motors.get(motorName);
        if (node != null) {
            node.motor.setVoltage(voltage);
        }
    }

    /**
     * Sets the voltage for a specific motor by index.
     * * @param motorIndex The index of the motor
     * @param voltage The voltage to set
     */
    public void setVoltage(int motorIndex, double voltage){
        setVoltage(motorNames[motorIndex], voltage);
    }

    /**
     * Sets the velocity for a specific motor.
     * * @param motorName The name of the motor
     * @param velocity The velocity to set
     */
    public void setVelocity(String motorName, double velocity){
        MotorNode node = motors.get(motorName);
        if (node != null) {
            node.motor.setVelocity(velocity);
        }
    }

    /**
     * Sets the velocity for a specific motor by index.
     * * @param motorIndex The index of the motor
     * @param velocity The velocity to set
     */
    public void setVelocity(int motorIndex, double velocity){
        setVelocity(motorNames[motorIndex], velocity);
    }

    /**
     * Sets the position using PositionVoltage for a specific motor.
     * Calibration is required before this command is executed.
     * * @param motorName The name of the motor
     * @param position The position to set
     */
    public void setPositionVoltage(String motorName, double position){
        MotorNode node = motors.get(motorName);
        if (node != null && node.hasCalibrated) {
            node.motor.setPositionVoltage(clampInLimits(node, position));
        }
    }

    /**
     * Sets the position using PositionVoltage for a specific motor by index.
     * Calibration is required before this command is executed.
     * * @param motorIndex The index of the motor
     * @param position The position to set
     */
    public void setPositionVoltage(int motorIndex, double position){
        setPositionVoltage(motorNames[motorIndex], position);
    }

    /**
     * Sets the motion (Magic Motion / Smart Motion) target for a specific motor.
     * Calibration is required before this command is executed.
     * * @param motorName The name of the motor
     * @param position The position to set
     */
    public void setMotion(String motorName, double position){
        MotorNode node = motors.get(motorName);
        if (node != null && node.hasCalibrated) {
            node.motor.setMotion(clampInLimits(node, position));
        }
    }

    /**
     * Sets the motion target for a specific motor by index.
     * Calibration is required before this command is executed.
     * * @param motorIndex The index of the motor
     * @param position The position to set
     */
    public void setMotion(int motorIndex, double position){
        setMotion(motorNames[motorIndex], position);
    }

    /**
     * Sets the angle for a specific motor using continuous wrap logic if configured.
     * Calibration is required before this command is executed.
     * * @param motorName The name of the motor
     * @param angle The angle to set
     */
    public void setAngle(String motorName, double angle){
        MotorNode node = motors.get(motorName);
        if (node != null && node.hasCalibrated) {
            double targetAngle = clampAngleInLimits(node, angle);
            node.motor.setMotion(targetAngle);
        }
    }

    /**
     * Sets the angle for a specific motor by index.
     * Calibration is required before this command is executed.
     * * @param motorIndex The index of the motor
     * @param angle The angle to set
     */
    public void setAngle(int motorIndex, double angle){
        setAngle(motorNames[motorIndex], angle);
    }

    /**
     * Clamps a linear position value within the min and max limits of the motor node.
     * * @param node The MotorNode containing the limits
     * @param position The requested position
     * @return The clamped position
     */
    private double clampInLimits(MotorNode node, double position) {
        return MathUtil.clamp(position, node.minLimit, node.maxLimit);
    }

    /**
     * Clamps a rotational angle within limits, applying continuous input modulus if necessary.
     * * @param node The MotorNode containing the limits
     * @param angle The requested angle
     * @return The clamped and/or wrapped angle
     */
    private double clampAngleInLimits(MotorNode node, double angle) {
        double min = node.minLimit;
        double max = node.maxLimit;
        double range = max - min;

        if (Double.isInfinite(min) || Double.isInfinite(max)) {
            return angle;
        }
    
        return MathUtil.clamp(
            MathUtil.inputModulus(
                angle,
                min - (2*Math.PI - range) / 2.0,
                max + (2*Math.PI - range) / 2.0),
            min,
            max);
    }

    /**
     * Checks if all motors in the mechanism have reached their target values within specified tolerances.
     * * @param allowedErrors An array of tolerances, matching the order of registered motors
     * @return true if all motors are within their allowed error, false otherwise
     */
    public boolean isReady(double[] allowedErrors){
        if (allowedErrors.length != motorsAmount){
            Log.log("errors amount is not the motors amounts");
            return true;
        }
        for (int i = 0; i < motorsAmount; i++){
            MotorNode node = motors.get(motorNames[i]);
            MotorInterface motor = node.motor;
            if (!motor.isReady(allowedErrors[i])){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if a specific motor has reached its target value within a specified tolerance.
     * * @param motorName The name of the motor
     * @param allowedError The allowable tolerance
     * @return true if the motor is within tolerance, false otherwise
     */
    public boolean isReady(String motorName, double allowedError){
        MotorNode node = motors.get(motorName);
        if (node == null){
            Log.log("Invalid motor: " + motorName);
            return false;
        }
        
        MotorInterface motor = node.motor;

        return motor.isReady(allowedError);
    }

    /**
     * Checks if a specific motor has reached its target value within a specified tolerance by index.
     * * @param motorIndex The index of the motor
     * @param allowedError The allowable tolerance
     * @return true if the motor is within tolerance, false otherwise
     */
    public boolean isReady(int motorIndex ,double allowedError){
        return isReady(motorNames[motorIndex], allowedError);
    }

    /**
     * Sets the neutral mode (Brake or Coast) for all motors.
     * * @param isBrake true for Brake mode, false for Coast mode
     */
    public void setNeutralMode(boolean isBrake) {
        if (motors == null) return;
        for (MotorNode node : motors.values()) {
            if (node.motor != null) node.motor.setNeutralMode(isBrake);
        }
    }

    /**
     * Sets the neutral mode (Brake or Coast) for a specific motor.
     * * @param motorName The name of the motor
     * @param isBrake true for Brake mode, false for Coast mode
     */
    public void setNeutralMode(String motorName, boolean isBrake){
        MotorNode node = motors.get(motorName);
        if (node != null) {
            node.motor.setNeutralMode(isBrake);
        }
    }

    /**
     * Sets the neutral mode (Brake or Coast) for a specific motor by index.
     * * @param motorIndex The index of the motor
     * @param isBrake true for Brake mode, false for Coast mode
     */
    public void setNeutralMode(int motorIndex, boolean isBrake){
        setNeutralMode(motorNames[motorIndex], isBrake);
    }

    /**
     * Triggers the electronics check for all motors and sensors.
     */
    public void checkElectronics() {
        checkElectronicsMotors();
        checkElectronicsSensors();
    }

    /**
     * Triggers the electronics check for all motors and sensors.
     */
    public void checkElectronicsMotors() {
        if (motors == null) return;
        for (MotorNode node : motors.values()) {
            if (node.motor != null) node.motor.checkElectronics();
        }
    }

    /**
     * Triggers the electronics check for all motors and sensors.
     */
    public void checkElectronicsSensors() {
        if (sensors == null) return;
        for (SensorInterface sensor : sensors.values()) {
            if (sensor != null) sensor.checkElectronics();
        }
    }

    /**
     * Checks electronics for a specific motor.
     * * @param motorName The name of the motor
     */
    public void checkElectronicsMotor(String motorName){
        MotorNode node = motors.get(motorName);
        if (node != null) {
            node.motor.checkElectronics();
        } else {
            Log.log("Invalid motor: " + motorName);
        }
    }

    /**
     * Checks electronics for a specific motor by index.
     * * @param motorIndex The index of the motor
     */
    public void checkElectronicsMotor(int motorIndex){
        checkElectronicsMotor(motorNames[motorIndex]);
    }

    /**
     * Checks electronics for a specific sensor.
     * * @param sensorName The name of the sensor
     */
    public void checkElectronicsSensor(String sensorName){
        SensorInterface sensor = sensors.get(sensorName);
        if (sensor != null){
            sensor.checkElectronics();
        } else {
            Log.log("Invalid sensor: " + sensorName);
        }
    }

    /**
     * Checks electronics for a specific sensor by index.
     * * @param sensorIndex The index of the sensor
     */
    public void checkElectronicsSensor(int sensorIndex){
        checkElectronicsSensor(sensorNames[sensorIndex]);
    }

    /**
     * Retrieves a motor object by its name.
     * * @param motorName The name of the motor
     * @return The MotorInterface object, or null if not found
     */
    public MotorInterface getMotor(String motorName) {
        MotorNode node = motors.get(motorName);
        if (node == null){
            Log.log("Invalid motor: " + motorName);
            return null;
        }
        return node.motor;
    }

    /**
     * Retrieves a motor object by its index.
     * * @param motorIndex The index of the motor
     * @return The MotorInterface object, or null if not found
     */
    public MotorInterface getMotor(int motorIndex) {
        return getMotor(motorNames[motorIndex]);
    }

    /**
     * Retrieves an array of all registered motors.
     * * @return An array of MotorInterface objects
     */
    public MotorInterface[] getMotors() {
        MotorInterface[] motorArray = new MotorInterface[motorsAmount];
        for (int i = 0; i < motorsAmount; i++){
            motorArray[i] = motors.get(motorNames[i]).motor;
        }
        return motorArray;
    }

    /**
     * Retrieves a sensor object by its name.
     * * @param sensorName The name of the sensor
     * @return The SensorInterface object, or null if not found
     */
    public SensorInterface getSensor(String sensorName) {
        SensorInterface sensor = sensors.get(sensorName);
        if (sensor == null){
            Log.log("Invalid sensor: " + sensorName);
            return null;
        }
        return sensor;
    }

    /**
     * Retrieves a sensor object by its index.
     * * @param sensorIndex The index of the sensor
     * @return The SensorInterface object, or null if not found
     */
    public SensorInterface getSensor(int sensorIndex) {
        return getSensor(sensorNames[sensorIndex]);
    }
    
    /**
     * Retrieves an array of all registered sensors.
     * * @return An array of SensorInterface objects
     */
    public SensorInterface[] getSensors() {
        SensorInterface[] sensorArray = new SensorInterface[sensorsAmount];
        for (int i = 0; i < sensorsAmount; i++){
            sensorArray[i] = sensors.get(sensorNames[i]);
        }
        return sensorArray;
    }

    /**
     * Checks if a motor name exists in the map.
     * * @param motorName The name to check
     * @return true if valid, false otherwise
     */
    protected boolean isValidMotor(String motorName) {
        return motors.containsKey(motorName);
    }

    /**
     * Checks if a motor index exists.
     * * @param motorIndex The index to check
     * @return true if valid, false otherwise
     */
    protected boolean isValidMotor(int motorIndex) {
        return isValidMotor(motorNames[motorIndex]);
    }

    /**
     * Checks if a sensor name exists in the map.
     * * @param sensorName The name to check
     * @return true if valid, false otherwise
     */
    protected boolean isValidSensor(String sensorName) {
        return sensors.containsKey(sensorName);
    }

    /**
     * Checks if a sensor index exists in the map.
     * * @param sensorIndex The index to check
     * @return true if valid, false otherwise
     */
    protected boolean isValidSensor(int sensorIndex) {
        return isValidSensor(sensorNames[sensorIndex]);
    }

    @Override
    public void periodic() {
        for (MotorNode node : motors.values()) {
            node.autoCalibration.run();
        }

        for (int i = 0; i < motorsAmount; i++){
            SmartDashboard.putBoolean(getName() + "/" + motorNames[i] + "/" + motorNames[i] + " has Calibrated", getIsCalibration(i));
            SmartDashboard.putNumber(getName() + "/" + motorNames[i] + "/" + motorNames[i] + " wanted value", motors.get(motorNames[i]).motor.getWantedValue());
            SmartDashboard.putNumber(getName() + "/" + motorNames[i] + "/" + motorNames[i] + " current Value", motors.get(motorNames[i]).motor.getCurrentVoltage());
        }
    }
}