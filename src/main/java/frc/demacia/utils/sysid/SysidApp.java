package frc.demacia.utils.sysid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.ejml.simple.SimpleMatrix;

import edu.wpi.first.math.MathUtil;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.log.LogReader.Entry;
import frc.demacia.utils.log.LogReader.EntryPoint;
import frc.demacia.utils.motors.CloseLoopParam;
import frc.demacia.utils.motors.MotorInterface;

public class SysidApp {
    private static final List<MotorInterface> motors = new ArrayList<>();

    private static final double[] VOLTAGE_THRESHOLDS = {0.1, 0.2, 0.3, 0.4};
    private static final int[] SMOOTH_WINDOWS = {1, 2, 3, 4, 5};
    private static final double[] Z_SCORE_THRESHOLDS = {1.5, 2.0, 2.5, 3.0};

    private static final double MAX_VOLT = 12;
    private static final double MIN_TIME_TO_MAX_VEL = 0.2;
    private static final double MAX_TIME_TO_MAX_VEL = 1;
    private static final double MAX_VEL_TIME_SCALAR = 1.2;
    private static final double TIME_TO_MAX_ACCEL = 0.1;

    private KFlags kFlags;

    private String name;
    private List<Entry> motorEntries;
    private List<SyncedDataPoint> rawData;
    private BucketResult result;

    private CloseLoopParam param;
    private double kP;
    private double maxVelocity;
    private double maxAcceleration;
    private double maxJerk;

    private boolean isCos;

    private class SyncedDataPoint{
        double velocity, position, acceleration, voltage;
        long timestamp;
        double error;
        
        SyncedDataPoint(double velocity, double position, double acceleration, double voltage, long timestamp) {
            this.velocity = velocity;
            this.position = position;
            this.acceleration = acceleration;
            this.voltage = voltage;
            this.timestamp = timestamp;
        }

        SyncedDataPoint copy() {
            SyncedDataPoint c = new SyncedDataPoint(velocity, position, acceleration, voltage, timestamp);
            c.error = error;
            return c;
        }
    }

    private class BucketResult {
        double kS, kV, kA, kG, kCos, kV2, avgError, maxError, rSquared;
        int points, rawPoints;

        BucketResult(double kS, double kV, double kA, double kG, double kCos, double kV2, double avgError, double maxError, int points, double rSquared) {
            this.kS = kS;
            this.kV = kV;
            this.kA = kA;
            this.kG = kG;
            this.kCos = kCos;
            this.kV2 = kV2;
            this.avgError = avgError;
            this.maxError = maxError;
            this.points = points;
            this.rSquared = rSquared;
        }
    }

    private class KFlags implements Iterable<Boolean>{
        boolean useKS, useKV, useKA, useKG, useKCos, useKV2;
        
        KFlags(boolean useKS, boolean useKV, boolean useKA, boolean useKG, boolean useKCos, boolean useKV2) {
            this.useKS = useKS;
            this.useKV = useKV;
            this.useKA = useKA;
            this.useKG = useKG;
            this.useKCos = useKCos;
            this.useKV2 = useKV2;
        }

        @Override
        public Iterator<Boolean> iterator() {
            return new Iterator<Boolean>() {
                private int currentIndex = 0;

                @Override
                public boolean hasNext() {
                    return currentIndex < 6;
                }

                @Override
                public Boolean next() {
                    switch (currentIndex++) {
                        case 0: return useKS;
                        case 1: return useKV;
                        case 2: return useKA;
                        case 3: return useKG;
                        case 4: return useKCos;
                        case 5: return useKV2;
                        default: throw new NoSuchElementException("No more flags available.");
                    }
                }
            };
        }
    }

    private static class KFunctions {
        public static double sFunction(double vel) {
            return Math.signum(vel);
        }
        
        public static double vFunction(double vel) {
            return vel;
        }
        
        public static double aFunction(double accel) {
            return accel;
        }
        
