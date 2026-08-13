package frc.demacia.utils.geometry;

import static edu.wpi.first.units.Units.Meters;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.geometry.proto.Translation2dProto;
import edu.wpi.first.math.geometry.struct.Translation2dStruct;
import edu.wpi.first.math.interpolation.Interpolatable;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.util.protobuf.ProtobufSerializable;
import edu.wpi.first.util.struct.StructSerializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * Represents a translation in 2D space. This object can be used to represent a point or a vector.
 *
 * <p>This assumes that you are using conventional mathematical axes. When the robot is at the
 * origin facing in the positive X direction, forward is positive X and left is positive Y.
 *
 * <p><b>NOTE: This is a MUTABLE implementation. Methods like plus(), minus(), rotateBy() modify the current object!</b>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Translation2dDemacia
    implements Interpolatable<Translation2dDemacia>, ProtobufSerializable, StructSerializable {

  private double m_x;
  private double m_y;

  /** Constructs a Translation2d with X and Y components equal to zero. */
  public Translation2dDemacia() {
    this(0.0, 0.0);
  }

  /**
   * Constructs a Translation2d with the X and Y components equal to the provided values.
   *
   * @param x The x component of the translation.
   * @param y The y component of the translation.
   */
  @JsonCreator
  public Translation2dDemacia(
      @JsonProperty(required = true, value = "x") double x,
      @JsonProperty(required = true, value = "y") double y) {
    m_x = x;
    m_y = y;
  }

  /**
   * Constructs a Translation2d with the provided distance and angle. This is essentially converting
   * from polar coordinates to Cartesian coordinates.
   *
   * @param distance The distance from the origin to the end of the translation.
   * @param angle The angle between the x-axis and the translation vector.
   */
  public Translation2dDemacia(double distance, Rotation2dDemacia angle) {
    m_x = distance * angle.getCos();
    m_y = distance * angle.getSin();
  }

  /**
   * Constructs a Translation2d with the X and Y components equal to the provided values. The X and
   * Y components will be converted to and tracked as meters.
   *
   * @param x The x component of the translation.
   * @param y The y component of the translation.
   */
  public Translation2dDemacia(Distance x, Distance y) {
    this(x.in(Meters), y.in(Meters));
  }

  /**
   * Constructs a Translation2d from a 2D translation vector. The values are assumed to be in
   * meters.
   *
   * @param vector The translation vector.
   */
  public Translation2dDemacia(Vector<N2> vector) {
    this(vector.get(0), vector.get(1));
  }

  /**
   * Updates this Translation2d to match the provided one without memory allocation
   * and without strict trigonometry recalculations.
   *
   * @param other The Translation2d to copy from.
   * @return This object.
   */
  public Translation2dDemacia set(Translation2dDemacia other) {
    m_x = other.m_x;
    m_y = other.m_y;
    return this;
  }

  /**
   * Updates this Translation2d to match the provided one without memory allocation
   * and without strict trigonometry recalculations.
   *
   * @param x The x to set.
   * @param y The y to set.
   * @return This object.
   */
  public Translation2dDemacia set(double x, double y) {
    m_x = x;
    m_y = y;
    return this;
  }

  /**
   * Calculates the distance between two translations in 2D space.
   *
   *
   * @param other The translation to compute the distance to.
   * @return The distance between the two translations.
   */
  public double getDistance(Translation2dDemacia other) {
    return Math.hypot(other.m_x - m_x, other.m_y - m_y);
  }

  /**
   * Calculates the square of the distance between two translations in 2D space. This is equivalent
   * to squaring the result of {@link #getDistance(Translation2dDemacia)}, but avoids computing a square
   * root.
   *
   *
   * @param other The translation to compute the squared distance to.
   * @return The square of the distance between the two translations, in square meters.
   */
  public double getSquaredDistance(Translation2dDemacia other) {
    double dx = other.m_x - m_x;
    double dy = other.m_y - m_y;
    return dx * dx + dy * dy;
  }

  /**
   * Returns the X component of the translation.
   *
   * @return The X component of the translation.
   */
  @JsonProperty
  public double getX() {
    return m_x;
  }

  /**
   * Returns the Y component of the translation.
   *
   * @return The Y component of the translation.
   */
  @JsonProperty
  public double getY() {
    return m_y;
  }

  /**
   * Returns the X component of the translation in a measure.
   *
   * @return The x component of the translation in a measure.
   */
  public Distance getMeasureX() {
    return Meters.of(m_x);
  }

  /**
   * Returns the Y component of the translation in a measure.
   *
   * @return The y component of the translation in a measure.
   */
  public Distance getMeasureY() {
    return Meters.of(m_y);
  }

  /**
   * Returns a 2D translation vector representation of this translation.
   *
   * @return A 2D translation vector representation of this translation.
   */
  public Vector<N2> toVector() {
    return VecBuilder.fill(m_x, m_y);
  }

  /**
   * Returns the norm, or distance from the origin to the translation.
   *
   * @return The norm of the translation.
   */
  public double getNorm() {
    return Math.hypot(m_x, m_y);
  }

  /**
   * Returns the squared norm, or squared distance from the origin to the translation. This is
   * equivalent to squaring the result of {@link #getNorm()}, but avoids computing a square root.
   *
   * @return The squared norm of the translation, in square meters.
   */
  public double getSquaredNorm() {
    return m_x * m_x + m_y * m_y;
  }

  /**
   * Returns the angle this translation forms with the positive X axis.
   *
   * @return The angle of the translation
   */
  public Rotation2dDemacia getAngle() {
    return new Rotation2dDemacia(m_x, m_y);
  }

  /**
   * Applies a rotation to the translation in 2D space.
   * <b>Modifies this object.</b>
   *
   * <p>This multiplies the translation vector by a counterclockwise rotation matrix of the given
   * angle.
   *
   * <pre>
   * [x_new]   [other.cos, -other.sin][x]
   * [y_new] = [other.sin,  other.cos][y]
   * </pre>
   *
   * <p>For example, rotating a Translation2d of &lt;2, 0&gt; by 90 degrees will return a
   * Translation2d of &lt;0, 2&gt;.
   *
   * @param other The rotation to rotate the translation by.
   * @return This object (modified).
   */
  public Translation2dDemacia rotateBy(Rotation2dDemacia other) {
    double x = m_x;
    m_x = x * other.getCos() - m_y * other.getSin();
    m_y = x * other.getSin() + m_y * other.getCos();
    return this;
  }

  /**
   * Rotates this translation around another translation in 2D space.
   * <b>Modifies this object.</b>
   *
   * <pre>
   * [x_new]   [rot.cos, -rot.sin][x - other.x]   [other.x]
   * [y_new] = [rot.sin,  rot.cos][y - other.y] + [other.y]
   * </pre>
   *
   * @param other The other translation to rotate around.
   * @param rot The rotation to rotate the translation by.
   * @return This object (modified).
   */
  public Translation2dDemacia rotateAround(Translation2dDemacia other, Rotation2dDemacia rot) {
    double x = m_x;
    m_x = (m_x - other.getX()) * rot.getCos() - (m_y - other.getY()) * rot.getSin() + other.getX();
    m_y = (x - other.getX()) * rot.getSin() + (m_y - other.getY()) * rot.getCos() + other.getY();
    return this;
  }

  /**
   * Computes the dot product between this translation and another translation in 2D space.
   *
   *
   * @param other The translation to compute the dot product with.
   * @return The dot product between the two translations, in square meters.
   */
  public double dot(Translation2dDemacia other) {
    return m_x * other.m_x + m_y * other.m_y;
  }

  /**
   * Computes the cross product between this translation and another translation in 2D space.
   *
   *
   * @param other The translation to compute the cross product with.
   * @return The cross product between the two translations, in square meters.
   */
  public double cross(Translation2dDemacia other) {
    return m_x * other.m_y - m_y * other.m_x;
  }

  /**
   * Adds the other translation to this translation in 2D space.
   * <b>Modifies this object.</b>
   *
   * <p>For example, Translation3d(1.0, 2.5) + Translation3d(2.0, 5.5) = Translation3d{3.0, 8.0).
   *
   * @param other The translation to add.
   * @return This object (modified).
   */
  public Translation2dDemacia plus(Translation2dDemacia other) {
    m_x = m_x + other.m_x;
    m_y = m_y + other.m_y;
    return this;
  }

  /**
   * Subtracts the other translation from this translation.
   * <b>Modifies this object.</b>
   *
   * <p>For example, Translation2d(5.0, 4.0) - Translation2d(1.0, 2.0) = Translation2d(4.0, 2.0).
   *
   * @param other The translation to subtract.
   * @return This object (modified).
   */
  public Translation2dDemacia minus(Translation2dDemacia other) {
    m_x = m_x - other.m_x;
    m_y = m_y - other.m_y;
    return this;
  }

  /**
   * Inverts the current translation. This is equivalent to rotating by 180 degrees,
   * flipping the point over both axes, or negating all components of the translation.
   * <b>Modifies this object.</b>
   *
   * @return This object (modified).
   */
  public Translation2dDemacia unaryMinus() {
    m_x = -m_x;
    m_y = -m_y;
    return this;
  }

  /**
   * Multiplies the translation by a scalar.
   * <b>Modifies this object.</b>
   *
   * <p>For example, Translation2d(2.0, 2.5) * 2 = Translation2d(4.0, 5.0).
   *
   * @param scalar The scalar to multiply by.
   * @return This object (modified).
   */
  public Translation2dDemacia times(double scalar) {
    m_x = m_x * scalar;
    m_y = m_y * scalar;
    return this;
  }

  /**
   * Divides the translation by a scalar.
   * <b>Modifies this object.</b>
   *
   * <p>For example, Translation3d(2.0, 2.5) / 2 = Translation3d(1.0, 1.25).
   *
   * @param scalar The scalar to multiply by.
   * @return This object (modified).
   */
  public Translation2dDemacia div(double scalar) {
    m_x = m_x / scalar;
    m_y = m_y / scalar;
    return this;
  }

  /**
   * Returns the nearest Translation2d from a collection of translations.
   *
   * @param translations The collection of translations.
   * @return The nearest Translation2d from the collection.
   */
  public Translation2dDemacia nearest(Collection<Translation2dDemacia> translations) {
    return Collections.min(translations, Comparator.comparing(this::getDistance));
  }

  @Override
  public String toString() {
    return String.format("Translation2d(X: %.2f, Y: %.2f)", m_x, m_y);
  }

  /**
   * Checks equality between this Translation2d and another object.
   *
   * @param obj The other object.
   * @return Whether the two objects are equal or not.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Translation2dDemacia other
        && Math.abs(other.m_x - m_x) < 1E-9
        && Math.abs(other.m_y - m_y) < 1E-9;
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_x, m_y);
  }

  @Override
  public Translation2dDemacia interpolate(Translation2dDemacia endValue, double t) {
    return new Translation2dDemacia(
        MathUtil.interpolate(this.getX(), endValue.getX(), t),
        MathUtil.interpolate(this.getY(), endValue.getY(), t));
  }

  /** Translation2d protobuf for serialization. */
  public static final Translation2dProto proto = new Translation2dProto();

  /** Translation2d struct for serialization. */
  public static final Translation2dStruct struct = new Translation2dStruct();
}