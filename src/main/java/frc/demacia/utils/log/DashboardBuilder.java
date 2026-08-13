package frc.demacia.utils.log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.FloatSubscriber;
import edu.wpi.first.networktables.IntegerSubscriber;
import edu.wpi.first.networktables.NTSendableBuilder;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.networktables.Topic;
import edu.wpi.first.util.function.BooleanConsumer;
import edu.wpi.first.util.function.FloatConsumer;
import edu.wpi.first.util.function.FloatSupplier;

/**
 * Custom NTSendableBuilder implementation to send all Sendable/NTSendable data 
 * through Demacia's custom Log framework.
 */
public class DashboardBuilder implements NTSendableBuilder {
    private final NetworkTable m_table;
    private final String m_basePath;
    private final List<Runnable> m_updaters = new ArrayList<>();
    private final List<Runnable> m_inputCheckers = new ArrayList<>();
    private final List<AutoCloseable> m_closeables = new ArrayList<>();
    private Runnable m_updateTable;

    public DashboardBuilder(String tableName) {
        this.m_basePath = tableName;
        this.m_table = Log.table.getSubTable(tableName);
    }

    
    @Override
    public NetworkTable getTable() {
        return m_table;
    }

    @Override
    public BackendKind getBackendKind() {
        return BackendKind.kNetworkTables;
    }

    @Override
    public Topic getTopic(String key) {
        return m_table.getTopic(key);
    }

    @Override
    public void setUpdateTable(Runnable func) {
        this.m_updateTable = func;
    }

   
    @Override
    public void setSmartDashboardType(String type) {
        m_table.getStringTopic(".type").publish().set(type);
        Log.putData(m_basePath + "/.type", () -> type);
    }

    @Override
    public void setActuator(boolean value) {
        m_table.getBooleanTopic(".actuator").publish().set(value);
    }

    @Override
    public void setSafeState(Runnable func) {
    }

    @Override
    public void addCloseable(AutoCloseable closeable) {
        m_closeables.add(closeable);
    }

    @Override
    public boolean isPublished() {
        return true;
    }

    @Override
    public void clearProperties() {
        m_updaters.clear();
        m_inputCheckers.clear();
        m_updateTable = null;
    }

    @Override
    public void close() {
        clearProperties();
        for (AutoCloseable c : m_closeables) {
            try {
                c.close();
            } catch (Exception ignored) {
            }
        }
        m_closeables.clear();
    }

    

    @Override
    public void addBooleanProperty(String key, BooleanSupplier getter, BooleanConsumer setter) {
        String fullKey = m_basePath + "/" + key;
        var topic = m_table.getBooleanTopic(key);
        if (getter != null) {
            Log.putData(fullKey, getter::getAsBoolean);
            var pub = topic.publish();
            m_updaters.add(() -> pub.set(getter.getAsBoolean()));
        }
        if (setter != null) {
            BooleanSubscriber sub = topic.subscribe(false);
            m_inputCheckers.add(() -> {
                for (boolean value : sub.readQueueValues()) {
                    setter.accept(value);
                }
            });
        }
    }

    @Override
    public void addDoubleProperty(String key, DoubleSupplier getter, DoubleConsumer setter) {
        String fullKey = m_basePath + "/" + key;
        var topic = m_table.getDoubleTopic(key);
        if (getter != null) {
            Log.putData(fullKey, getter::getAsDouble);
            var pub = topic.publish();
            m_updaters.add(() -> pub.set(getter.getAsDouble()));
        }
        if (setter != null) {
            DoubleSubscriber sub = topic.subscribe(0.0);
            m_inputCheckers.add(() -> {
                for (double value : sub.readQueueValues()) {
                    setter.accept(value);
                }
            });
        }
    }

    @Override
    public void addFloatProperty(String key, FloatSupplier getter, FloatConsumer setter) {
        String fullKey = m_basePath + "/" + key;
        var topic = m_table.getFloatTopic(key);
        if (getter != null) {
            Log.putData(fullKey, getter::getAsFloat);
            var pub = topic.publish();
            m_updaters.add(() -> pub.set(getter.getAsFloat()));
        }
        if (setter != null) {
            FloatSubscriber sub = topic.subscribe(0.0f);
            m_inputCheckers.add(() -> {
                for (float value : sub.readQueueValues()) {
                    setter.accept(value);
                }
            });
        }
    }

