package frc.demacia.utils.geometry;

import static edu.wpi.first.units.Units.Meters;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.geometry.proto.Transform2dProto;
import edu.wpi.first.math.geometry.struct.Transform2dStruct;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.util.protobuf.ProtobufSerializable;
import edu.wpi.first.util.struct.StructSerializable;
import java.util.Objects;

/** Represents a transformation for a Pose2d in the pose's frame. 
 * <p><b>NOTE: This is a MUTABLE implementation. Methods like plus(), inverse() modify the current object!</b>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Transform2dDemacia implements ProtobufSerializable, StructSerializable {

  private final Translation2dDemacia m_translation;
  private final Rotation2dDemacia m_rotation;

  /**
   * Constructs the transform that maps the initial pose to the final pose.
   *
   * @param initial The initial pose for the transformation.
   * @param last The final pose for the transformation.
   */
  public Transform2dDemacia(Pose2dDemacia initial, Pose2dDemacia last) {
    // 1. Calculate translation difference (last - initial)
    // We create a NEW translation from 'last' to avoid mutating it.
    m_translation = new Translation2dDemacia(last.getTranslation().getX(), last.getTranslation().getY());
    m_translation.minus(initial.getTranslation());

    // 2. Rotate delta by -initial.rotation
    // We create a NEW rotation for the inverse to avoid mutating 'initial'.
    Rotation2dDemacia initialRotInv = new Rotation2dDemacia(initial.getRotation().getRadians());
    initialRotInv.unaryMinus();
    m_translation.rotateBy(initialRotInv);

    // 3. Calculate rotation difference
    m_rotation = new Rotation2dDemacia(last.getRotation().getRadians());
    m_rotation.minus(initial.getRotation());
  }

  /**
   * Constructs a transform with the given translation and rotation components.
   *
   * @param translation Translational component of the transform.
   * @param rotation Rotational component of the transform.
   */
  @JsonCreator
  public Transform2dDemacia(
      @JsonProperty(required = true, value = "translation") Translation2dDemacia translation,
      @JsonProperty(required = true, value = "rotation") Rotation2dDemacia rotation) {
    m_translation = translation;
    m_rotation = rotation;
  }

  /**
   * Constructs a transform with x and y translations instead of a separate Translation2d.
   *
   * @param x The x component of the translational component of the transform.
   * @param y The y component of the translational component of the transform.
   * @param rotation The rotational component of the transform.
   */
  public Transform2dDemacia(double x, double y, Rotation2dDemacia rotation) {
    m_translation = new Translation2dDemacia(x, y);
    m_rotation = rotation;
  }

  /**
   * Constructs a transform with x and y translations instead of a separate Translation2d. The X and
   * Y translations will be converted to and tracked as meters.
   *
   * @param x The x component of the translational component of the transform.
   * @param y The y component of the translational component of the transform.
   * @param rotation The rotational component of the transform.
   */
  public Transform2dDemacia(Distance x, Distance y, Rotation2dDemacia rotation) {
    this(x.in(Meters), y.in(Meters), rotation);
  }

  /**
   * Constructs a transform with the specified affine transformation matrix.
   *
   * @param matrix The affine transformation matrix.
   * @throws IllegalArgumentException if the affine transformation matrix is invalid.
   */
  public Transform2dDemacia(Matrix<N3, N3> matrix) {
    m_translation = new Translation2dDemacia(matrix.get(0, 2), matrix.get(1, 2));
    m_rotation = new Rotation2dDemacia(matrix.block(2, 2, 0, 0));
    if (matrix.get(2, 0) != 0.0 || matrix.get(2, 1) != 0.0 || matrix.get(2, 2) != 1.0) {
      throw new IllegalArgumentException("Affine transformation matrix is invalid");
    }
  }

  /** Constructs the identity transform -- maps an initial pose to itself. */
  public Transform2dDemacia() {
    m_translation = new Translation2dDemacia();
    m_rotation = new Rotation2dDemacia();
  }

  /**
   * Updates this Transform2d to match the provided one without memory allocation.
   * @param other The transform to copy from.
   * @return This object.
   */
  public Transform2dDemacia set(Transform2dDemacia other) {
    m_translation.set(other.m_translation);
    m_rotation.set(other.m_rotation);
    return this;
  }

  /**
   * Multiplies the transform by the scalar.
   * <b>Modifies this object.</b>
   *
   * @param scalar The scalar.
   * @return This object (modified).
   */
  public Transform2dDemacia times(double scalar) {
    m_translation.times(scalar);
    m_rotation.times(scalar);
    return this;
  }

  /**
   * Divides the transform by the scalar.
   * <b>Modifies this object.</b>
   *
   * @param scalar The scalar.
   * @return This object (modified).
   */
  public Transform2dDemacia div(double scalar) {
    return times(1.0 / scalar);
  }

  /**
   * Composes two transformations. The second transform is applied relative to the orientation of
   * the first.
   * <b>Modifies this object.</b>
   *
   * @param other The transform to compose with this one.
   * @return This object (modified).
   */
  public Transform2dDemacia plus(Transform2dDemacia other) {
    // T_new = T_this + R_this * T_other
    // We make a copy of other.translation to safely rotate it
    Translation2dDemacia otherTrans = new Translation2dDemacia(other.getTranslation().getX(), other.getTranslation().getY());
    otherTrans.rotateBy(m_rotation);
    
    m_translation.plus(otherTrans);
    
    // R_new = R_this + R_other
    m_rotation.plus(other.getRotation());
    
    return this;
  }

  /**
   * Returns the translation component of the transformation.
   *
   * @return The translational component of the transform.
   */
  @JsonProperty
  public Translation2dDemacia getTranslation() {
    return m_translation;
  }

  /**
   * Returns the X component of the transformation's translation.
   *
   * @return The x component of the transformation's translation.
   */
  public double getX() {
    return m_translation.getX();
  }

  /**
   * Returns the Y component of the transformation's translation.
   *
   * @return The y component of the transformation's translation.
   */
  public double getY() {
    return m_translation.getY();
  }

  /**
   * Returns the X component of the transformation's translation in a measure.
   *
   * @return The x component of the transformation's translation in a measure.
   */
  public Distance getMeasureX() {
    return m_translation.getMeasureX();
  }

  /**
   * Returns the Y component of the transformation's translation in a measure.
   *
   * @return The y component of the transformation's translation in a measure.
   */
  public Distance getMeasureY() {
    return m_translation.getMeasureY();
  }

  /**
   * Returns an affine transformation matrix representation of this transformation.
   *
   * @return An affine transformation matrix representation of this transformation.
   */
  public Matrix<N3, N3> toMatrix() {
    var vec = m_translation.toVector();
    var mat = m_rotation.toMatrix();
    return MatBuilder.fill(
        Nat.N3(),
        Nat.N3(),
        mat.get(0, 0),
        mat.get(0, 1),
        vec.get(0),
        mat.get(1, 0),
        mat.get(1, 1),
        vec.get(1),
        0.0,
        0.0,
        1.0);
  }

  /**
   * Returns the rotational component of the transformation.
   *
   * @return Reference to the rotational component of the transform.
   */
  @JsonProperty
  public Rotation2dDemacia getRotation() {
    return m_rotation;
  }

  /**
   * Invert the transformation. This is useful for undoing a transformation.
   * <b>Modifies this object.</b>
   *
   * @return This object (modified).
   */
  public Transform2dDemacia inverse() {
    // 1. Invert rotation (R = -R)
    m_rotation.unaryMinus();
    
    // 2. Invert translation (T = -T)
    m_translation.unaryMinus();
    
    // 3. Rotate negated translation by inverted rotation
    m_translation.rotateBy(m_rotation);
    
    return this;
  }

  @Override
  public String toString() {
    return String.format("Transform2d(%s, %s)", m_translation, m_rotation);
  }

  /**
   * Checks equality between this Transform2d and another object.
   *
   * @param obj The other object.
   * @return Whether the two objects are equal or not.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Transform2dDemacia other
        && other.m_translation.equals(m_translation)
        && other.m_rotation.equals(m_rotation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_translation, m_rotation);
  }

  /** Transform2d protobuf for serialization. */
  public static final Transform2dProto proto = new Transform2dProto();

  /** Transform2d struct for serialization. */
  public static final Transform2dStruct struct = new Transform2dStruct();
}