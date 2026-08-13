package frc.demacia.utils.chassis;

public enum Mk5nConstants {
    R1(7.03),
    R2(6.03),
    R3(5.72);

    public static final double WHEEL_DIAMETER = 4 * 0.0254;
    public static final double STEER_GEAR_RATIO = 287d / 11d;
    public final double driveGearRatio;

    private Mk5nConstants(double driveGearRatio) {
        this.driveGearRatio = driveGearRatio;
    }
}
