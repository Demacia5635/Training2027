package frc.demacia.utils;


import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;

/**
 * A generic wrapper class for data sources (StatusSignals or Suppliers).
 * <p>
 * This class abstracts the source of data and its type, allowing uniform
 * handling
 * of logging and network tables. It caches values to detect changes and convert
 * types (e.g., Boolean to Double) efficiently.
 * </p>
 * 
 * @param <T> The type of the data (Double, Boolean, String, etc.)
 */
public class Data<T> {
    /**
     * Static array of all signals to refresh them all at once (CTRE optimization)
     */
    private static BaseStatusSignal[] rioSignalsArray = new BaseStatusSignal[0];
    private static BaseStatusSignal[] canivoreSignalsArray = new BaseStatusSignal[0];
    /** List of all Data instances based on Signals */
    private static final ArrayList<Data<?>> signalInstances = new ArrayList<>();
    /** List of all Data instances based on Suppliers */
    private static final ArrayList<Data<?>> supplierInstances = new ArrayList<>();

    private static final ArrayList<Data<?>> groupFloatData = new ArrayList<>();
    private static String groupFloatDataName = "Float/";
    private static String groupFloatDataMetaData = "";
    private static float[] groupFloatDataValues = new float[0];
    private static boolean isGroupFloatFirst = true;
    private static final ArrayList<Data<?>> groupBooleanData = new ArrayList<>();
    private static String groupBooleanDataName = "Boolean/";
    private static String groupBooleanDataMetaData = "";
    private static boolean[] groupBooleanDataValues = new boolean[0];
    private static boolean isGroupBooleanFirst = true;
    private static final ArrayList<Data<?>> groupStringData = new ArrayList<>();
    private static String groupStringDataName = "String/";
    private static String groupStringDataMetaData = "";
    private static String[] groupStringDataValues = new String[0];
    private static boolean isGroupStringFirst = true;

    private StatusSignal<T> signal;
    private Supplier<T> supplier;

    private boolean isDouble = false;
    private boolean isBoolean = false;

    private boolean changed = true;

    // Cached primitive arrays to avoid auto-boxing and garbage collection
    private double doubleValue;
    private float floatValue;
    private boolean booleanValue;
    private String stringValue;

    /**
     * Creates a new Data object from Phoenix 6 StatusSignals.
     * 
     * @param signal Variable arguments of StatusSignals
     */
    public Data(StatusSignal<T> signal, boolean isRio) {
        this.signal = signal;

        detectTypeFromSignal();
        registerSignal();
        addSignal(signal, isRio);
        refresh();
    }

    /**
     * Creates a new Data object from Java Suppliers.
     * 
     * @param supplier Variable arguments of Suppliers
     */
    public Data(Supplier<T> supplier) {
        this.supplier = supplier;

        detectTypeFromSupplier();
        registerSupplier();
        refresh();
    }

    /** Registers this instance's signals to the static master list */
    private void registerSignal() {
        signalInstances.add(this);
    }

    /** Registers this instance to the static supplier list */
    private void registerSupplier() {
        supplierInstances.add(this);
    }

    /** Detects the data type based on the first signal's value */
    private void detectTypeFromSignal() {
        T value = signal.getValue();

        if (value instanceof Number) {
            isDouble = true;
        } else if (value instanceof Boolean) {
            isBoolean = true;
        } else {
            try {
                signal.getValueAsDouble();
                isDouble = true;
            } catch (Exception e) {
                isDouble = false;
            }
        }
    }

    /** Detects the data type based on the first supplier's value */
    private void detectTypeFromSupplier() {
        T value = supplier.get();

        if (value instanceof Number) {
            isDouble = true;
        } else if (value instanceof Boolean) {
            isBoolean = true;
        }
    }

    /**
     * Refreshes the local data from the source.
     * If using signals, assumes the master refresh has already been called.
     */
    private void refresh() {
        if (signal != null) {
            StatusSignal.refreshAll(signal);
            updateSignalValue();
        } else {
            refreshSupplier();
        }
    }