        public static double gFunction() {
            return 1;
        }
        
        public static double cosFunction(double pos, boolean isCos) {
            return isCos ? Math.cos(pos) : Math.sin(pos);
        }
        
        public static double v2Function(double vel) {
            return vel * Math.abs(vel);
        }
    }

    public static void registerMotor(MotorInterface motor) {
        if (!motors.contains(motor)) {
            motors.add(motor);
        }
    }
    
    public static List<MotorInterface> getMotors() {
        return motors;
    }

    public SysidApp(String name, List<Entry> motorEntries, boolean[] kFlags) {
        Log.log("Performing analysis...");

        this.name = name;
        this.motorEntries = motorEntries;
        if (kFlags.length == 6){
            this.kFlags = new KFlags(kFlags[0], kFlags[1], kFlags[2], kFlags[3], kFlags[4], kFlags[5]);
        } else {
            this.kFlags = new KFlags(true, true, true, false, false, false);
            Log.log("kFlags shuold have 6 flags for kS, kV, kA, kG, kCos, kV2");
        }
        rawData = new ArrayList<>();
        isCos = true;

        analyzeGroup();

        param = new CloseLoopParam();

        if (result == null) {
            param = null;
            return;
        }

        if (result.kA > 0){
            double kASafe = Math.max(result.kA, 0.01);
        
            kP = result.kV / kASafe;
        }
        
        param.set(
            kP,
            0.0,
            0.0,
            result.kS,
            result.kV,
            result.kA,
            result.kG,
            result.kCos,
            result.kV2
        );

        if (result.kV != 0) {
            maxVelocity = (MAX_VOLT - result.kS) / result.kV;
            double timeToMaxVel = result.kA / result.kV * MAX_VEL_TIME_SCALAR;
            timeToMaxVel = MathUtil.clamp(timeToMaxVel, MIN_TIME_TO_MAX_VEL, MAX_TIME_TO_MAX_VEL);
            maxAcceleration = maxVelocity / timeToMaxVel;
            maxJerk = maxAcceleration / TIME_TO_MAX_ACCEL;
        }

        Log.log(name + " analysis complete.");
    }

    public CloseLoopParam getParams() {
        return param;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public double getMaxAcceleration() {
        return maxAcceleration;
    }

    public double getMaxJerk() {
        return maxJerk;
    }

    private void analyzeGroup() {
        if (motorEntries == null || motorEntries.isEmpty()) return;

        Entry posEntry = null;
        Entry velEntry = null;
        Entry accelEntry = null;
        Entry voltEntry = null;

        for (Entry entry : motorEntries) {
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

        if (posEntry == null && motorEntries.size() > 0) posEntry = motorEntries.get(0);
        if (velEntry == null && motorEntries.size() > 1) velEntry = motorEntries.get(1);
        if (accelEntry == null && motorEntries.size() > 2) accelEntry = motorEntries.get(2);
        if (voltEntry == null && motorEntries.size() > 3) voltEntry = motorEntries.get(3);

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

                    rawData.add(new SyncedDataPoint(vel, pos, accel, volt, time));
                }
            }
        }

        if (rawData.isEmpty()) {
            for (Entry entry : motorEntries) {
                for (EntryPoint dp : entry.data) {
                    if (dp.value instanceof double[]) {
                        double[] vals = (double[]) dp.value;
                        if (vals.length >= 4) {
                            rawData.add(new SyncedDataPoint(vals[1], vals[0], vals[2], vals[3], dp.time));
                        }
                    } else if (dp.value instanceof float[]) {
                        float[] vals = (float[]) dp.value;
                        if (vals.length >= 4) {
                            rawData.add(new SyncedDataPoint(vals[1], vals[0], vals[2], vals[3], dp.time));
                        }
                    }
                }
            }
        }

        System.out.println("Group: " + name + " | Total data points: " + rawData.size());
        if (rawData.isEmpty()) return;

        rawData.sort((p1, p2) -> Long.compare(p1.timestamp, p2.timestamp));
        
