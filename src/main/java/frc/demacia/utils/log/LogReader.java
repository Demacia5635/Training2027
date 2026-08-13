package frc.demacia.utils.log;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.RobotBase;

public class LogReader {
    private static final Map<String, List<Entry>> groups = new HashMap<>();
    private static final List<Entry> entries = new ArrayList<>();

    private static final Map<Integer, ActiveLogData> activeEntriesMap = new HashMap<>(); // for adding values to entries

    private static Predicate<EntryInfo> filter;

    private record ActiveLogData(String type, List<Entry> targetEntries) {}
    public record EntryInfo(String name, String type, String metadata) {}

    public static class Entry {
        public String groupName;
        public String name;
        public List<EntryPoint> data = new ArrayList<>();

        public Entry(String groupName, String name) {
            this.groupName = groupName;
            this.name = name;
        }
    }

    public static class EntryPoint {
        public Object value;
        public long time;

        public EntryPoint(Object value, long time) {
            this.value = value;
            this.time = time;
        }
    }

    public static List<Entry> getEntries(boolean isLatestLog, Predicate<EntryInfo> filter) {
        processLogFile(isLatestLog, filter);
        
        return entries;
    }

    public static Map<String, List<Entry>> getGroups(boolean isLatestLog, Predicate<EntryInfo> filter) {
        processLogFile(isLatestLog, filter);
        
        return groups;
    }

