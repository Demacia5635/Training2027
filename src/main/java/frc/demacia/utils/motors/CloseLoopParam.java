package frc.demacia.utils.motors;

/**
 * Container class for closed-loop control parameters (PID + feed-forward).
 * 
 * <p>Stores seven control parameters used for precise motor control:</p>
 * <ul>
 *   <li>kP, kI, kD - PID gains</li>
 *   <li>kS - Static friction compensation</li>
 *   <li>kV - Velocity feed-forward</li>
 *   <li>kA - Acceleration feed-forward</li>
 *   <li>kG - Gravity feed-forward</li>
 * </ul>
 * 
 * <p><b>Note:</b> This class calculates output in <i>volts</i>, not normalized [-1, 1].</p>
 */
public class CloseLoopParam {

    public static String[] PARAMETER_NAMES = {"kP", "kI", "kD", "kS", "kV", "kA", "kG"};

    private double[] parameters = {0,0,0,0,0,0,0};

    /**
     * Default constructor. Initializes all parameters to zero.
     */
    CloseLoopParam() {}

    /**
     * Constructor with all seven control parameters.
     * 
     * @param kP Proportional gain
     * @param kI Integral gain
     * @param kD Derivative gain
     * @param kS Static friction feed-forward (volts)
     * @param kV Velocity feed-forward (volts per unit/sec)
     * @param kA Acceleration feed-forward (volts per unit/sec²)
     * @param kG Gravity feed-forward (volts)
     */
    CloseLoopParam(double kP, double kI, double kD, double kS, double kV, double kA, double kG) {
        set(kP,kI,kD,kS,kV,kA,kG);
    }

    /**
     * Simplified constructor with feed-forward (legacy).
     * 
     * @param kP Proportional gain
     * @param kI Integral gain
     * @param kD Derivative gain
     * @param kf Feed-forward gain (mapped to kV)
     */
    CloseLoopParam(double kP, double kI, double kD, double kf) {
        set(kP,kI,kD,0,kf,0,0);
    }

    public void set (double kP, double kI, double kD, double kS, double kV, double kA, double kG) {
        parameters[0] = kP;
        parameters[1] = kI;
        parameters[2] = kD;
        parameters[3] = kS;
        parameters[4] = kV;
        parameters[5] = kA;
        parameters[6] = kG;
    }

    public void set(CloseLoopParam other) {
        System.arraycopy(other.parameters, 0, this.parameters, 0, 7);
    }

    public double[] toArray() {
        return parameters;
    }

    public double kP() {
        return parameters[0];
    }
    
    public void setKP(double kP) {
        parameters[0] = kP;
    }

    public double kI() {
        return parameters[1];
    }
    
    public void setKI(double kI) {
        parameters[1] = kI;
    }

    public double kD() {
        return parameters[2];
    }
    
    public void setKD(double kD) {
        parameters[2] = kD;
    }

    public double kS() {
        return parameters[3];
    }
    
    public void setKS(double kS) {
        parameters[3] = kS;
    }

    public double kV() {
        return parameters[4];
    }
    
    public void setKV(double kV) {
        parameters[4] = kV;
    }

    public double kA() {
        return parameters[5];
    }
    
    public void setKA(double kA) {
        parameters[5] = kA;
    }

    public double kG() {
        return parameters[6];
    }
    
    public void setKG(double kG) {
        parameters[6] = kG;
    }
}