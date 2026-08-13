package frc.demacia.utils.log;

import java.util.function.Supplier;

import com.ctre.phoenix6.StatusSignal;

import frc.demacia.utils.Data;

/**
 * Builder class for creating and configuring LogEntries.
 * Allows setting properties like log level, metadata, and consumers before registration.
 * @param <T> The type of data to log
 */
public class LogEntryBuilder<T> {

    /**
     * Enumeration for different logging levels.
     * Defines behavior for file logging and NetworkTables updating, both in and out of competition.
     */
    public static enum LogLevel { 
        /** Log to file only, but remove entirely during competition */
        LOG_ONLY_NOT_IN_COMP, 
        /** Log to file only */
        LOG_ONLY, 
        /** Log to file and NetworkTables only when not in competition */
        LOG_AND_NT_NOT_IN_COMP, 
        /** Log to file and NetworkTables */
        LOG_AND_NT
    } 

    private String name;
    private LogLevel logLevel = LogLevel.LOG_ONLY_NOT_IN_COMP;
    private String metadata = "";
    private boolean isSeparated = false;
    private Data<T> data;
    private boolean isRio = true;
    
    /**
     * Creates a builder for Phoenix6 StatusSignals.
     * @param name The name of the log entry
     * @param statusSignals Variable arguments of StatusSignals
     */
    LogEntryBuilder(String name, StatusSignal<T>[] statusSignals, boolean isRio) {
        this.name = name;
        this.data = new Data<>(statusSignals, isRio);
    }
    
    /**
     * Creates a builder for Suppliers.
     * @param name The name of the log entry
     * @param suppliers Variable arguments of Suppliers
     */
    @SafeVarargs
    LogEntryBuilder(String name, Supplier<T>... suppliers) {
        this.name = name;
        this.data = new Data<>(suppliers);
    }
    
    /**
     * Sets the log level for this entry.
     * @param level The desired LogLevel
     * @return The builder instance
     */
    public LogEntryBuilder<T> withLogLevel(LogLevel level) {
        this.logLevel = level;
        return this;
    }
    
    /**
     * Sets the metadata description for this entry.
     * @param metaData The metadata string
     * @return The builder instance
     */
    public LogEntryBuilder<T> withMetaData(String metaData) {
        this.metadata = metaData;
        return this;
    }
    
    /**
     * Convenience method to set metadata to "motor".
     * @return The builder instance
     */
    public LogEntryBuilder<T> withIsMotor() {
        this.metadata = "motor";
        return this;
    }

    /**
     * Forces the entry to be separated (not grouped with others).
     * @param isSeparated true to keep separate
     * @return The builder instance
     */
    public LogEntryBuilder<T> withIsSeparated(boolean isSeparated) {
        this.isSeparated = isSeparated;
        return this;
    }
    
    /**
     * Builds the LogEntry and registers it with the LogManager.
     * @return The created LogEntry, or null if validation fails
     */
    public LogEntry<T> build() {
        if (name == null || name.trim().isEmpty()) {
            LogManager.log("Log entry name cannot be null or empty");
            return null;
        }
        if (logLevel == null) {
            LogManager.log("Log level cannot be null");
            return null;
        }

        LogEntry<T> entry = LogManager.add(name, data, logLevel, metadata, isSeparated, isRio);
        return entry;
    }
}