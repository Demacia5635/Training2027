package frc.demacia.sysid;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ejml.simple.SimpleMatrix;

public class LogReader {

    private static final double VOLTAGE_THRESHOLD = 0.5;
    private static final int SMOOTH_WINDOW = 3;
    private static final double OUTLIER_PERCENTAGE = 0.15;

    private static double minTimestamp;
    private static double maxTimestamp;

    private static Map<Integer, List<EntryDescription>> entries;

    private static class EntryDescription {
        String name;
        boolean isFloat;
        boolean isDouble;
        List<DataPoint> data = new ArrayList<>();

        EntryDescription(String name, String type) {
            this.name = name;
            isDouble = type.equals("double") || type.equals("double[]");
            isFloat = type.equals("float") || type.equals("float[]");
        }
    }

    private static class DataPoint {
        long timestamp;
        double[] value;

        DataPoint(long timestamp, double[] value) {
            this.timestamp = timestamp;
            this.value = value.clone();
        }
    }

    public static Map<String, BucketResult> getResult(String fileName) {
        entries = new HashMap<>();
        try {
            wpilogReader(fileName);
            System.out.println("Performing analysis...");
            return performAnalysis();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private static void wpilogReader(String fileName) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
             DataInputStream dataInputStream = new DataInputStream(fileInputStream)) {
            
            byte[] signature = readHeader(dataInputStream);
            if (!Arrays.equals(signature, "WPILOG".getBytes())) {
                throw new IOException("Invalid WPILOG");
            }
            minTimestamp = Double.MAX_VALUE;
            maxTimestamp = 0;
            
            skipHeaderExtra(dataInputStream);
            System.out.println("Header read successfully. Starting to read records...");
            readRecords(dataInputStream);
            System.out.println("File Read Successfully. Total entries: " + entries.size());
            System.out.println("Timestamp range: " + minTimestamp + "s to " + maxTimestamp + "s");
        }
    }

    private static byte[] readHeader(DataInputStream dataInputStream) throws IOException {
        byte[] signature = new byte[6];
        dataInputStream.readFully(signature);
        return signature;
    }

    private static void skipHeaderExtra(DataInputStream dataInputStream) throws IOException {
        dataInputStream.readShort();
        int extraLength = Integer.reverseBytes(dataInputStream.readInt());
        if(extraLength > 0) {
            dataInputStream.skipBytes(extraLength);
        }
    }
    private static void readRecords(DataInputStream dataInputStream) throws IOException {
        int n = 0;
        while (true) {
            try {
                readRecord(dataInputStream);
                n++;
                if(n%100 == 0) {
                    System.out.println("Read " + n + " records...");
                }
            } catch (EOFException e) {
                break;
            }
        }
    }

    private static boolean readRecord(DataInputStream dataInputStream) throws IOException {
        int headerByte = dataInputStream.readUnsignedByte();
        int idLength = (headerByte & 0x3) + 1;
        int payloadLength = (headerByte >> 2 & 0x3) + 1;
        int timestampLength = (headerByte >> 4 & 0x7) + 1;
        int recordId = readLittleEndianInt(dataInputStream, idLength);
        int payloadSize = readLittleEndianInt(dataInputStream, payloadLength);
        long timestamp = readLittleEndianLong(dataInputStream, timestampLength);
        double time = timestamp / 1000.0;
            if (time < minTimestamp) minTimestamp = time;
            if (time > maxTimestamp) maxTimestamp = time;

        if (recordId == 0) {
            addEntryFromControlRecord(dataInputStream, payloadSize);
            return false;
        } else {
            List<EntryDescription> entryList = entries.get(recordId);
            if (entryList != null && !entryList.isEmpty()) {
                EntryDescription e = entryList.get(0);
                boolean isFloat = e.isFloat;
                boolean isDouble = e.isDouble;

                if (isFloat || isDouble) {
                    double[] value = null;

                    if (isFloat) {
                        if (payloadSize % 4 == 0) {
                            int count = payloadSize / 4;
                            value = new double[count];
                            for (int i = 0; i < count; i++) {
                                int raw = Integer.reverseBytes(dataInputStream.readInt());
                                value[i] = (double) Float.intBitsToFloat(raw);
                            }
                        } else {
                            dataInputStream.skipBytes(payloadSize);
                        }
                    } else if (isDouble) {
                         if (payloadSize % 8 == 0) {
                            int count = payloadSize / 8;
                            value = new double[count];
                            for (int i = 0; i < count; i++) {
                                long raw = Long.reverseBytes(dataInputStream.readLong());
                                value[i] = Double.longBitsToDouble(raw);
                            }
                        } else {
                            dataInputStream.skipBytes(payloadSize);
                        }
                    }

                    if (value != null) {
                        int numEntries = entryList.size();
                        if (numEntries > 1 && value.length % numEntries == 0) {
                            int chunkSize = value.length / numEntries;
                            for (int i = 0; i < numEntries; i++) {
                                double[] slice = Arrays.copyOfRange(value, i * chunkSize, (i + 1) * chunkSize);
                                entryList.get(i).data.add(new DataPoint(timestamp, slice));
                            }
                        } else {
                            for (EntryDescription entry : entryList) {
                                entry.data.add(new DataPoint(timestamp, value));
                            }
                        }
                        return true;
                    }
                } else {
                    dataInputStream.skipBytes(payloadSize);
                }
            } else {
                dataInputStream.skipBytes(payloadSize);
            }
        }
        return false;
    }