    public static void addSignal(StatusSignal<?> signals, boolean isRio) {
        if (isRio) {
            BaseStatusSignal[] newArr = new BaseStatusSignal[rioSignalsArray.length + 1];
            System.arraycopy(rioSignalsArray, 0, newArr, 0, rioSignalsArray.length);
            newArr[newArr.length - 1] = signals;
            rioSignalsArray = newArr;
        }
        else{
            BaseStatusSignal[] newArr = new BaseStatusSignal[canivoreSignalsArray.length + 1];
            System.arraycopy(canivoreSignalsArray, 0, newArr, 0, canivoreSignalsArray.length);
            newArr[newArr.length - 1] = signals;
            canivoreSignalsArray = newArr;
        }
    }

    public static void addSignals(boolean isRio, StatusSignal<?>... signals) {
        if (isRio) {
            BaseStatusSignal[] newArr = new BaseStatusSignal[rioSignalsArray.length + signals.length];
            System.arraycopy(rioSignalsArray, 0, newArr, 0, rioSignalsArray.length);
            System.arraycopy(signals, 0, newArr, rioSignalsArray.length, signals.length);
            rioSignalsArray = newArr;
        }
        else{
            BaseStatusSignal[] newArr = new BaseStatusSignal[canivoreSignalsArray.length + signals.length];
            System.arraycopy(canivoreSignalsArray, 0, newArr, 0, canivoreSignalsArray.length);
            System.arraycopy(signals, 0, newArr, canivoreSignalsArray.length, signals.length);
            canivoreSignalsArray = newArr;
        }
    }

    /**
     * Updates the internal primitive arrays from the signals.
     * Sets the 'changed' flag if values have changed.
     */
    private void updateSignalValue() {
        changed = false;
        if (isDouble) {
            if (doubleValue != signal.getValueAsDouble()) {
                changed = true;
                doubleValue = signal.getValueAsDouble();
            }
            if (floatValue != signal.getValueAsDouble()) {
                changed = true;
                floatValue = (float) signal.getValueAsDouble();
            }
        } else if (isBoolean) {
            if (booleanValue != (Boolean) signal.getValue()) {
                changed = true;
                booleanValue = (Boolean) signal.getValue();
            }
        } else {
            if (!Objects.equals(stringValue, ((signal.getValue() == null) ? "null" : signal.getValue().toString()))) {
                changed = true;
                stringValue = (signal.getValue() == null) ? "null" : signal.getValue().toString();;
            }
        }
    }

    /**
     * Updates the internal primitive arrays from the suppliers.
     * Sets the 'changed' flag if values have changed.
     */
    private void refreshSupplier() {
        changed = false;
        if (isDouble) {
            double newVal = ((Number) supplier.get()).doubleValue();
            if (doubleValue != newVal) {
                changed = true;
                doubleValue = newVal;
            }
            float newFloatVal = ((Number) supplier.get()).floatValue();
            if (floatValue != newFloatVal) {
                changed = true;
                floatValue = newFloatVal;
            }
        } else if (isBoolean) {
            boolean newVal = (Boolean) supplier.get();
            if (booleanValue != newVal) {
                changed = true;
                booleanValue = newVal;
            }
        } else {
            String newVal = (supplier.get() == null) ? "null" : supplier.get().toString();
            if (!Objects.equals(stringValue, newVal)) {
                changed = true;
                stringValue = newVal;
            }
        }
    }

    public static void setFrequancyAll() {
       // StatusSignal.setUpdateFrequencyForAll(Frequency.ofBaseUnits(100, Hertz), rioSignals);
       // StatusSignal.setUpdateFrequencyForAll(Frequency.ofBaseUnits(100, Hertz), canivoreSignals);
    }

    /**
     * Static method to refresh all registered Data instances.
     * First refreshes all Phoenix signals, then updates local values.
     */
    public static void refreshAll() {
        if (rioSignalsArray.length > 0) {
            BaseStatusSignal.refreshAll(rioSignalsArray);
        }
        
        if (canivoreSignalsArray.length > 0) {
            BaseStatusSignal.refreshAll(canivoreSignalsArray);
        }

        for (int i = 0; i < signalInstances.size(); i++) {
            signalInstances.get(i).updateSignalValue();
        }

        for (int i = 0; i < supplierInstances.size(); i++) {
            supplierInstances.get(i).refreshSupplier();
        }
    }

    /**
     * @return true if the data has changed since the last refresh
     */
    public boolean hasChanged() {
        return changed;
    }

    /**
     * @return The value as a double (1.0 for true if boolean)
     */
    public double getDouble() {
        return isDouble ? doubleValue
                        : isBoolean ? booleanValue ? 1f : 0f
                                : 0f;
    }

