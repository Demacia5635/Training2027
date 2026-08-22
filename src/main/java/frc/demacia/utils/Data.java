package frc.demacia.utils;


import java.util.ArrayList;
import java.util.Arrays;
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
    private static ArrayList<BaseStatusSignal> rioSignals = new ArrayList<>();
    private static ArrayList<BaseStatusSignal> canivoreSignals = new ArrayList<>();
    /** List of all Data instances based on Signals */
    private static final ArrayList<Data<?>> signalInstances = new ArrayList<>();
    /** List of all Data instances based on Suppliers */
    private static final ArrayList<Data<?>> supplierInstances = new ArrayList<>();

    private StatusSignal<T>[] signal;
    private Supplier<T>[] supplier;
    private int length;

    private boolean isDouble = false;
    private boolean isBoolean = false;
    private boolean isArray = false;

    private boolean changed = true;

    // Cached primitive arrays to avoid auto-boxing and garbage collection
    private double[] doubleArrayValues;
    private float[] floatArrayValues;
    private boolean[] booleanArrayValues;
    private String[] stringArrayValues;

    /**
     * Creates a new Data object from Phoenix 6 StatusSignals.
     * 
     * @param signal Variable arguments of StatusSignals
     */
    public Data(StatusSignal<T>[] signal, boolean isRio) {
        this.signal = signal;
        length = signal.length;

        detectTypeFromSignal();
        allocateCachedArrays();

        registerSignal();
        if (isRio) {
            rioSignals.addAll(Arrays.asList(signal));
        } else {
            canivoreSignals.addAll(Arrays.asList(signal));
        }
        refresh();
    }

    /**
     * Creates a new Data object from Java Suppliers.
     * 
     * @param supplier Variable arguments of Suppliers
     */
    @SuppressWarnings("unchecked")
    public Data(Supplier<T>... supplier) {
        this.supplier = supplier;
        length = supplier.length;

        detectTypeFromSupplier();
        allocateCachedArrays();

        registerSupplier();
        refresh();
    }

    /** Registers this instance's signals to the static master list */
    private void registerSignal() {
        signalInstances.add(this);
    }

    // /**
    //  * Adds new signals to the static master array.
    //  * 
    //  * @param newSignals The signals to add
    //  */
    // private void registerSignal(BaseStatusSignal[] newSignals) {
    //     int oldLength = signals.length;
    //     int newLength = oldLength + newSignals.length;
    //     BaseStatusSignal[] combined = new BaseStatusSignal[newLength];
    //     System.arraycopy(signals, 0, combined, 0, oldLength);
    //     System.arraycopy(newSignals, 0, combined, oldLength, newSignals.length);
    //     signals = combined;
    // }

    /** Registers this instance to the static supplier list */
    private void registerSupplier() {
        supplierInstances.add(this);
    }

    /** Detects the data type based on the first signal's value */
    private void detectTypeFromSignal() {
        if (length == 0)
            return;

        if (length > 1)
            isArray = true;

        T value = signal[0].getValue();

        if (value instanceof Number) {
            isDouble = true;
        } else if (value instanceof Boolean) {
            isBoolean = true;
        } else {
            try {
                signal[0].getValueAsDouble();
                isDouble = true;
            } catch (Exception e) {
                isDouble = false;
            }
        }
    }

    /** Detects the data type based on the first supplier's value */
    private void detectTypeFromSupplier() {
        if (length == 0)
            return;

        if (length > 1)
            isArray = true;

        T value = supplier[0].get();

        if (value instanceof Number) {
            isDouble = true;
        } else if (value instanceof Boolean) {
            isBoolean = true;
        }
    }

    /** Allocates the primitive arrays based on the detected type and length */
    private void allocateCachedArrays() {
        if (length > 0) {
            if (isDouble) {
                doubleArrayValues = new double[length];
                floatArrayValues = new float[length];
            } else if (isBoolean) {
                booleanArrayValues = new boolean[length];
            } else {
                stringArrayValues = new String[length];
            }
        }
    }

    /**
     * Refreshes the local data from the source.
     * If using signals, assumes the master refresh has already been called.
     */
    public void refresh() {
        if (signal != null) {
            StatusSignal.refreshAll(signal);
            updateSignalValue();
        } else {
            refreshSupplier();
        }
    }

    public static void addSignals(boolean isRio, StatusSignal<?>... signals) {
        if (isRio) 
            rioSignals.addAll(Arrays.asList(signals));
        else
            canivoreSignals.addAll(Arrays.asList(signals));
    }

    /**
     * Updates the internal primitive arrays from the signals.
     * Sets the 'changed' flag if values have changed.
     */
    private void updateSignalValue() {
        changed = false;
        if (isDouble) {
            if (doubleArrayValues == null)
                return;
            for (int i = 0; i < length; i++) {
                if (doubleArrayValues[i] != signal[i].getValueAsDouble()) {
                    changed = true;
                    doubleArrayValues[i] = signal[i].getValueAsDouble();
                }
                if (floatArrayValues[i] != (float) signal[i].getValueAsDouble()) {
                    changed = true;
                    floatArrayValues[i] = (float) signal[i].getValueAsDouble();
                }
            }
        } else if (isBoolean) {
            if (booleanArrayValues == null)
                return;
            for (int i = 0; i < length; i++) {
                if (booleanArrayValues[i] != (Boolean) signal[i].getValue()) {
                    changed = true;
                    booleanArrayValues[i] = (Boolean) signal[i].getValue();
                }
            }
        } else {
            if (stringArrayValues == null)
                return;
            for (int i = 0; i < length; i++) {
                if (!Objects.equals(stringArrayValues[i],
                        (signal[i].getValue() == null) ? "null" : signal[i].getValue().toString())) {
                    changed = true;
                    stringArrayValues[i] = (signal[i].getValue() == null) ? "null" : signal[i].getValue().toString();
                }
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
            if (doubleArrayValues == null)
                return;
            for (int i = 0; i < length; i++) {
                double newVal = ((Number) supplier[i].get()).doubleValue();
                if (doubleArrayValues[i] != newVal) {
                    changed = true;
                    doubleArrayValues[i] = newVal;
                }
                float newFloatVal = ((Number) supplier[i].get()).floatValue();
                if (floatArrayValues[i] != newFloatVal) {
                    changed = true;
                    floatArrayValues[i] = newFloatVal;
                }
            }
        } else if (isBoolean) {
            if (booleanArrayValues == null)
                return;
            for (int i = 0; i < length; i++) {
                boolean newVal = (Boolean) supplier[i].get();
                if (booleanArrayValues[i] != newVal) {
                    changed = true;
                    booleanArrayValues[i] = newVal;
                }
            }
        } else {
            if (stringArrayValues == null)
                return;
            for (int i = 0; i < length; i++) {
                String newVal = (supplier[i].get() == null) ? "null" : supplier[i].get().toString();
                if (!Objects.equals(stringArrayValues[i], newVal)) {
                    changed = true;
                    stringArrayValues[i] = newVal;
                }
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
        if (rioSignals.size() > 0) {
            BaseStatusSignal.refreshAll(rioSignals);
        }
        if (canivoreSignals.size() > 0) {
            BaseStatusSignal.refreshAll(canivoreSignals);
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
        return length == 0 ? 0
                : isDouble ? doubleArrayValues[0]
                        : isBoolean ? booleanArrayValues[0] ? 1.0 : 0.0
                                : 0;
    }

    /**
     * @return The array of values as doubles
     */
    public double[] getDoubleArray() {
        if (length == 0) {
            return new double[0];
        } else if (isDouble) {
            return doubleArrayValues;
        } else if (isBoolean) {
            double[] doubles = new double[length];
            for (int i = 0; i < length; i++) {
                doubles[i] = booleanArrayValues[i] ? 1.0 : 0.0;
            }
            return doubles;
        } else {
            return new double[0];
        }
    }

    /**
     * @return The value as a float (1f for true if boolean)
     */
    public float getFloat() {
        return length == 0 ? 0f
                : isDouble ? floatArrayValues[0]
                        : isBoolean ? booleanArrayValues[0] ? 1f : 0f
                                : 0f;
    }

    /**
     * @return The array of values as floats
     */
    public float[] getFloatArray() {
        if (length == 0) {
            return new float[0];
        } else if (isDouble) {
            return floatArrayValues;
        } else if (isBoolean) {
            float[] floats = new float[length];
            for (int i = 0; i < length; i++) {
                floats[i] = booleanArrayValues[i] ? 1f : 0f;
            }
            return floats;
        } else {
            return new float[0];
        }
    }

    /**
     * @return The value as a boolean (true for 1 if number)
     */
    public boolean getBoolean() {
        return length == 0 ? false
                : isBoolean ? booleanArrayValues[0]
                        : isDouble ? doubleArrayValues[0] == 1
                                : false;
    }

    /**
     * @return The array of values as booleans
     */
    public boolean[] getBooleanArray() {
        if (length == 0) {
            return new boolean[0];
        } else if (isBoolean) {
            return booleanArrayValues;
        } else if (isDouble) {
            boolean[] bools = new boolean[length];
            for (int i = 0; i < length; i++) {
                bools[i] = (doubleArrayValues[i] == 1);
            }
            return bools;
        } else {
            return new boolean[0];
        }
    }

    /**
     * @return The value as a string
     */
    public String getString() {
        return length == 0 ? ""
                : isDouble ? ((Double) doubleArrayValues[0]).toString()
                        : isBoolean ? ((Boolean) booleanArrayValues[0]).toString()
                                : stringArrayValues[0];
    }

    /**
     * @return The array of values as strings
     */
    public String[] getStringArray() {
        if (length == 0) {
            return new String[0];
        } else if (isDouble) {
            String[] strs = new String[length];
            for (int i = 0; i < length; i++) {
                strs[i] = ((Double) doubleArrayValues[i]).toString();
            }
            return strs;
        } else if (isBoolean) {
            String[] strs = new String[length];
            for (int i = 0; i < length; i++) {
                strs[i] = ((Boolean) booleanArrayValues[i]).toString();
            }
            return strs;
        } else {
            return stringArrayValues;
        }
    }

    /**
     * @return The first StatusSignal if available
     */
    public StatusSignal<T> getSignal() {
        return (signal != null && signal.length > 0) ? signal[0]
                : null;
    }

    /**
     * @return The StatusSignal array if available
     */
    public StatusSignal<T>[] getSignalArray() {
        return (signal != null) ? signal
                : null;
    }

    /**
     * @return The first Supplier if available
     */
    public Supplier<T> getSupplier() {
        return (supplier != null && supplier.length > 0) ? supplier[0]
                : null;
    }

    /**
     * @return The Supplier array if available
     */
    public Supplier<T>[] getSupplierArray() {
        return (supplier != null) ? supplier
                : null;
    }

    /**
     * @return The timestamp of the signal (in milliseconds) or 0
     */
    public long getTime() {
        if (signal != null)
            return (long) (signal[0].getTimestamp().getTime() * 1000);
        return 0;
    }

    public boolean isDouble() {
        return isDouble;
    }

    public boolean isBoolean() {
        return isBoolean;
    }

    public boolean isArray() {
        return isArray;
    }

    // /**
    //  * Removes this instance from the static management lists.
    //  * Also rebuilds the static signal array to remove these signals.
    //  */
    // public void cleanup() {
    //     signalInstances.remove(this);
    //     supplierInstances.remove(this);

    //     if (signal != null) {
    //         int count = 0;
    //         for (BaseStatusSignal s : rioSignals) {
    //             boolean isMine = false;
    //             for (StatusSignal<T> mySignal : signal) {
    //                 if (s == mySignal) {
    //                     isMine = true;
    //                     break;
    //                 }
    //             }
    //             if (!isMine)
    //                 count++;
    //         }

    //         BaseStatusSignal[] newSignalsArray = new BaseStatusSignal[count];
    //         int index = 0;

    //         for (BaseStatusSignal s : rioSignals) {
    //             boolean isMine = false;
    //             for (StatusSignal<T> mySignal : signal) {
    //                 if (s == mySignal) {
    //                     isMine = true;
    //                     break;
    //                 }
    //             }
    //             if (!isMine) {
    //                 newSignalsArray[index++] = s;
    //             }
    //         }

    //         rioSignals = newSignalsArray;
    //     }

    //     signal = null;
    //     supplier = null;
    //     doubleArrayValues = null;
    //     floatArrayValues = null;
    //     booleanArrayValues = null;
    //     stringArrayValues = null;
    // }

    /**
     * Clears all static references and signals.
     */
    public static void clearAllSignals() {
        rioSignals = new ArrayList<>();
        canivoreSignals = new ArrayList<>();
        signalInstances.clear();
        supplierInstances.clear();
    }

    /**
     * Expands the current Data object by adding more StatusSignals.
     * 
     * @param newSignals The new signals to append
     */
    @SuppressWarnings("unchecked")
    public void expandWithSignals(StatusSignal<T>[] newSignals, boolean isRio) {
        if (newSignals == null || newSignals.length == 0)
            return;

        int newLength = length + newSignals.length;
        StatusSignal<T>[] expandedSignals = new StatusSignal[newLength];
        if (signal != null) {
            System.arraycopy(signal, 0, expandedSignals, 0, length);
        }
        System.arraycopy(newSignals, 0, expandedSignals, length, newSignals.length);
        signal = expandedSignals;

        length = signal.length;

        if (length > 1)
            isArray = true;

        detectTypeFromSignal();
        allocateCachedArrays();

        refresh();
    }

    public void expandWithSignals(StatusSignal<T>[] newSignals) {expandWithSignals(newSignals, true);}

    /**
     * Expands the current Data object by adding more Suppliers.
     * 
     * @param newSuppliers The new suppliers to append
     */
    @SuppressWarnings("unchecked")
    public void expandWithSuppliers(Supplier<T>[] newSuppliers) {

        if (newSuppliers == null || newSuppliers.length == 0)
            return;
        int newLength = length + newSuppliers.length;
        Supplier<T>[] expandedSuppliers = new Supplier[newLength];
        if (supplier != null) {
            System.arraycopy(supplier, 0, expandedSuppliers, 0, length);
        }
        System.arraycopy(newSuppliers, 0, expandedSuppliers, length, newSuppliers.length);
        supplier = expandedSuppliers;

        length = expandedSuppliers.length;

        if (length > 1)
            isArray = true;

        detectTypeFromSupplier();
        allocateCachedArrays();

        refresh();
    }
}