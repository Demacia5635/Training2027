package frc.demacia.utils.geometry;

import static edu.wpi.first.units.Units.Radians;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.MathSharedStore;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.geometry.proto.Rotation2dProto;
import edu.wpi.first.math.geometry.struct.Rotation2dStruct;
import edu.wpi.first.math.interpolation.Interpolatable;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.util.protobuf.ProtobufSerializable;
import edu.wpi.first.util.struct.StructSerializable;
import java.util.Objects;

/**
 * A rotation in a 2D coordinate frame represented by a point on the unit circle (cosine and sine).
 *
 * <p>The angle is continuous, that is if a Rotation2d is constructed with 361 degrees, it will
 * return 361 degrees. This allows algorithms that wouldn't want to see a discontinuity in the
 * rotations as it sweeps past from 360 to 0 on the second time around.
 *
 * <p><b>NOTE: This is a MUTABLE implementation. Methods like plus(), minus(), rotateBy() modify the current object!</b>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Rotation2dDemacia
    implements Interpolatable<Rotation2dDemacia>, ProtobufSerializable, StructSerializable {
  
  private double m_value;
  private double m_cos;
  private double m_sin;

  /** Constructs a Rotation2d with a default angle of 0 degrees. */
  public Rotation2dDemacia() {
    m_value = 0.0;
    m_cos = 1.0;
    m_sin = 0.0;
  }

  /**
   * Constructs a Rotation2d with the given radian value.
   *
   * @param value The value of the angle in radians.
   */
  @JsonCreator
  public Rotation2dDemacia(@JsonProperty(required = true, value = "radians") double value) {
    m_value = value;
    m_cos = Math.cos(value);
    m_sin = Math.sin(value);
  }

  /**
   * Constructs a Rotation2d with the given x and y (cosine and sine) components.
   *
   * @param x The x component or cosine of the rotation.
   * @param y The y component or sine of the rotation.
   */
  public Rotation2dDemacia(double x, double y) {
    double magnitude = Math.hypot(x, y);
    if (magnitude > 1e-6) {
      m_cos = x / magnitude;
      m_sin = y / magnitude;
    } else {
      m_cos = 1.0;
      m_sin = 0.0;
      MathSharedStore.reportError(
          "x and y components of Rotation2d are zero\n", Thread.currentThread().getStackTrace());
    }
    m_value = Math.atan2(m_sin, m_cos);
  }

  /**
   * Constructs a Rotation2d with the given angle.
   *
   * @param angle The angle of the rotation.
   */
  public Rotation2dDemacia(Angle angle) {
    this(angle.in(Radians));
  }

  /**
   * Constructs a Rotation2d from a rotation matrix.
   *
   * @param rotationMatrix The rotation matrix.
   * @throws IllegalArgumentException if the rotation matrix isn't special orthogonal.
   */
  public Rotation2dDemacia(Matrix<N2, N2> rotationMatrix) {
    final var R = rotationMatrix;

    // Require that the rotation matrix is special orthogonal. This is true if
    // the matrix is orthogonal (RRᵀ = I) and normalized (determinant is 1).
    if (R.times(R.transpose()).minus(Matrix.eye(Nat.N2())).normF() > 1e-9) {
      var msg = "Rotation matrix isn't orthogonal\n\nR =\n" + R.getStorage().toString() + '\n';
      MathSharedStore.reportError(msg, Thread.currentThread().getStackTrace());
      throw new IllegalArgumentException(msg);
    }
    if (Math.abs(R.det() - 1.0) > 1e-9) {
      var msg =
          "Rotation matrix is orthogonal but not special orthogonal\n\nR =\n"
              + R.getStorage().toString()
              + '\n';
      MathSharedStore.reportError(msg, Thread.currentThread().getStackTrace());
      throw new IllegalArgumentException(msg);
    }

    // R = [cosθ  −sinθ]
    //     [sinθ   cosθ]
    m_cos = R.get(0, 0);
    m_sin = R.get(1, 0);

    m_value = Math.atan2(m_sin, m_cos);
  }

  /**
   * Updates this Rotation2d to match the provided one without memory allocation
   * and without strict trigonometry recalculations.
   *
   * @param other The rotation to copy from.
   * @return This object.
   */
  public Rotation2dDemacia set(Rotation2dDemacia other) {
    m_value = other.m_value;
    m_cos = other.m_cos;
    m_sin = other.m_sin;
    return this;
  }

  /**
   * Constructs and returns a Rotation2d with the given radian value.
   *
   * @param radians The value of the angle in radians.
   * @return The rotation object with the desired angle value.
   */
  public static Rotation2dDemacia fromRadians(double radians) {
    return new Rotation2dDemacia(radians);
  }

  /**
   * Constructs and returns a Rotation2d with the given degree value.
   *
   * @param degrees The value of the angle in degrees.
   * @return The rotation object with the desired angle value.
   */
  public static Rotation2dDemacia fromDegrees(double degrees) {
    return new Rotation2dDemacia(Math.toRadians(degrees));
  }

  /**
   * Constructs and returns a Rotation2d with the given number of rotations.
   *
   * @param rotations The value of the angle in rotations.
   * @return The rotation object with the desired angle value.
   */
  public static Rotation2dDemacia fromRotations(double rotations) {
    return new Rotation2dDemacia(Units.rotationsToRadians(rotations));
  }

  /**
   * Adds two rotations together, with the result being bounded between -π and π.
   * <b>Modifies this object.</b>
   *
   * <p>For example, <code>Rotation2d.fromDegrees(30).plus(Rotation2d.fromDegrees(60))</code> equals
   * <code>Rotation2d(Math.PI/2.0)</code>
   *
   * @param other The rotation to add.
   * @return This object (modified).
   */
  public Rotation2dDemacia plus(Rotation2dDemacia other) {
    return rotateBy(other);
  }

  /**
   * Subtracts the new rotation from the current rotation.
   * <b>Modifies this object.</b>
   *
   * <p>For example, <code>Rotation2d.fromDegrees(10).minus(Rotation2d.fromDegrees(100))</code>
   * equals <code>Rotation2d(-Math.PI/2.0)</code>
   *
   * @param other The rotation to subtract.
   * @return This object (modified).
   */
  public Rotation2dDemacia minus(Rotation2dDemacia other) {
    double x = m_cos * other.m_cos + m_sin * other.m_sin;
    double y = m_sin * other.m_cos - m_cos * other.m_sin;
    double magnitude = Math.hypot(x, y);
    if (magnitude > 1e-6) {
      m_cos = x / magnitude;
      m_sin = y / magnitude;
    } else {
      m_cos = 1.0;
      m_sin = 0.0;
      MathSharedStore.reportError(
          "x and y components of Rotation2d are zero\n", Thread.currentThread().getStackTrace());
    }
    m_value = Math.atan2(m_sin, m_cos);
    return this;
  }

  /**
   * Takes the inverse of the current rotation. This is simply the negative of the current angular value.
   * <b>Modifies this object.</b>
   *
   * @return This object (modified).
   */
  public Rotation2dDemacia unaryMinus() {
    m_value = -m_value;
    m_sin = -m_sin;
    return this;
  }

  /**
   * Multiplies the current rotation by a scalar.
   * <b>Modifies this object.</b>
   *
   * @param scalar The scalar.
   * @return This object (modified).
   */
  public Rotation2dDemacia times(double scalar) {
    m_value = m_value * scalar;
    m_cos = Math.cos(m_value * scalar);
    m_sin = Math.sin(m_value * scalar);
    return this;
  }

  /**
   * Divides the current rotation by a scalar.
   * <b>Modifies this object.</b>
   *
   * @param scalar The scalar.
   * @return This object (modified).
   */
  public Rotation2dDemacia div(double scalar) {
    return times(1.0 / scalar);
  }

  /**
   * Adds the new rotation to the current rotation using a rotation matrix.
   * <b>Modifies this object.</b>
   *
   * <p>The matrix multiplication is as follows:
   *
   * <pre>
   * [cos_new]   [other.cos, -other.sin][cos]
   * [sin_new] = [other.sin,  other.cos][sin]
   * value_new = atan2(sin_new, cos_new)
   * </pre>
   *
   * @param other The rotation to rotate by.
   * @return This object (modified).
   */
  public Rotation2dDemacia rotateBy(Rotation2dDemacia other) {
    double x = m_cos * other.m_cos - m_sin * other.m_sin;
    double y = m_cos * other.m_sin + m_sin * other.m_cos;
    double magnitude = Math.hypot(x, y);
    if (magnitude > 1e-6) {
      m_cos = x / magnitude;
      m_sin = y / magnitude;
    } else {
      m_cos = 1.0;
      m_sin = 0.0;
      MathSharedStore.reportError(
          "x and y components of Rotation2d are zero\n", Thread.currentThread().getStackTrace());
    }
    m_value = Math.atan2(m_sin, m_cos);
    return this;
  }

  /**
   * Returns matrix representation of this rotation.
   *
   * @return Matrix representation of this rotation.
   */
  public Matrix<N2, N2> toMatrix() {
    // R = [cosθ  −sinθ]
    //     [sinθ   cosθ]
    return MatBuilder.fill(Nat.N2(), Nat.N2(), m_cos, -m_sin, m_sin, m_cos);
  }

  /**
   * Returns the measure of the Rotation2d.
   *
   * @return The measure of the Rotation2d.
   */
  public Angle getMeasure() {
    return Radians.of(getRadians());
  }

  /**
   * Returns the radian value of the Rotation2d.
   *
   * @return The radian value of the Rotation2d.
   * @see edu.wpi.first.math.MathUtil#angleModulus(double) to constrain the angle within (-π, π]
   */
  @JsonProperty
  public double getRadians() {
    return m_value;
  }

  /**
   * Returns the degree value of the Rotation2d.
   *
   * @return The degree value of the Rotation2d.
   * @see edu.wpi.first.math.MathUtil#inputModulus(double, double, double) to constrain the angle
   * within (-180, 180]
   */
  public double getDegrees() {
    return Math.toDegrees(m_value);
  }

  /**
   * Returns the number of rotations of the Rotation2d.
   *
   * @return The number of rotations of the Rotation2d.
   */
  public double getRotations() {
    return Units.radiansToRotations(m_value);
  }

  /**
   * Returns the cosine of the Rotation2d.
   *
   * @return The cosine of the Rotation2d.
   */
  public double getCos() {
    return m_cos;
  }

  /**
   * Returns the sine of the Rotation2d.
   *
   * @return The sine of the Rotation2d.
   */
  public double getSin() {
    return m_sin;
  }

  /**
   * Returns the tangent of the Rotation2d.
   *
   * @return The tangent of the Rotation2d.
   */
  public double getTan() {
    return m_sin / m_cos;
  }

  @Override
  public String toString() {
    return String.format("Rotation2d(Rads: %.2f, Deg: %.2f)", m_value, Math.toDegrees(m_value));
  }

  /**
   * Checks equality between this Rotation2d and another object.
   *
   * @param obj The other object.
   * @return Whether the two objects are equal or not.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Rotation2dDemacia other
        && Math.hypot(m_cos - other.m_cos, m_sin - other.m_sin) < 1E-9;
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_value);
  }

  @Override
  public Rotation2dDemacia interpolate(Rotation2dDemacia endValue, double t) {
    return new Rotation2dDemacia(m_value).plus(((new Rotation2dDemacia(endValue.m_value)).minus(this).times(MathUtil.clamp(t, 0, 1))));
  }

  /** Rotation2d protobuf for serialization. */
  public static final Rotation2dProto proto = new Rotation2dProto();

  /** Rotation2d struct for serialization. */
  public static final Rotation2dStruct struct = new Rotation2dStruct();
}