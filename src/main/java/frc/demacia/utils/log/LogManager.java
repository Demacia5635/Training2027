// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.utils.log;

import java.util.ArrayList;
import java.util.function.Supplier;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.DataLogManager;
import com.ctre.phoenix6.StatusSignal;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.Data;
import frc.demacia.utils.log.LogEntryBuilder.LogLevel;

/**
 * Centralized logging system for robot telemetry and diagnostics.
 * <p>
 * Manages the creation, updating, and optimization of log entries.
 * Handles both file logging (DataLog) and live dashboard updates
 * (NetworkTables).
 * </p>
 */
public class LogManager extends SubsystemBase {

  /** Singleton instance of the LogManager */
  private static LogManager logManager;

  /** The main DataLog instance for file writing */
  public static DataLog log;
  /** The NetworkTable instance for the "Log" table */
  public static NetworkTable table = NetworkTableInstance.getDefault().getTable("Log");

  /** List of currently active console alerts */
  private static ArrayList<ConsoleAlert> activeConsole;

  /** List of individual log entries that are not grouped */
  private ArrayList<LogEntry<?>> individualLogEntries = new ArrayList<>();

  /**
   * * Array of grouped log entries.
   * Used to optimize logging by combining similar data types and log levels into
   * single array entries.
   */
  private LogEntry<?>[] categoryLogEntries = new LogEntry<?>[24];

  /**
   * Private constructor to enforce Singleton pattern.
   * Initializes DataLogManager and starts logging.
   */
  private LogManager() {
    if (logManager != null) {
      CommandScheduler.getInstance().unregisterSubsystem(this);
      return;
    }
    logManager = this;
    DataLogManager.start();
    DataLogManager.logNetworkTables(false);
    log = DataLogManager.getLog();
    DriverStation.startDataLog(log);

    activeConsole = new ArrayList<>();
    log("log manager is ready");
  }

  /**
   * Static initializer to ensure the LogManager is created.
   */
  static {
    if (logManager == null) {
      new LogManager();
    }
  }

  /**
   * Starts building a new log entry from Phoenix6 StatusSignals.
   * 
   * @param <T>           The type of data
   * @param name          The name of the log entry
   * @param statusSignals The signals to log
   * @return A new LogEntryBuilder
   */
  public static <T> LogEntryBuilder<T> addEntry(String name, StatusSignal<T>[] statusSignals, boolean isRio) {
    return new LogEntryBuilder<T>(name, statusSignals, isRio);
  }

  /**
   * Starts building a new log entry from standard Suppliers.
   * 
   * @param <T>       The type of data
   * @param name      The name of the log entry
   * @param suppliers The suppliers to log
   * @return A new LogEntryBuilder
   */
  @SuppressWarnings("unchecked")
  public static <T> LogEntryBuilder<T> addEntry(String name, Supplier<T>... suppliers) {
    return new LogEntryBuilder<T>(name, suppliers);
  }

  /**
   * Removes non-essential log entries when in competition mode.
   * Cleans up both individual and categorized entries based on their LogLevel.
   */
  public static void removeInComp() {
    if (logManager == null)
      return;

    for (int i = 0; i < logManager.individualLogEntries.size(); i++) {
      LogEntry<?> entry = logManager.individualLogEntries.get(i);
      entry.removeInComp();
      if (entry.getLogLevel() == LogLevel.LOG_ONLY_NOT_IN_COMP
          || logManager.individualLogEntries.get(i).getLogLevel() == LogLevel.LOG_AND_NT_NOT_IN_COMP) {
        logManager.individualLogEntries.remove(i);
        i--;
      }
    }

    for (int i = 0; i < logManager.categoryLogEntries.length; i++) {
      if (logManager.categoryLogEntries[i] != null) {
        logManager.categoryLogEntries[i].removeInComp();
        if (logManager.categoryLogEntries[i].getLogLevel() == LogLevel.LOG_ONLY_NOT_IN_COMP
            || logManager.categoryLogEntries[i].getLogLevel() == LogLevel.LOG_AND_NT_NOT_IN_COMP) {
          logManager.categoryLogEntries[i] = null;
        }
      }
    }
  }

  /**
   * Clears all log entries from the manager.
   */
  public static void clearEntries() {
    if (logManager != null) {
      logManager.individualLogEntries.clear();
      for (int i = 0; i < logManager.categoryLogEntries.length; i++) {
        logManager.categoryLogEntries[i] = null;
      }
    }
  }

  /**
   * Logs a message to the console and creates an alert.
   * Manages the console limit by removing old alerts.
   * 
   * @param message   The message to log
   * @param alertType The severity of the alert
   * @return The created ConsoleAlert
   */
  public static ConsoleAlert log(Object message, AlertType alertType) {
    DataLogManager.log(String.valueOf(message));

    ConsoleAlert alert = new ConsoleAlert(String.valueOf(message), alertType);
    alert.set(true);
    if (activeConsole.size() > ConsoleConstants.CONSOLE_LIMIT) {
      activeConsole.get(0).close();
      activeConsole.remove(0);
    }
    activeConsole.add(alert);
    return alert;
  }

