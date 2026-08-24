package frc.demacia.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import frc.demacia.utils.log.LogManager;

/**
 * Linear interpolation lookup table.
 * 
 * <p>Stores and interpolates multi-dimensional data for feed-forward,
 * trajectory generation, or any mapping from input to multiple outputs.</p>
 * 
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Multi-output interpolation (1 input → N outputs)</li>
 *   <li>Automatic sorting by input value</li>
 *   <li>Extrapolation beyond table bounds</li>
 *   <li>Binary search for O(log n) lookup</li>
 * </ul>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // Shooter characterization: distance → (RPM, angle)
 * LookUpTable shooterTable = new LookUpTable(2);  // 2 outputs
 * shooterTable.add(1.0, 3000, 45);  // 1m: 3000 RPM, 45°
 * shooterTable.add(2.0, 3500, 50);  // 2m: 3500 RPM, 50°
 * shooterTable.add(3.0, 4000, 55);  // 3m: 4000 RPM, 55°
 * 
 * // Interpolate at 2.5m
 * double[] result = shooterTable.get(2.5);
 * double rpm = result[0];    // 3750
 * double angle = result[1];  // 52.5
 * </pre>
 * 
 * <p><b>Use Cases:</b></p>
 * <ul>
 *   <li>Shooter characterization (distance → speed, angle)</li>
 *   <li>Elevator feed-forward (height → voltage)</li>
 *   <li>Odometry correction (position → error correction)</li>
 *   <li>Auto path parameters (time → position, velocity, acceleration)</li>
 * </ul>
 */
public class LookUpTable {

    /**The internal table data as a list of double arrays. */
    private final List<double[]> table;

    /**The number of columns in the table (one more than the actual number of interpolated values). */
    private final int size;

    /**
     * Creates an empty lookup table.
     * 
     * @param size Number of output values per input
     * @throws IllegalArgumentException if size < 2
     */
    public LookUpTable(int size) throws IllegalArgumentException {
        if (size < 2) {
            throw new IllegalArgumentException("Size must be at least 2.");
        }
        table = new ArrayList<>();
        this.size = size + 1;
    }

    /**
     * Creates a lookup table from existing data.
     * 
     * <p>Data is automatically sorted by first column (input value).</p>
     * 
     * @param table 2D array where each row is [input, output1, output2, ...]
     * @throws IllegalArgumentException if rows have inconsistent lengths
     */
    public LookUpTable(double[][] table) throws IllegalArgumentException {
        this.table = new ArrayList<>();

        size = table[0].length;
        for (int i = 1; i < table.length; i++) {
            if (table[i].length != size) {
                throw new IllegalArgumentException("All rows in the table must have the same length.");
            }
        }

        sort(table);
        for (double[] row : table) {
            this.table.add(row);
        }
    }

    /**
     * sort an array
     * @param arr the wanted sorted array in double[][]
     */
    private void sort(double[][] arr){
        Arrays.sort(arr, new Comparator<double[]>() {
            @Override
            public int compare(double[] entry1, double[] entry2){
                return Double.compare(entry1[0], entry2[0]);
            }
        });
    }
    
    /**
     * Adds a data point to the table.
     * 
     * <p>Table is automatically sorted after insertion for fast lookup.</p>
     * 
     * @param row Input value followed by output values
     */
    public void add(double... row) {
        if (row.length != size) {
            LogManager.log("Size of new row (" + row.length + ") does not match row size of: " + size);
            return;
        }
        table.add(row);
        table.sort(Comparator.comparingDouble(r -> r[0]));
    }

    /**
     * Interpolates output values for a given input.
     * 
     * <p><b>Interpolation behavior:</b></p>
     * <ul>
     *   <li>Input within table range: Linear interpolation between nearest points</li>
     *   <li>Input below minimum: Returns minimum values (no extrapolation)</li>
     *   <li>Input above maximum: Returns maximum values (no extrapolation)</li>
     * </ul>
     * 
     * @param value Input value to interpolate at
     * @return Array of interpolated output values
     */
    public double[] get(double value) {
        
        if (table.isEmpty()) {
            LogManager.log("Cannot interpolate - table is empty. Use add() to add data first.");
            return null;
        }

        /* checks if the value is or bigger than the biggest point */
        if (value >= table.get(table.size() - 1)[0]) {
            double[] ans = new double[size -1];
            for (int j = 1; j < size; j++) {
                ans[j-1] = table.get(table.size() - 1)[j];
            }
            
            return ans;

        /* checks if the value is or smaller than the smallest point */
        } else if (value <= table.get(0)[0]) {
            double[] ans = new double[size - 1];
            for (int j = 1; j < size; j++) {
                ans[j - 1] = table.get(0)[j];
            }

            return ans;
        }

        /* checks what is the after and before points of the value */
        // int i = 0;
        // for (i = 0; i < table.size() && table.get(i)[0] < value; i++);

        int left = 0;
        int right = table.size() - 1;
        int mid = left;
        
        while (left < right) {
            mid = left + (right - left) / 2;

            if (table.get(mid)[0] < value) {
                left = mid + 1;
            } else if (table.get(mid)[0] > value) {
                right = mid;
            } else {
                left = mid;
                break;
            }
        }

        /* assign the after and before points */
        double[] after, before;
        after = table.get(left);
        before = table.get(left - 1);
        
        /* set all of the var to be at the right var for the value */
        double[] ans = new double[size - 1];
        for (int j = 1; j < size; j++) {
            if (after[0] == before[0]){
                ans[j-1] = after[j];
            } else{
                ans[j-1] = ((after[j] - before[j]) / (after[0] - before[0])) * value + (-((after[j] - before[j]) / (after[0] - before[0])) * after[0] + after[j]);
            }
        }
     
        /* returns the right var for the value as a arr of double */
        return ans;
    }
}