    @Override
    public void addIntegerProperty(String key, LongSupplier getter, LongConsumer setter) {
        String fullKey = m_basePath + "/" + key;
        var topic = m_table.getIntegerTopic(key);
        if (getter != null) {
            Log.putData(fullKey, getter::getAsLong);
            var pub = topic.publish();
            m_updaters.add(() -> pub.set(getter.getAsLong()));
        }
        if (setter != null) {
            IntegerSubscriber sub = topic.subscribe(0);
            m_inputCheckers.add(() -> {
                for (long value : sub.readQueueValues()) {
                    setter.accept(value);
                }
            });
        }
    }

    @Override
    public void addStringProperty(String key, Supplier<String> getter, Consumer<String> setter) {
        String fullKey = m_basePath + "/" + key;
        var topic = m_table.getStringTopic(key);
        if (getter != null) {
            Log.putData(fullKey, getter);
            var pub = topic.publish();
            m_updaters.add(() -> pub.set(getter.get()));
        }
        if (setter != null) {
            StringSubscriber sub = topic.subscribe("");
            m_inputCheckers.add(() -> {
                for (String value : sub.readQueueValues()) {
                    setter.accept(value);
                }
            });
        }
    }

    @Override
    public void addBooleanArrayProperty(String key, Supplier<boolean[]> getter, Consumer<boolean[]> setter) {
        if (getter != null) {
            Log.putData(m_basePath + "/" + key, getter);
        }
    }

    @Override
    public void addDoubleArrayProperty(String key, Supplier<double[]> getter, Consumer<double[]> setter) {
        if (getter != null) {
            Log.putData(m_basePath + "/" + key, getter);
        }
    }

    @Override
    public void addFloatArrayProperty(String key, Supplier<float[]> getter, Consumer<float[]> setter) {
        if (getter != null) {
            Log.putData(m_basePath + "/" + key, getter);
        }
    }


    @Override
    public void addIntegerArrayProperty(String key, Supplier<long[]> getter, Consumer<long[]> setter) {
        if (getter != null) {
        Log.putData(m_basePath + "/" + key, getter);
    }
    }

    @Override
    public void addStringArrayProperty(String key, Supplier<String[]> getter, Consumer<String[]> setter) {
        if (getter != null){
         Log.putData(m_basePath + "/" + key, getter);
        }
    }

    @Override
    public void addRawProperty(String key, String typeString, Supplier<byte[]> getter, Consumer<byte[]> setter) {
        if (getter != null) {
        Log.putData(m_basePath + "/" + key, getter);
        }
    }

    @Override
    public void publishConstBoolean(String key, boolean value) {
        Log.putData(m_basePath + "/" + key, () -> value);
    }

    @Override
    public void publishConstInteger(String key, long value) {
        Log.putData(m_basePath + "/" + key, () -> value);
    }

    @Override
    public void publishConstFloat(String key, float value) {
        Log.putData(m_basePath + "/" + key, () -> value);
    }

    @Override
    public void publishConstDouble(String key, double value) {
        Log.putData(m_basePath + "/" + key, () -> value);
    }

    @Override
    public void publishConstString(String key, String value) {
        Log.putData(m_basePath + "/" + key, () -> value);
    }

    @Override
    public void publishConstBooleanArray(String key, boolean[] value) {
        Log.putData(m_basePath + "/" + key, () -> value);
    }

    @Override
    public void publishConstIntegerArray(String key, long[] value) {
        Log.putData(m_basePath + "/" + key, () -> value);
    }

    @Override
    public void publishConstFloatArray(String key, float[] value) {
        Log.putData(m_basePath + "/" + key, () -> value);
    }

    @Override
    public void publishConstDoubleArray(String key, double[] value) {
        Log.putData(m_basePath + "/" + key, () -> value);
    }

    @Override
    public void publishConstStringArray(String key, String[] value) {
        Log.putData(m_basePath + "/" + key, () -> value);
    }

    @Override
    public void publishConstRaw(String key, String typeString, byte[] value) {
        Log.putData(m_basePath + "/" + key, () -> value);
    }

   
    public void update() {
        if (m_updateTable != null) {
            m_updateTable.run();
        }
        for (int i = 0; i < m_updaters.size(); i++) {
            m_updaters.get(i).run();
        }
    }

    public void pollInputs() {
        for (int i = 0; i < m_inputCheckers.size(); i++) {
            m_inputCheckers.get(i).run();
        }
    }
}