package frc.demacia.sysid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ejml.simple.SimpleMatrix;
import frc.demacia.utils.log.LogReader;

public class Sysid {
    private static final double VOLTAGE_THRESHOLD = 0.5;
    private static final int SMOOTH_WINDOW = 3;
    private static final double OUTLIER_PERCENTAGE = 0.15;

    public static Map<String, BucketResult> getResult(Map<String, List<LogReader.Entry>> groupedEntries) {
        return performAnalysis(groupedEntries);
    }

    private static Map<String, BucketResult> performAnalysis(Map<String, List<LogReader.Entry>> groupedEntries) {
        Map<String, BucketResult> results = new HashMap<>();

        for (Map.Entry<String, List<LogReader.Entry>> group : groupedEntries.entrySet()) {
            System.out.println("Analyzing group: " + group.getKey());
            BucketResult result = analyzeGroup(group.getKey(), group.getValue());
            if (result != null) {
                results.put(group.getKey(), result);
            }
        }
        System.out.println("Analysis complete. Results for " + results.size() + " groups.");
        return results;
    }

    private static BucketResult analyzeGroup(String name, List<LogReader.Entry> groupEntries) {
        List<SyncedDataPoint> syncedData = new ArrayList<>();

        if (groupEntries == null || groupEntries.isEmpty()) return null;

        LogReader.Entry posEntry = null;
        LogReader.Entry velEntry = null;
        LogReader.Entry accelEntry = null;
        LogReader.Entry voltEntry = null;

        for (LogReader.Entry entry : groupEntries) {
            String entryName = entry.name.toLowerCase();
            if (entryName.contains("pos")) {
                if (posEntry == null) posEntry = entry;
            } else if (entryName.contains("vel")) {
                if (velEntry == null) velEntry = entry;
            } else if (entryName.contains("accel")) {
                if (accelEntry == null) accelEntry = entry;
            } else if (entryName.contains("volt")) {
                if (voltEntry == null) voltEntry = entry;
            }
        }

        if (posEntry == null && groupEntries.size() > 0) posEntry = groupEntries.get(0);
        if (velEntry == null && groupEntries.size() > 1) velEntry = groupEntries.get(1);
        if (accelEntry == null && groupEntries.size() > 2) accelEntry = groupEntries.get(2);
        if (voltEntry == null && groupEntries.size() > 3) voltEntry = groupEntries.get(3);

        if (posEntry != null && velEntry != null && accelEntry != null && voltEntry != null) {
            int minSize = Math.min(Math.min(posEntry.data.size(), velEntry.data.size()),
                                   Math.min(accelEntry.data.size(), voltEntry.data.size()));

            for (int i = 0; i < minSize; i++) {
                Object posVal = posEntry.data.get(i).value;
                Object velVal = velEntry.data.get(i).value;
                Object accelVal = accelEntry.data.get(i).value;
                Object voltVal = voltEntry.data.get(i).value;

                if (posVal instanceof Number && velVal instanceof Number && 
                    accelVal instanceof Number && voltVal instanceof Number) {
                    
                    double pos = ((Number) posVal).doubleValue();
                    double vel = ((Number) velVal).doubleValue();
                    double accel = ((Number) accelVal).doubleValue();
                    double volt = ((Number) voltVal).doubleValue();
                    long time = posEntry.data.get(i).time;

                    syncedData.add(new SyncedDataPoint(vel, pos, accel, volt, time));
                }
            }
        }

        if (syncedData.isEmpty()) {
            for (LogReader.Entry entry : groupEntries) {
                for (LogReader.EntryPoint dp : entry.data) {
                    if (dp.value instanceof double[]) {
                        double[] vals = (double[]) dp.value;
                        if (vals.length >= 4) {
                            syncedData.add(new SyncedDataPoint(vals[1], vals[0], vals[2], vals[3], dp.time));
                        }
                    } else if (dp.value instanceof float[]) {
                        float[] vals = (float[]) dp.value;
                        if (vals.length >= 4) {
                            syncedData.add(new SyncedDataPoint(vals[1], vals[0], vals[2], vals[3], dp.time));
                        }
                    }
                }
            }
        }

        System.out.println("Group: " + name + " | Total data points: " + syncedData.size());
        if (syncedData.isEmpty()) return null;

        syncedData.sort((p1, p2) -> Long.compare(p1.timestamp, p2.timestamp));
        
        return calculateResult(syncedData, name);
    }

    public static class SyncedDataPoint {
        double velocity, position, acceleration, rawAcceleration, voltage;
        long timestamp;
        double error;
        
        SyncedDataPoint(double velocity, double position, double acceleration, double voltage, long timestamp) {
            this.velocity = velocity;
            this.position = position;
            this.acceleration = acceleration;
            this.rawAcceleration = acceleration;
            this.voltage = voltage;
            this.timestamp = timestamp;
        }
    }

