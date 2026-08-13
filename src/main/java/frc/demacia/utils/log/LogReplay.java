package frc.demacia.utils.log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.demacia.utils.log.Log.LogLevel;
import frc.demacia.utils.log.LogReader.Entry;
import frc.demacia.utils.log.LogReader.EntryPoint;

public class LogReplay implements Sendable {
    public static LogReplay instance;
    
    private double time = 0.0;
    private static long minTime = 0;
    private static long maxTime = 0;

    public static class ReplayEntry {
        String path;
        List<EntryPoint> points;

        int currentIndex = 0;
        Object currentValue;

        ReplayEntry(String path, List<EntryPoint> points) {
            this.path = path;
            this.points = points;

            if (!points.isEmpty()) {
                currentValue = points.get(0).value;
            }
        }

        public Object getValue() {
            return currentValue;
        }

        public double getDoubleValue() {
            if (currentValue instanceof Number) {
                return ((Number) currentValue).doubleValue();
            }
            return 0.0;
        }

        public boolean getBooleanValue() {
            if (currentValue instanceof Boolean) {
                return (Boolean) currentValue;
            }
            return false;
        }

        public String getStringValue() {
            return currentValue != null ? currentValue.toString() : "";
        }

        public void update(long target) {
            if (points.isEmpty()) return;

            if (target <= points.get(0).time) {
                currentIndex = 0;
                currentValue = points.get(0).value;
                return;
            }

            if (target >= points.get(points.size() - 1).time) {
                currentIndex = points.size() - 1;
                currentValue = points.get(currentIndex).value;
                return;
            }

            if (points.get(currentIndex).time <= target && 
               (currentIndex == points.size() - 1 || points.get(currentIndex + 1).time > target)) {
                currentValue = points.get(currentIndex).value;
                return;
            }

            int low = 0;
            int high = points.size() - 1;
            int resultIndex = 0;

            while (low <= high) {
                int mid = (low + high) / 2;
                long midVal = points.get(mid).time;

                if (midVal <= target) {
                    resultIndex = mid;
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }

            currentIndex = resultIndex;
            currentValue = points.get(currentIndex).value;
        }
    }

    public static List<ReplayEntry> replayEntries = new ArrayList<>();

    public static void init() {
        if (instance == null) {
            instance = new LogReplay();
            SmartDashboard.putData("replay", instance);
        }
    }

    public static LogReplay getInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public static void loadLogs() {
        replayEntries.clear();

        try {
            Log.log("Reading log file...");
            List<Entry> logEntries = LogReader.getEntries(true, null);

            minTime = Long.MAX_VALUE;
            maxTime = Long.MIN_VALUE;

            for (LogReader.Entry entry : logEntries) {
                if (entry.data.isEmpty()) {
                    continue;
                }

                List<LogReader.EntryPoint> dataPoints = new ArrayList<>(entry.data);

                long entryStart = dataPoints.get(0).time;
                long entryEnd = dataPoints.get(dataPoints.size() - 1).time;

                if (entryStart < minTime) minTime = entryStart;
                if (entryEnd > maxTime) maxTime = entryEnd;

                String replayPath = "replay/" + (entry.groupName.isEmpty() ? "" : entry.groupName + "/") + entry.name;

                ReplayEntry replay = new ReplayEntry(replayPath, dataPoints);
                replayEntries.add(replay);

                Object firstVal = dataPoints.get(0).value;

                if (firstVal instanceof Boolean) {
                    Log.putData(replay.path, new Supplier[] {replay::getBooleanValue}, LogLevel.LOG_AND_NT, "replay", true);
                } else if (firstVal instanceof Number) {
                    Log.putData(replay.path, new Supplier[] {replay::getDoubleValue}, LogLevel.LOG_AND_NT, "replay", true);
                } else {
                    Log.putData(replay.path, new Supplier[] {replay::getStringValue}, LogLevel.LOG_AND_NT, "replay", true);
                }
            }

            if (minTime > maxTime) {
                minTime = 0;
                maxTime = 0;
            }

            setReplayTime(maxTime / 1000000.0);

            Log.log("Log loaded successfully!");
        } catch (Exception e) {
            System.err.println("Error processing logs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setReplayTime(double time) {
        if (instance == null) return;

        long target = (long) (time * 1000000.0);
        target = Math.max(minTime, Math.min(target, maxTime));
        
        instance.time = target / 1000000.0;

        for (ReplayEntry replay : replayEntries) {
            replay.update(target);
        }
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("time", () -> time, LogReplay::setReplayTime);
    }
}