  /**
   * Logs an info message to the console.
   * 
   * @param message The message to log
   * @return The created ConsoleAlert
   */
  public static ConsoleAlert log(Object message) {
    return log(message, AlertType.kInfo);
  }

  /**
   * Periodic method called by the scheduler.
   * Refreshes data, updates console alerts (handling expiration), and updates all
   * log entries.
   */
  @Override
  public void periodic() {
    Data.refreshAll();

    for (int i = activeConsole.size() - 1; i >= 0; i--) {
      ConsoleAlert alert = activeConsole.get(i);
      if (alert.isTimerOver()) {
        alert.set(false);
        activeConsole.remove(i);
      }
    }

    for (int i = 0; i < individualLogEntries.size(); i++) {
      individualLogEntries.get(i).log();
    }

    for (LogEntry<?> e : categoryLogEntries) {
      if (e != null) {
        e.log();
      }
    }
  }

  /**
   * Internal method to add a log entry to the manager.
   * 
   * @param <T>         The data type
   * @param name        Name of the entry
   * @param data        Data wrapper
   * @param logLevel    Logging level
   * @param metaData    Metadata
   * @param isSeparated Whether to force a separate entry
   * @return The created or updated LogEntry
   */
  public static <T> LogEntry<T> add(String name, Data<T> data, LogLevel logLevel, String metaData, boolean isSeparated,
      boolean isRio) {
    LogEntry<T> entry = null;

    int categoryIndex = logManager.getCategoryIndex(data, logLevel, isSeparated);

    if (categoryIndex == -1) {
      entry = new LogEntry<T>(name, data, logLevel, metaData);
      logManager.individualLogEntries.add(entry);
    } else {
      entry = logManager.addToEntryArray(categoryIndex, name, logLevel, data, metaData, isRio);
    }

    return entry;
  }

  public static <T> LogEntry<T> add(String name, Data<T> data, LogLevel logLevel, String metaData,
      boolean isSeparated) {
    return add(name, data, logLevel, metaData, isSeparated, true);
  }

  /**
   * Adds data to an existing category entry or creates a new one if it doesn't
   * exist.
   * Handles type mismatches gracefully by creating a separate entry.
   * 
   * @param i        The index in the category array
   * @param name     Name of the entry
   * @param logLevel Logging level
   * @param data     Data wrapper
   * @param metaData Metadata
   * @return The LogEntry
   */
  @SuppressWarnings("unchecked")
  private <T> LogEntry<T> addToEntryArray(int i, String name, LogLevel logLevel, Data<T> data, String metaData,
      boolean isRio) {

    if (categoryLogEntries[i] != null && categoryLogEntries[i].getData() != null) {
      if ((categoryLogEntries[i].getData().getSignalArray() != null) != (data.getSignalArray() != null)) {
        LogManager.log("Log Type Mismatch in '" + name + "'. Creating separate entry.", AlertType.kWarning);
        return add(name, data, LogLevel.LOG_ONLY, metaData, true, isRio);
      }
    }

    if (categoryLogEntries[i] == null) {
      categoryLogEntries[i] = new LogEntry<>(name, data, logLevel, metaData);
    } else {
      try {

        ((LogEntry<T>) categoryLogEntries[i]).addData(name, data, metaData, isRio);
      } catch (Exception e) {
        LogManager.log("Error combining log entries: " + e.getMessage(), AlertType.kError);
      }
    }

    return (LogEntry<T>) categoryLogEntries[i];
  }

  public <T> LogEntry<T> addToEntryArray(int i, String name, LogLevel logLevel, Data<T> data, String metaData) {
    return addToEntryArray(i, name, logLevel, data, metaData, true);
  }

  /**
   * Calculates the index for the category array based on data type and log level.
   * 
   * @param data        The data object
   * @param logLevel    The log level
   * @param isSeperated Whether the entry is forced to be separate
   * @return The index, or -1 if it should be an individual entry
   */
  private int getCategoryIndex(Data<?> data, LogLevel logLevel, Boolean isSeperated) {
    boolean isSignal = data.getSignalArray() != null;
    boolean isSupplier = data.getSupplierArray() != null;
    boolean isDouble = data.isDouble();
    boolean isBoolean = data.isBoolean();

    if (!(isSignal || isSupplier) || isSeperated) {
      return -1;
    }

    int baseIndex = (isSignal ? 0 : 3) + (isDouble ? 0 : isBoolean ? 1 : 2);
    int levelOffset;
    switch (logLevel) {
      case LOG_ONLY_NOT_IN_COMP:
        levelOffset = 0;
        break;
      case LOG_ONLY:
        levelOffset = 6;
        break;
      case LOG_AND_NT_NOT_IN_COMP:
        levelOffset = 12;
        break;
      default:
        levelOffset = 18;
        break;
    }

    return baseIndex + levelOffset;
  }
}