    private static void processLogFile(boolean isLatestLog, Predicate<EntryInfo> filter) {
        LogReader.filter = filter != null ? filter : info -> true;
    
        entries.clear();
        groups.clear();
        activeEntriesMap.clear();
        
        try {
            System.out.println("Reading log file...");
            String filePath = isLatestLog ? loadLatestRobotLog() : LogFileChooser.selectFileFromComputer();

            wpilogReader(filePath);
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String loadLatestRobotLog() throws IOException {
        DataLogManager.getLog().flush();

        File latestLogFile = getLatestLogFile();
        if (latestLogFile == null) {
            throw new IOException("SysID Error: No .wpilog files found on the robot!");
        }

        System.out.println("SysID: Successfully found and loading latest log: " + latestLogFile.getAbsolutePath());

        return latestLogFile.getAbsolutePath(); 
    }

    private static File getLatestLogFile() {
        File logDir;

        if (RobotBase.isSimulation()) {
            logDir = new File("logs/");
        } else {
            logDir = new File("/home/lvuser/logs/");
        }

        if (!logDir.exists()) {
            return null;
        }
        
        File[] files = logDir.listFiles((dir, name) -> name.endsWith(".wpilog"));

        if (files == null || files.length == 0) {
            return null;
        }

        File latestFile = files[0];
        for (File file : files) {
            if (file.lastModified() > latestFile.lastModified()) {
                latestFile = file;
            }
        }
        return latestFile;
    }

    private static void wpilogReader(String fileName) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
             DataInputStream dataInputStream = new DataInputStream(fileInputStream)) {
            
            byte[] signature = readHeader(dataInputStream);
            if (!Arrays.equals(signature, "WPILOG".getBytes())) {
                throw new IOException("Invalid WPILOG");
            }

            skipHeaderExtra(dataInputStream);
            System.out.println("Header read successfully. Starting to read records...");
            readRecords(dataInputStream);
            System.out.println("File Read Successfully. Total entries: " + entries.size());
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
                if(n%1000 == 0) {
                    System.out.println("Read " + n + " records...");
                }
            } catch (EOFException e) {
                break;
            }
        }
    }

    private static void readRecord(DataInputStream dataInputStream) throws IOException {
        int headerByte = dataInputStream.readUnsignedByte();
        int idLength = (headerByte & 0x3) + 1;
        int payloadLength = (headerByte >> 2 & 0x3) + 1;
        int timestampLength = (headerByte >> 4 & 0x7) + 1;
        int recordId = readLittleEndianInt(dataInputStream, idLength);
        int payloadSize = readLittleEndianInt(dataInputStream, payloadLength);
        long timestamp = readLittleEndianLong(dataInputStream, timestampLength);

        if (recordId == 0) {
            addEntryFromControlRecord(dataInputStream, payloadSize);
            return;
        }

        ActiveLogData activeData = activeEntriesMap.get(recordId);
        
        if (activeData == null) {
            dataInputStream.skipBytes(payloadSize);
            return;
        }

        String type = activeData.type();
        List<Entry> targets = activeData.targetEntries();

        if (type.equals("float[]")) {
            int count = payloadSize / 4;
            for (int i = 0; i < count; i++) {
                float val = Float.intBitsToFloat(Integer.reverseBytes(dataInputStream.readInt()));
                if (i < targets.size() && targets.get(i) != null) {
                    targets.get(i).data.add(new EntryPoint((double) val, timestamp));
                }
            }
            int remainder = payloadSize % 4;
            if (remainder > 0) dataInputStream.skipBytes(remainder);

        } else if (type.equals("double[]")) {
            int count = payloadSize / 8;
            for (int i = 0; i < count; i++) {
                double val = Double.longBitsToDouble(Long.reverseBytes(dataInputStream.readLong()));
                if (i < targets.size() && targets.get(i) != null) {
                    targets.get(i).data.add(new EntryPoint(val, timestamp));
                }
            }
            int remainder = payloadSize % 8;
            if (remainder > 0) dataInputStream.skipBytes(remainder);

        } else if (type.equals("boolean[]")) {
            for (int i = 0; i < payloadSize; i++) {
                boolean val = dataInputStream.readByte() != 0;
                if (i < targets.size() && targets.get(i) != null) {
                    targets.get(i).data.add(new EntryPoint(val, timestamp));
                }
            }

        } else if (type.equals("string[]")) {
            int bytesRead = 0;
            int i = 0;
            while (bytesRead < payloadSize) {
                int strLen = Integer.reverseBytes(dataInputStream.readInt());
                bytesRead += 4;
                
                byte[] strBytes = new byte[strLen];
                dataInputStream.readFully(strBytes);
                bytesRead += strLen;
                
                String val = new String(strBytes, "UTF-8");
                if (i < targets.size() && targets.get(i) != null) {
                    targets.get(i).data.add(new EntryPoint(val, timestamp));
                }
                i++;
            }
        } else if (type.equals("float")) {
            if (payloadSize >= 4) {
                float val = Float.intBitsToFloat(Integer.reverseBytes(dataInputStream.readInt()));
                if (!targets.isEmpty() && targets.get(0) != null) {
                    targets.get(0).data.add(new EntryPoint((double) val, timestamp));
                }
                if (payloadSize > 4) dataInputStream.skipBytes(payloadSize - 4);
            } else {
                dataInputStream.skipBytes(payloadSize);
            }
        } else if (type.equals("double")) {
            if (payloadSize >= 8) {
                double val = Double.longBitsToDouble(Long.reverseBytes(dataInputStream.readLong()));
                if (!targets.isEmpty() && targets.get(0) != null) {
                    targets.get(0).data.add(new EntryPoint(val, timestamp));
                }
                if (payloadSize > 8) dataInputStream.skipBytes(payloadSize - 8);
            } else {
                dataInputStream.skipBytes(payloadSize);
            }
        } else if (type.equals("boolean")) {
            if (payloadSize >= 1) {
                boolean val = dataInputStream.readByte() != 0;
                if (!targets.isEmpty() && targets.get(0) != null) {
                    targets.get(0).data.add(new EntryPoint(val, timestamp));
                }
                if (payloadSize > 1) dataInputStream.skipBytes(payloadSize - 1);
            } else {
                dataInputStream.skipBytes(payloadSize);
            }
        } else if (type.equals("string")) {
            byte[] strBytes = new byte[payloadSize];
            dataInputStream.readFully(strBytes);
            String val = new String(strBytes, "UTF-8");
            
            if (!targets.isEmpty() && targets.get(0) != null) {
                targets.get(0).data.add(new EntryPoint(val, timestamp));
            }
        } else {
            dataInputStream.skipBytes(payloadSize);
        }
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

            List<Entry> targets = new ArrayList<>();

            if (name.contains(":") && (type.equals("float[]") || type.equals("double[]") || type.equals("boolean[]") || type.equals("string[]"))) {
                String cleanName = name;
                if (cleanName.startsWith("Float/")) cleanName = cleanName.substring(6);
                else if (cleanName.startsWith("Double/")) cleanName = cleanName.substring(7);
                else if (cleanName.startsWith("Boolean/")) cleanName = cleanName.substring(8);
                else if (cleanName.startsWith("String/")) cleanName = cleanName.substring(7);
    
                String[] groupsArr = cleanName.split("\\|");
                String[] metaArr = metadata.split("\\|");

                for (int i = 0; i < groupsArr.length; i++) {
                    String group = groupsArr[i];
                    String currentMeta = (i < metaArr.length) ? metaArr[i].trim() : "";
                    String[] parts = group.split(":");

                    if (parts.length == 2) {
                        String baseGroup = parts[0].trim();
                        String[] fields = parts[1].split(",");
                        String groupName = currentMeta.isEmpty() ? baseGroup : currentMeta + "/" + baseGroup;

                        for (String field : fields) {
                            String fieldName = field.trim();
                            String entryName = baseGroup + "/" + field.trim();
                            EntryInfo info = new EntryInfo(entryName, type.replace("[]", ""), currentMeta);
                            
                            if (filter.test(info)) {
                                Entry newEntry = new Entry(groupName, fieldName);
                                entries.add(newEntry);
                                groups.computeIfAbsent(groupName, key -> new ArrayList<>()).add(newEntry);
                                targets.add(newEntry);
                            } else {
                                targets.add(null);
                            }
                        }
                    }
                }
            } else {
                EntryInfo info = new EntryInfo(name, type, metadata);

                if (filter.test(info) && !metadata.contains("replay")) {
                    String fieldName = name;
                    String groupName = metadata.isEmpty() ? "" : metadata + "/"; 
                
                    if (name.contains("/")) {
                        fieldName = name.substring(name.lastIndexOf('/') + 1);
                        groupName += name.substring(0, name.lastIndexOf('/'));
                    }

                    Entry newEntry = new Entry(groupName, fieldName);
                    entries.add(newEntry);

                    groups.computeIfAbsent(groupName, key -> new ArrayList<>()).add(newEntry);
                    targets.add(newEntry);
                } else {
                    targets.add(null);
                }
            }

            activeEntriesMap.put(entryId, new ActiveLogData(type, targets));
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
}