    private static BucketResult calculateResult(List<SyncedDataPoint> rawData, String name) {
        List<SyncedDataPoint> cleanData = filterAndSmooth(rawData, VOLTAGE_THRESHOLD, SMOOTH_WINDOW);
        if (cleanData.size() < 10) return null;

        BucketResult initialResult = solveOLS(cleanData);
        if (initialResult == null) return null;

        List<SyncedDataPoint> refinedData = removeOutliers(cleanData, initialResult, OUTLIER_PERCENTAGE);
        if (refinedData.size() < 10) return null;

        BucketResult finalModel = solveOLS(refinedData);
        
        if (finalModel != null) {
            double sumErr = 0;
            double maxErr = 0;
            boolean[] flags = SysidApp.kFlags;

            for(SyncedDataPoint p : rawData) {
                double pred = 0;
                if(flags[0]) pred += finalModel.ks * Math.signum(p.velocity);
                if(flags[1]) pred += finalModel.kv * p.velocity;
                if(flags[2]) pred += finalModel.ka * p.acceleration;
                if(flags[3]) pred += finalModel.kg * 1.0;
                if(flags[4]) pred += finalModel.ksin * Math.cos(p.position);
                if(flags[5]) pred += finalModel.kv2 * p.velocity * Math.abs(p.velocity);
                
                double error = Math.abs(p.voltage - pred);
                sumErr += error;
                if(error > maxErr) maxErr = error;
            }

            finalModel.avgError = sumErr / rawData.size();
            finalModel.maxError = maxErr;
            finalModel.rawPoints = rawData.size();
        }

        return finalModel;
    }

    private static List<SyncedDataPoint> filterAndSmooth(List<SyncedDataPoint> rawData, double voltageThresh, int windowSize) {
        List<SyncedDataPoint> filtered = new ArrayList<>();
        for (int i = 0; i < rawData.size(); i++) {
            SyncedDataPoint current = rawData.get(i);
            
            double sumAccel = 0;
            int count = 0;
            for (int j = Math.max(0, i - windowSize/2); j < Math.min(rawData.size(), i + windowSize/2 + 1); j++) {
                sumAccel += rawData.get(j).rawAcceleration;
                count++;
            }
            current.acceleration = sumAccel / count;
            
            if (Math.abs(current.voltage) > voltageThresh) {
                filtered.add(current);
            }
        }
        return filtered;
    }

    private static List<SyncedDataPoint> removeOutliers(List<SyncedDataPoint> data, BucketResult model, double percentage) {
        if (percentage <= 0.001) return data;

        double kS = model.ks;
        double kV = model.kv;
        double kA = model.ka;
        double kG = model.kg;
        double kCos = model.ksin;
        double kV2 = model.kv2;
        boolean[] flags = SysidApp.kFlags;

        for (SyncedDataPoint p : data) {
            double pred = 0;
            if(flags[0]) pred += kS * Math.signum(p.velocity);
            if(flags[1]) pred += kV * p.velocity;
            if(flags[2]) pred += kA * p.acceleration;
            if(flags[3]) pred += kG * 1.0;
            if(flags[4]) pred += kCos * Math.cos(p.position);
            if(flags[5]) pred += kV2 * p.velocity * Math.abs(p.velocity);
            
            p.error = Math.abs(p.voltage - pred);
        }

        Collections.sort(data, (p1, p2) -> Double.compare(p1.error, p2.error));

        int removeCount = (int)(data.size() * percentage);
        int keepCount = data.size() - removeCount;
        
        if (keepCount < 1) return new ArrayList<>();
        return new ArrayList<>(data.subList(0, keepCount));
    }

    private static BucketResult solveOLS(List<SyncedDataPoint> data) {
        int n = data.size();
        boolean[] flags = SysidApp.kFlags;
        int numParams = 0;
        for(boolean f : flags) if(f) numParams++;

        if(numParams == 0) return null;

        SimpleMatrix A = new SimpleMatrix(n, numParams);
        SimpleMatrix b = new SimpleMatrix(n, 1);

        for (int i = 0; i < n; i++) {
            SyncedDataPoint p = data.get(i);
            b.set(i, 0, p.voltage);

            int col = 0;
            if(flags[0]) A.set(i, col++, Math.signum(p.velocity));
            if(flags[1]) A.set(i, col++, p.velocity);
            if(flags[2]) A.set(i, col++, p.acceleration);
            if(flags[3]) A.set(i, col++, 1.0);
            if(flags[4]) A.set(i, col++, Math.cos(p.position));
            if(flags[5]) A.set(i, col++, p.velocity * Math.abs(p.velocity));
        }

        SimpleMatrix x;
        try {
            x = A.solve(b);
        } catch(Exception e) {
            return null;
        }

        double[] k = new double[6];
        int col = 0;
        for(int i=0; i<6; i++) {
            if(flags[i]) k[i] = x.get(col++);
        }

        double ssTot = 0, ssRes = 0, meanV = 0;
        for(SyncedDataPoint p : data) meanV += p.voltage;
        meanV /= n;

        for (SyncedDataPoint p : data) {
            double pred = 0;
            if(flags[0]) pred += k[0] * Math.signum(p.velocity);
            if(flags[1]) pred += k[1] * p.velocity;
            if(flags[2]) pred += k[2] * p.acceleration;
            if(flags[3]) pred += k[3] * 1.0;
            if(flags[4]) pred += k[4] * Math.cos(p.position);
            if(flags[5]) pred += k[5] * p.velocity * Math.abs(p.velocity);

            ssTot += Math.pow(p.voltage - meanV, 2);
            ssRes += Math.pow(p.voltage - pred, 2);
        }

        double r2 = 1 - (ssRes / ssTot);

        return new BucketResult(k[0], k[1], k[5], k[2], k[3], k[4], 0, 0, n, r2);
    }

    public static class BucketResult {
        double ks, kv, kv2, ka, kg, ksin, avgError, maxError, rSquared;
        int points, rawPoints;

        BucketResult(double ks, double kv, double kv2, double ka, double kg, double ksin, double avgError, double maxError, int points, double rSquared) {
            this.ks = ks;
            this.kv = kv;
            this.kv2 = kv2;
            this.ka = ka;
            this.kg = kg;
            this.ksin = ksin;
            this.avgError = avgError;
            this.maxError = maxError;
            this.points = points;
            this.rSquared = rSquared;
        }
    }

    public class Entry {
    }
}