    private static void addEntryFromControlRecord(DataInputStream dataInputStream, int payloadSize) throws IOException {
        int recordType = dataInputStream.readUnsignedByte();
        if (recordType == 0) { 
            int entryId = Integer.reverseBytes(dataInputStream.readInt());
            int nameLength = Integer.reverseBytes(dataInputStream.readInt());
            String name = readString(dataInputStream, nameLength);
            int typeLength = Integer.reverseBytes(dataInputStream.readInt());
            String type = readString(dataInputStream, typeLength).trim();
            int metaLength = Integer.reverseBytes(dataInputStream.readInt());
            String metadata = readString(dataInputStream, metaLength);

            String[] names = name.split(" \\| ");
            String[] metas = metadata.split(" \\| ");
            
            for (int i = 0; i < names.length; i++) {
                String currentName = names[i].trim().split("\\: ")[0];
                String currentMeta = (i < metas.length) ? metas[i].trim() : "";
            
                if (currentMeta.contains("motor")) {
                    entries.putIfAbsent(entryId, new ArrayList<>());
                    entries.get(entryId).add(new EntryDescription(currentName, type));
                }
            }
        } else {
            if (payloadSize > 1) {
                dataInputStream.skipBytes(payloadSize - 1);
            }
        }
    }

    private static String readString(DataInputStream dataInputStream, int length) throws IOException {
        byte[] bytes = new byte[length];
        dataInputStream.readFully(bytes);
        return new String(bytes, "UTF-8");
    }

    private static int readLittleEndianInt(DataInputStream dis, int bytes) throws IOException {
        int result = 0;
        for (int i = 0; i < bytes; i++) {
            result |= (dis.readUnsignedByte() << (i * 8));
        }
        return result;
    }

    private static long readLittleEndianLong(DataInputStream dis, int bytes) throws IOException {
        long result = 0L;
        for (int i = 0; i < bytes; i++) {
            result |= ((long) dis.readUnsignedByte() << (i * 8));
        }
        return result;
    }

    private static Map<String, BucketResult> performAnalysis() {
        Map<String, BucketResult> results = new HashMap<>();
        Set<String> groups = findGroups();
        
        for (String group : groups) {
            System.out.println("Analyzing group: " + group);
            BucketResult result = analyzeGroup(group);
            if (result != null) {
                results.put(group, result);
            }
        }
        System.out.println("Analysis complete. Results for " + results.size() + " groups.");
        return results;
    }

    private static Set<String> findGroups() {
        Set<String> groups = new HashSet<>();
        for (List<EntryDescription> list : entries.values()) {
            for (EntryDescription entry : list) {
                groups.add(entry.name);
            }
        }
        return groups;
    }

    private static BucketResult analyzeGroup(String name) {
        List<DataPoint> allData = new ArrayList<>();
        for (List<EntryDescription> list : entries.values()) {
            for (EntryDescription entry : list) {
                if (entry.name.equals(name)) {
                    allData.addAll(entry.data);
                }
            }
        }

        System.out.println("Group: " + name + " | Total data points: " + allData.size());
        if (allData.isEmpty()) return null;

        allData.sort((p1, p2) -> Long.compare(p1.timestamp, p2.timestamp));
        List<SyncedDataPoint> syncedData = synchronizeData(allData);
        
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

    private static List<SyncedDataPoint> synchronizeData(List<DataPoint> dataPoints) {
        List<SyncedDataPoint> result = new ArrayList<>();
        for (DataPoint dp : dataPoints) {
            if (dp.value.length >= 4) {
                result.add(new SyncedDataPoint(dp.value[1], dp.value[0], dp.value[2], dp.value[3], dp.timestamp));
            }
        }
        return result;
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
            
            if (current.voltage != 0)
            System.out.println(current.voltage);
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
}