        calculateResult();
    }

    private void calculateResult() {
        double bestAvgError = Double.MAX_VALUE;

        for (double voltageThreshold : VOLTAGE_THRESHOLDS) {
            for (int smoothWindow : SMOOTH_WINDOWS) {
                for (double zScoreThresholds :Z_SCORE_THRESHOLDS) {
                    List<SyncedDataPoint> cleanData = filterAndSmooth(rawData, voltageThreshold, smoothWindow);
                    if (cleanData.size() < 10) continue;

                    BucketResult initialResult = solveOLS(cleanData);
                    if (initialResult == null) continue;

                    List<SyncedDataPoint> refinedData = removeOutliers(cleanData, initialResult, zScoreThresholds);
                    if (refinedData.size() < 10) continue;

                    BucketResult candidateModel = solveOLS(refinedData);
                    
                    if (candidateModel != null) {
                        double currentKS = kFlags.useKS ? candidateModel.kS : 0;
                        double currentKA = kFlags.useKA ? candidateModel.kA : 0;
                        double currentKV = kFlags.useKV ? candidateModel.kV : 0;
    
                        if (currentKA < 0 || currentKS < 0|| currentKV < 0) {
                            continue;
                        }

                        candidateModel.rawPoints = rawData.size();

                        if (candidateModel.avgError < bestAvgError) {
                            bestAvgError = candidateModel.avgError;
                            result = candidateModel;
                        }
                    }
                }
            }
        }
        
        if (result == null) return;

        if (kFlags.useKCos) {
            checkZeroPos(result);
        }
        
        double sumErr = 0;
        double maxErr = 0;

        for (SyncedDataPoint p : rawData) {
            double pred = calculatePredictedVoltage(p, result);
            double error = Math.abs(p.voltage - pred);
            sumErr += error;
            if(error > maxErr) maxErr = error;
        }

        result.avgError = sumErr / rawData.size();
        result.maxError = maxErr;
        result.rawPoints = rawData.size();

        Log.log(name + " avg Error: " + result.avgError);
        Log.log(name + " max Error: " + result.maxError);
        Log.log(name + " used Points size: " + result.points);
        Log.log(name + " raw Points size: " + result.rawPoints);
        Log.log(name + " r Squared: " + result.rSquared);
    }

    private List<SyncedDataPoint> filterAndSmooth(List<SyncedDataPoint> data, double voltageThresh, int windowSize) {
        List<SyncedDataPoint> filtered = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            SyncedDataPoint current = data.get(i).copy();
            
            double sumAccel = 0;
            int count = 0;
            for (int j = Math.max(0, i - windowSize/2); j < Math.min(data.size(), i + windowSize/2 + 1); j++) {
                sumAccel += data.get(j).acceleration;
                count++;
            }
            current.acceleration = sumAccel / count;
            
            if (Math.abs(current.voltage) > voltageThresh) {
                filtered.add(current);
            }
        }
        return filtered;
    }

    private List<SyncedDataPoint> removeOutliers(List<SyncedDataPoint> data, BucketResult model, double zScoreThreshold) {
        if (zScoreThreshold <= 0) return data;

        double sumError = 0;

        for (SyncedDataPoint p : data) {
            double pred = calculatePredictedVoltage(p, model);
            p.error = Math.abs(p.voltage - pred);
            sumError += p.error;
        }

        double meanError = sumError / data.size();
        double sumSqDiff = 0;

        for (SyncedDataPoint p : data) {
            sumSqDiff += Math.pow(p.error - meanError, 2);
        }
        double stdDev = Math.sqrt(sumSqDiff / data.size());

        List<SyncedDataPoint> filteredData = new ArrayList<>();
        double maxAllowedError = meanError + (zScoreThreshold * stdDev);

        for (SyncedDataPoint p : data) {
            if (p.error <= maxAllowedError) {
                filteredData.add(p);
            }
        }

        return filteredData;
    }

    private BucketResult solveOLS(List<SyncedDataPoint> data) {
        int n = data.size();
        int numParams = 0;
        for (boolean kFlag : kFlags) {
            if(kFlag) numParams++;
        }

        if(numParams == 0) return null;

        SimpleMatrix A = new SimpleMatrix(n, numParams);
        SimpleMatrix b = new SimpleMatrix(n, 1);

        for (int i = 0; i < n; i++) {
            SyncedDataPoint p = data.get(i);
            b.set(i, 0, p.voltage);

            int col = 0;
            if(kFlags.useKS) A.set(i, col++, KFunctions.sFunction(p.velocity));
            if(kFlags.useKV) A.set(i, col++, KFunctions.vFunction(p.velocity));
            if(kFlags.useKA) A.set(i, col++, KFunctions.aFunction(p.acceleration));
            if(kFlags.useKG) A.set(i, col++, KFunctions.gFunction());
            if(kFlags.useKCos) A.set(i, col++, KFunctions.cosFunction(p.position, isCos));
            if(kFlags.useKV2) A.set(i, col++, KFunctions.v2Function(p.velocity));
        }

        SimpleMatrix x;
        try {
            x = A.solve(b);
        } catch(Exception e) {
            return null;
        }

        double[] k = new double[6];
        int col = 0;
        int index = 0;
        
        for (boolean kFlag : kFlags) {
            if(kFlag) k[index] = x.get(col++);
            index++;
        }

        double ssTot = 0, ssRes = 0, meanV = 0;
        for(SyncedDataPoint p : data) meanV += p.voltage;
        meanV /= n;

        BucketResult olsResult = new BucketResult(k[0], k[1], k[2], k[3], k[4], k[5], 0, 0, 0, 0);
        
        for (SyncedDataPoint p : data) {
            double pred = calculatePredictedVoltage(p, olsResult);

            ssTot += Math.pow(p.voltage - meanV, 2);
            ssRes += Math.pow(p.voltage - pred, 2);
        }

        double sumErr = 0;
        double maxErr = 0;

        for(SyncedDataPoint p : rawData) {
            double pred = calculatePredictedVoltage(p, olsResult);
            double error = Math.abs(p.voltage - pred);
            sumErr += error;
            if(error > maxErr) maxErr = error;
        }

        double avgError = sumErr / rawData.size();
        double maxError = maxErr;
        double r2 = 0;

        if (ssTot != 0) r2 = 1 - (ssRes / ssTot);

        return new BucketResult(k[0], k[1], k[2], k[3], k[4], k[5], avgError, maxError, n, r2);
    }

    private double calculatePredictedVoltage(SyncedDataPoint p, BucketResult model) {
        double pred = 0;
        if(kFlags.useKS) pred += model.kS * KFunctions.sFunction(p.velocity);
        if(kFlags.useKV) pred += model.kV * KFunctions.vFunction(p.velocity);
        if(kFlags.useKA) pred += model.kA * KFunctions.aFunction(p.acceleration);
        if(kFlags.useKG) pred += model.kG * KFunctions.gFunction();
        if(kFlags.useKCos) pred += model.kCos * KFunctions.cosFunction(p.position, isCos);
        if(kFlags.useKV2) pred += model.kV2 * KFunctions.v2Function(p.velocity);
        return pred;
    }

    private void checkZeroPos(BucketResult result) {
        boolean prevIsCos = isCos;
        isCos = false;

        try {
            List<SyncedDataPoint> cleanData = filterAndSmooth(rawData, VOLTAGE_THRESHOLDS[0], SMOOTH_WINDOWS[0]);
            BucketResult sinResult = solveOLS(cleanData);

            if (sinResult != null && sinResult.avgError < result.avgError) {
                Log.log("its seems " + name
                        + " zero is not Supported by the code, the zero should be forward like Unit Circle");
            }
        } finally {
            isCos = prevIsCos;
        }
    }
}