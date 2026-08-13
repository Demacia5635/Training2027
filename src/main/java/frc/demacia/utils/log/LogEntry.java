// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.utils.log;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import edu.wpi.first.networktables.BooleanArrayPublisher;
import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.FloatArrayPublisher;
import edu.wpi.first.networktables.FloatPublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.Publisher;
import edu.wpi.first.networktables.StringArrayPublisher;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.util.datalog.BooleanArrayLogEntry;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DataLogEntry;
import edu.wpi.first.util.datalog.FloatArrayLogEntry;
import edu.wpi.first.util.datalog.FloatLogEntry;
import edu.wpi.first.util.datalog.StringArrayLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import frc.demacia.utils.Data;
import frc.demacia.utils.RobotCommon;
import frc.demacia.utils.log.Log.LogLevel;

/**
 * Represents a single log entry of a specific type (T).
 * <p>
 * This class handles the logic of writing data to both the local DataLog (file)
 * and NetworkTables (live dashboard)
 * </p>
 * @param <T> The type of data contained in this entry
 */
public class LogEntry<T> {
    /** The actual WPILib DataLog entry for file logging */
    private DataLogEntry entry;
    /** The data wrapper containing the value and type information */
    private Data<T> data;

    /** The name/key of the log entry */
    private String name;
    /** Metadata description for the log */
    private String metaData;
    /** The NetworkTables publisher for live dashboard updates */
    private Publisher ntPublisher;
    /** The logging level configuration (Log only, NT only, or both) */
    private LogLevel logLevel;

    /** Strategy for writing to the DataLog file based on type */
    private BiConsumer<Long, Data<T>> logStrategy;
    /** Strategy for updating NetworkTables based on type */
    private BiConsumer<Data<T>, Publisher> ntStrategy;

    private Supplier<T> supplier;
    private boolean isDouble;
    private boolean isBoolean;
    
    /**
     * Constructs a new LogEntry.
     * @param name The name of the entry
     * @param supplier The supplier object wrapper
     * @param logLevel The desired log level
     * @param metaData Additional metadata for the log file
     */
    LogEntry(String name, Supplier<T> supplier, LogLevel logLevel, String metaData, boolean isDouble, boolean isBoolean) {
        this.name = name;
        this.logLevel = logLevel;
        this.metaData = metaData;
        this.supplier = supplier;
        this.isDouble = isDouble;
        this.isBoolean = isBoolean;
    }

    public void reInitialize(String name, Supplier<T> supplier, LogLevel logLevel, String metaData, boolean isDouble, boolean isBoolean){
        this.name = name;
        this.logLevel = logLevel;
        this.metaData = metaData;
        this.supplier = supplier;
        this.isDouble = isDouble;
        this.isBoolean = isBoolean;
    }
    
    /**
     * Constructs a new LogEntry.
     * @param name The name of the entry
     * @param data The data object wrapper
     * @param logLevel The desired log level
     * @param metaData Additional metadata for the log file
     */
    LogEntry(String name, Data<T> data, LogLevel logLevel, String metaData) {
        this.name = name;
        this.logLevel = logLevel;
        this.data = data;
        this.metaData = metaData;

        initializeLogging();
    }

    /**
     * Initializes or re-initializes the logging strategies and publishers.
     * Closes existing publishers if they exist before creating new ones.
     * Determines if NT publishing is allowed based on competition status.
     */
    private void initializeLogging() {
        createLogEntry(Log.log, name, metaData);

        // Check if we should publish to NetworkTables based on LogLevel and Competition state
        if (logLevel == LogLevel.LOG_AND_NT || (logLevel == LogLevel.LOG_AND_NT_NOT_IN_COMP && !RobotCommon.getIsComp())) {
            createPublisher(Log.table, name);
        } else {
            ntPublisher = null;
            ntStrategy = null;
        }

        long time = data != null ? data.getTime() : 0;

        if (logStrategy != null) {
            logStrategy.accept(time, data);
        }

        // Initial update to NT if applicable
        if (ntPublisher != null && ntStrategy != null) {
            ntStrategy.accept(data, ntPublisher);
        }
    }

    