    /**
     * @return The value as a float (1f for true if boolean)
     */
    public float getFloat() {
        return isDouble ? floatValue
                        : isBoolean ? booleanValue ? 1f : 0f
                                : 0f;
    }

    /**
     * @return The value as a boolean (true for 1 if number)
     */
    public boolean getBoolean() {
        return isBoolean ? booleanValue
                        : isDouble ? doubleValue == 1
                                : false;
    }

    /**
     * @return The value as a string
     */
    public String getString() {
        return isDouble ? ((Double) doubleValue).toString()
                        : isBoolean ? ((Boolean) booleanValue).toString()
                                : stringValue;
    }

    /**
     * @return The first StatusSignal if available
     */
    public StatusSignal<T> getSignal() {
        return (signal != null) ? signal
                : null;
    }

    /**
     * @return The first Supplier if available
     */
    public Supplier<T> getSupplier() {
        return (supplier != null) ? supplier
                : null;
    }

    /**
     * @return The timestamp of the signal (in milliseconds) or 0
     */
    public long getTime() {
        return (signal != null) ?
            (long) (signal.getTimestamp().getTime() * 1000) 
                : 0;
    }

    public boolean isDouble() {
        return isDouble;
    }

    public boolean isBoolean() {
        return isBoolean;
    }

    public static void addToGroupFloat(String name, String metaData, Data<?>... data) {
        for (Data<?> d : data) {
            groupFloatData.add(d);
        }

        if (isGroupFloatFirst) {
            groupFloatDataName += name;
            groupFloatDataMetaData += metaData;
            isGroupFloatFirst = false;
        } else {
            groupFloatDataName += " | " + name;
            groupFloatDataMetaData += " | " + metaData;
        }

        groupFloatDataValues = new float[groupFloatData.size()];
    }

    public static void addToGroupBoolean(String name, String metaData, Data<?>... data) {
        for (Data<?> d : data) {
            groupBooleanData.add(d);
        }

        if (isGroupBooleanFirst) {
            groupBooleanDataName += name;
            groupBooleanDataMetaData += metaData;
            isGroupBooleanFirst = false;
        } else {
            groupBooleanDataName += " | " + name;
            groupBooleanDataMetaData += " | " + metaData;
        }
        
        groupBooleanDataValues = new boolean[groupBooleanData.size()];
    }

    public static void addToGroupString(String name, String metaData, Data<?>... data) {
        for (Data<?> d : data) {
            groupStringData.add(d);
        }

        if (isGroupStringFirst) {
            groupStringDataName += name;
            groupStringDataMetaData += metaData;
            isGroupStringFirst = false;
        } else {
            groupStringDataName += " | " + name;
            groupStringDataMetaData += " | " + metaData;
        }
        
        groupStringDataValues = new String[groupStringData.size()];
    }

    public static float[] getGroupFloat(){
        for (int i = 0; i < groupFloatData.size(); i++) {
            groupFloatDataValues[i] = groupFloatData.get(i).getFloat();
        }
        return groupFloatDataValues;
    }

    public static boolean[] getGroupBoolean(){
        for (int i = 0; i < groupBooleanData.size(); i++) {
            groupBooleanDataValues[i] = groupBooleanData.get(i).getBoolean();
        }
        return groupBooleanDataValues;
    }

    public static String[] getGroupString(){
        for (int i = 0; i < groupStringData.size(); i++) {
            groupStringDataValues[i] = groupStringData.get(i).getString();
        }
        return groupStringDataValues;
    }

    public static String getGroupFloatName() {
        return groupFloatDataName;
    }

    public static String getGroupBooleanName() {
        return groupBooleanDataName;
    }

    public static String getGroupStringName() {
        return groupStringDataName;
    }

    public static String getGroupDoubleMetaData() {
        return groupFloatDataMetaData;
    }

    public static String getGroupBooleanMetaData() {
        return groupBooleanDataMetaData;
    }

    public static String getGroupStringMetaData() {
        return groupStringDataMetaData;
    }

    /**
     * Clears all static references and signals.
     */
    public static void clearAllSignals() {
        rioSignalsArray = new BaseStatusSignal[0];
        canivoreSignalsArray = new BaseStatusSignal[0];
        signalInstances.clear();
        supplierInstances.clear();
        groupFloatData.clear();
        groupBooleanData.clear();
        groupStringData.clear();
    }
}