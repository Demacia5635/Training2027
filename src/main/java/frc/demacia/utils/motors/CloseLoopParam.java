package frc.demacia.utils.motors;

/**
 * Container class for closed-loop control parameters (PID + feed-forward).
 * 
 * <p>
 * Stores seven control parameters used for precise motor control:
 * </p>
 * <ul>
 * <li>kP, kI, kD - PID gains</li>
 * <li>kS - Static friction compensation</li>
 * <li>kV - Velocity feed-forward</li>
 * <li>kA - Acceleration feed-forward</li>
 * <li>kG - Gravity feed-forward</li>
 * </ul>
 * 
 * <p>
 * <b>Note:</b> This class calculates output in <i>volts</i>, not normalized
 * [-1, 1].
 * </p>
 */
public class CloseLoopParam {

    private double kP, kI, kD, kS, kV, kA, kG, kCos, kV2;

    /**
     * Default constructor. Initializes all parameters to zero.
     */
    public CloseLoopParam() {
    }

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
    CloseLoopParam(double kP, double kI, double kD, double kS, double kV, double kA, double kG, double kCos,
            double kV2) {
        set(kP, kI, kD, kS, kV, kA, kG, kCos, kV2);
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
        set(kP, kI, kD, 0, kf, 0, 0, 0, 0);
    }

    public void set(double kP, double kI, double kD, double kS, double kV, double kA, double kG, double kCos,
            double kV2) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kS = kS;
        this.kV = kV;
        this.kA = kA;
        this.kG = kG;
        this.kCos = kCos;
        this.kV2 = kV2;
    }

    public void set(CloseLoopParam other) {
        this.kP = other.kP;
        this.kI = other.kI;
        this.kD = other.kD;
        this.kS = other.kS;
        this.kV = other.kV;
        this.kA = other.kA;
        this.kG = other.kG;
        this.kCos = other.kCos;
        this.kV2 = other.kV2;
    }

    public double kP() {
        return kP;
    }

    public void setKP(double kP) {
        this.kP = kP;
    }

    public double kI() {
        return kI;
    }

    public void setKI(double kI) {
        this.kI = kI;
    }

    public double kD() {
        return kD;
    }

    public void setKD(double kD) {
        this.kD = kD;
    }

    public double kS() {
        return kS;
    }

    public void setKS(double kS) {
        this.kS = kS;
    }

    public double kV() {
        return kV;
    }

    public void setKV(double kV) {
        this.kV = kV;
    }

    public double kA() {
        return kA;
    }

    public void setKA(double kA) {
        this.kA = kA;
    }

    public double kG() {
        return kG;
    }

    public void setKG(double kG) {
        this.kG = kG;
    }

    public double kCos() {
        return kCos;
    }

    public void setKCos(double kCos) {
        this.kCos = kCos;
    }

    public double kV2() {
        return kV2;
    }

    public void setKV2(double kV2) {
        this.kV2 = kV2;
    }
}