    /**
     * Initializes or re-initializes the logging strategies and publishers.
     * Closes existing publishers if they exist before creating new ones.
     * Determines if NT publishing is allowed based on competition status.
     */
    private void initializeLoggingWithSupllier() {
        if (isDouble) {
            entry = new FloatArrayLogEntry(Log.log, name, metaData);
            logStrategy = (time, d) -> ((FloatArrayLogEntry) entry).append((float[]) supplier.get(), time);
        } else if (isBoolean) {
            entry = new BooleanArrayLogEntry(Log.log, name, metaData);
            logStrategy = (time, d) -> ((BooleanArrayLogEntry) entry).append((boolean[]) supplier.get(), time);
        } else {
            entry = new StringArrayLogEntry(Log.log, name, metaData);
            logStrategy = (time, d) -> ((StringArrayLogEntry) entry).append((String[]) supplier.get(), time);
        }

        if (logLevel == LogLevel.LOG_AND_NT || (logLevel == LogLevel.LOG_AND_NT_NOT_IN_COMP && !RobotCommon.getIsComp())) {
            if (isDouble) {
                ntPublisher = Log.table.getFloatArrayTopic(name).publish();
                ntStrategy = (d, p) -> ((FloatArrayPublisher) p).set((float[]) supplier.get());
            } else if (isBoolean) {
                ntPublisher = Log.table.getBooleanArrayTopic(name).publish();
                ntStrategy = (d, p) -> ((BooleanArrayPublisher) p).set((boolean[]) supplier.get());
            } else {
                ntPublisher = Log.table.getStringArrayTopic(name).publish();
                ntStrategy = (d, p) -> ((StringArrayPublisher) p).set((String[]) supplier.get());
            }
        } else {
            ntPublisher = null;
            ntStrategy = null;
        }
    }

    /**
     * updates the log.
     * <p>
     * Checks if the data has changed. If so, updates the DataLog,
     * the NetworkTable, and triggers the consumer.
     * </p>
     */
    void log() {
        if (data != null && !data.hasChanged()) {
            return;
        }

        if (data == null && !Log.table.getFloatArrayTopic(name).exists()) {
            initializeLoggingWithSupllier();
            if (ntPublisher != null && ntStrategy != null ) {
                ntStrategy.accept(null, ntPublisher);
            }
        }

        long time = data != null ? data.getTime() : 0;

        // Write to file log
        if (logStrategy != null) {
            logStrategy.accept(time, data);
        }

        // Write to NetworkTables
        if (ntPublisher != null && ntStrategy != null) {
            ntStrategy.accept(data, ntPublisher);
        }
    }

    /**
     * @return The name of the entry
     */
    public String getName(){
        return name;
    }

    /**
     * @return The data wrapper object
     */
    public Data<T> getData(){
        return data;
    }

    /**
     * @return The metadata string
     */
    public String getMetaData(){
        return metaData;
    }

    /**
     * @return The configured log level
     */
    public LogLevel getLogLevel(){
        return logLevel;
    }

    /**
     * Removes the NetworkTables publisher if the log level is set to 
     * disable NT during competition.
     */
    public void removeInComp() {
        if (logLevel == LogLevel.LOG_AND_NT_NOT_IN_COMP && ntPublisher != null) {
            ntPublisher.close();
        }
    }

    /**
     * Creates the specific DataLog entry and strategy based on the data type.
     * Supports Float, Boolean, String and their Array variants.
     * @param log The DataLog instance
     * @param name The name of the entry
     * @param metaData Metadata for the entry
     */
    private void createLogEntry(DataLog log, String name, String metaData) {
        boolean isFloat = data.isDouble();
        boolean isBoolean = data.isBoolean();

        if (isFloat){
            entry = new FloatLogEntry(log, name, metaData);
            logStrategy = (time, d) -> ((FloatLogEntry) entry).append(d.getFloat(), time);
        } else if (isBoolean){
            entry = new BooleanLogEntry(log, name, metaData);
            logStrategy = (time, d) -> ((BooleanLogEntry) entry).append(d.getBoolean(), time);
        } else{
            entry = new StringLogEntry(log, name, metaData);
            logStrategy = (time, d) -> ((StringLogEntry) entry).append(d.getString(), time);
        }
    }

    /**
     * Creates the specific NetworkTable publisher and strategy based on the data type.
     * Supports Float, Boolean, String and their Array variants.
     * @param table The NetworkTable instance
     * @param name The name of the topic
     */
    private void createPublisher(NetworkTable table, String name) {
        boolean isFloat = data.isDouble();
        boolean isBoolean = data.isBoolean();

        if (isFloat){
            ntPublisher = table.getFloatTopic(name).publish();
            ntStrategy = (d, p) -> ((FloatPublisher) p).set(d.getFloat());
        } else if (isBoolean){
            ntPublisher = table.getBooleanTopic(name).publish();
            ntStrategy = (d, p) -> ((BooleanPublisher) p).set(d.getBoolean());
        } else{
            ntPublisher = table.getStringTopic(name).publish();
            ntStrategy = (d, p) -> ((StringPublisher) p).set(d.getString());
        }
    }
}