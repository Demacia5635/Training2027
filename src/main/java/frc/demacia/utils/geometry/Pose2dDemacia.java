package frc.demacia.utils.geometry;

import static edu.wpi.first.units.Units.Meters;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.geometry.proto.Pose2dProto;
import edu.wpi.first.math.geometry.struct.Pose2dStruct;
import edu.wpi.first.math.interpolation.Interpolatable;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.util.protobuf.ProtobufSerializable;
import edu.wpi.first.util.struct.StructSerializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/** Represents a 2D pose containing translational and rotational elements.
 * * <p><b>NOTE: This is a MUTABLE implementation. Methods like plus(), minus(), rotateBy() modify the current object!</b>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Pose2dDemacia implements Interpolatable<Pose2dDemacia>, ProtobufSerializable, StructSerializable {

  private Translation2dDemacia m_translation;
  private Rotation2dDemacia m_rotation;

  /** Constructs a pose at the origin facing toward the positive X axis. */
  public Pose2dDemacia() {
    m_translation = new Translation2dDemacia();
    m_rotation = new Rotation2dDemacia();
  }

  /**
   * Constructs a pose with the specified translation and rotation.
   *
   * @param translation The translational component of the pose.
   * @param rotation The rotational component of the pose.
   */
  @JsonCreator
  public Pose2dDemacia(
      @JsonProperty(required = true, value = "translation") Translation2dDemacia translation,
      @JsonProperty(required = true, value = "rotation") Rotation2dDemacia rotation) {
    m_translation = translation;
    m_rotation = rotation;
  }

  /**
   * Constructs a pose with x and y translations instead of a separate Translation2d.
   *
   * @param x The x component of the translational component of the pose.
   * @param y The y component of the translational component of the pose.
   * @param rotation The rotational component of the pose.
   */
  public Pose2dDemacia(double x, double y, Rotation2dDemacia rotation) {
    m_translation = new Translation2dDemacia(x, y);
    m_rotation = rotation;
  }

  /**
   * Constructs a pose with x and y translations instead of a separate Translation2d. The X and Y
   * translations will be converted to and tracked as meters.
   *
   * @param x The x component of the translational component of the pose.
   * @param y The y component of the translational component of the pose.
   * @param rotation The rotational component of the pose.
   */
  public Pose2dDemacia(Distance x, Distance y, Rotation2dDemacia rotation) {
    this(x.in(Meters), y.in(Meters), rotation);
  }

  /**
   * Constructs a pose with the specified affine transformation matrix.
   *
   * @param matrix The affine transformation matrix.
   * @throws IllegalArgumentException if the affine transformation matrix is invalid.
   */
  public Pose2dDemacia(Matrix<N3, N3> matrix) {
    m_translation = new Translation2dDemacia(matrix.get(0, 2), matrix.get(1, 2));
    m_rotation = new Rotation2dDemacia(matrix.block(2, 2, 0, 0));
    if (matrix.get(2, 0) != 0.0 || matrix.get(2, 1) != 0.0 || matrix.get(2, 2) != 1.0) {
      throw new IllegalArgumentException("Affine transformation matrix is invalid");
    }
  }

  /**
   * Updates this Pose2d to match the provided one without memory allocation.
   * @param other The pose to copy from.
   * @return This object.
   */
  public Pose2dDemacia set(Pose2dDemacia other) {
    m_translation.set(other.m_translation);
    m_rotation.set(other.m_rotation);
    return this;
  }
  
  /**
   * Updates this Pose2d with raw values.
   * @param x The new x translation.
   * @param y The new y translation.
   * @param rot The new rotation.
   * @return This object.
   */
  public Pose2dDemacia set(double x, double y, Rotation2dDemacia rot) {
    m_translation.set(x, y);
    m_rotation.set(rot);
    return this;
  }

  /**
   * Transforms the pose by the given transformation.
   * <b>Modifies this object.</b>
   *
   * <pre>
   * [x_new]    [cos, -sin, 0][transform.x]
   * [y_new] += [sin,  cos, 0][transform.y]
   * [t_new]    [  0,    0, 1][transform.t]
   * </pre>
   *
   * @param other The transform to transform the pose by.
   * @return This object (modified).
   */
  public Pose2dDemacia plus(Transform2dDemacia other) {
    return transformBy(other);
  }

  /**
   * Subtracts the other pose from this pose (geometric difference).
   * Effectively makes this pose relative to the other pose.
   * <b>Modifies this object.</b>
   *
   * @param other The pose to subtract.
   * @return This object (modified, now representing the difference).
   */
  public Pose2dDemacia minus(Pose2dDemacia other) {
    return relativeTo(other);
  }

  /**
   * Returns the translation component of the transformation.
   *
   * @return The translational component of the pose.
   */
  @JsonProperty
  public Translation2dDemacia getTranslation() {
    return m_translation;
  }

  /**
   * Returns the X component of the pose's translation.
   *
   * @return The x component of the pose's translation.
   */
  public double getX() {
    return m_translation.getX();
  }

  /**
   * Returns the Y component of the pose's translation.
   *
   * @return The y component of the pose's translation.
   */
  public double getY() {
    return m_translation.getY();
  }

  /**
   * Returns the X component of the pose's translation in a measure.
   *
   * @return The x component of the pose's translation in a measure.
   */
  public Distance getMeasureX() {
    return m_translation.getMeasureX();
  }

  /**
   * Returns the Y component of the pose's translation in a measure.
   *
   * @return The y component of the pose's translation in a measure.
   */
  public Distance getMeasureY() {
    return m_translation.getMeasureY();
  }

  /**
   * Returns the rotational component of the transformation.
   *
   * @return The rotational component of the pose.
   */
  @JsonProperty
  public Rotation2dDemacia getRotation() {
    return m_rotation;
  }

  /**
   * Multiplies the current pose by a scalar.
   * <b>Modifies this object.</b>
   *
   * @param scalar The scalar.
   * @return This object (modified).
   */
  public Pose2dDemacia times(double scalar) {
    m_translation.times(scalar);
    m_rotation.times(scalar);
    return this;
  }

  /**
   * Divides the current pose by a scalar.
   * <b>Modifies this object.</b>
   *
   * @param scalar The scalar.
   * @return This object (modified).
   */
  public Pose2dDemacia div(double scalar) {
    return times(1.0 / scalar);
  }

  /**
   * Rotates the pose around the origin.
   * <b>Modifies this object.</b>
   *
   * @param other The rotation to transform the pose by.
   * @return This object (modified).
   */
  public Pose2dDemacia rotateBy(Rotation2dDemacia other) {
    m_translation.rotateBy(other);
    m_rotation.rotateBy(other);
    return this;
  }

  /**
   * Transforms the pose by the given transformation. See + operator for
   * the matrix multiplication performed.
   * <b>Modifies this object.</b>
   *
   * @param other The transform to transform the pose by.
   * @return This object (modified).
   */
  public Pose2dDemacia transformBy(Transform2dDemacia other) {
    double cos = m_rotation.getCos();
    double sin = m_rotation.getSin();
    double tx = other.getX();
    double ty = other.getY();

    double newX = tx * cos - ty * sin;
    double newY = tx * sin + ty * cos;

    m_translation.set(m_translation.getX() + newX, m_translation.getY() + newY);

    m_rotation.rotateBy(other.getRotation());
    
    return this;
  }

  /**
   * Updates the current pose to be relative to the given pose.
   * <b>Modifies this object.</b>
   *
   * @param other The pose that is the origin of the new coordinate frame.
   * @return This object (modified, now relative to other).
   */
  public Pose2dDemacia relativeTo(Pose2dDemacia other) {
    double dx = m_translation.getX() - other.getX();
    double dy = m_translation.getY() - other.getY();
    
    double c = other.getRotation().getCos();
    double s = other.getRotation().getSin();

    double newX = dx * c + dy * s;
    double newY = dx * -s + dy * c;

    m_translation.set(newX, newY);

    m_rotation.minus(other.getRotation());

    return this;
  }

  /**
   * Rotates the current pose around a point in 2D space.
   * <b>Modifies this object.</b>
   *
   * @param point The point in 2D space to rotate around.
   * @param rot The rotation to rotate the pose by.
   * @return This object (modified).
   */
  public Pose2dDemacia rotateAround(Translation2dDemacia point, Rotation2dDemacia rot) {
    m_translation.rotateAround(point, rot);
    m_rotation.rotateBy(rot);
    return this;
  }

  /**
   * Updates the pose from a (constant curvature) velocity.
   * <b>Modifies this object.</b>
   *
   * <p>See <a href="https://file.tavsys.net/control/controls-engineering-in-frc.pdf">Controls
   * Engineering in the FIRST Robotics Competition</a> section 10.2 "Pose exponential" for a
   * derivation.
   *
   * <p>The twist is a change in pose in the robot's coordinate frame since the previous pose
   * update. When the user runs exp() on the previous known field-relative pose with the argument
   * being the twist, the user will receive the new field-relative pose.
   *
   * <p>"Exp" represents the pose exponential, which is solving a differential equation moving the
   * pose forward in time.
   *
   * @param twist The change in pose in the robot's coordinate frame since the previous pose update.
   * For example, if a non-holonomic robot moves forward 0.01 meters and changes angle by 0.5
   * degrees since the previous pose update, the twist would be Twist2d(0.01, 0.0,
   * Units.degreesToRadians(0.5)).
   * @return This object (modified).
   */
  public Pose2dDemacia exp(Twist2d twist) {
    double dx = twist.dx;
    double dy = twist.dy;
    double dtheta = twist.dtheta;

    double sinTheta = Math.sin(dtheta);
    double cosTheta = Math.cos(dtheta);

    double s;
    double c;
    
    if (Math.abs(dtheta) < 1E-9) {
      s = 1.0 - 1.0 / 6.0 * dtheta * dtheta;
      c = 0.5 * dtheta;
    } else {
      s = sinTheta / dtheta;
      c = (1 - cosTheta) / dtheta;
    }

    double transformX = dx * s - dy * c;
    double transformY = dx * c + dy * s;

    double currentCos = m_rotation.getCos();
    double currentSin = m_rotation.getSin();
    
    double newX = transformX * currentCos - transformY * currentSin;
    double newY = transformX * currentSin + transformY * currentCos;

    m_translation.set(m_translation.getX() + newX, m_translation.getY() + newY);
    m_rotation.rotateBy(Rotation2dDemacia.fromRadians(dtheta));

    return this;
  }

  /**
   * Returns a Twist2d that maps this pose to the end pose. If c is the output of {@code a.Log(b)},
   * then {@code a.Exp(c)} would yield b.
   *
   * @param end The end pose for the transformation.
   * @return The twist that maps this to end.
   */
  public Twist2d log(Pose2dDemacia end) {
    final var transform = end.relativeTo(this);
    final var dtheta = transform.getRotation().getRadians();
    final var halfDtheta = dtheta / 2.0;

    final var cosMinusOne = transform.getRotation().getCos() - 1;

    double halfThetaByTanOfHalfDtheta;
    if (Math.abs(cosMinusOne) < 1E-9) {
      halfThetaByTanOfHalfDtheta = 1.0 - 1.0 / 12.0 * dtheta * dtheta;
    } else {
      halfThetaByTanOfHalfDtheta = -(halfDtheta * transform.getRotation().getSin()) / cosMinusOne;
    }

    Translation2dDemacia translationPart =
        transform
            .getTranslation()
            .rotateBy(new Rotation2dDemacia(halfThetaByTanOfHalfDtheta, -halfDtheta))
            .times(Math.hypot(halfThetaByTanOfHalfDtheta, halfDtheta));

    return new Twist2d(translationPart.getX(), translationPart.getY(), dtheta);
  }

  /**
   * Returns an affine transformation matrix representation of this pose.
   *
   * @return An affine transformation matrix representation of this pose.
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
   * Returns the nearest Pose2d from a collection of poses. If two or more poses in the collection
   * have the same distance from this pose, return the one with the closest rotation component.
   *
   * @param poses The collection of poses to find the nearest.
   * @return The nearest Pose2d from the collection.
   */
  public Pose2dDemacia nearest(Collection<Pose2dDemacia> poses) {
    return Collections.min(
        poses,
        Comparator.comparing(
                (Pose2dDemacia other) -> this.getTranslation().getDistance(other.getTranslation()))
            .thenComparing(
                (Pose2dDemacia other) ->
                    Math.abs(this.getRotation().minus(other.getRotation()).getRadians())));
  }

  @Override
  public String toString() {
    return String.format("Pose2d(%s, %s)", m_translation, m_rotation);
  }

  /**
   * Checks equality between this Pose2d and another object.
   *
   * @param obj The other object.
   * @return Whether the two objects are equal or not.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Pose2dDemacia pose
        && m_translation.equals(pose.m_translation)
        && m_rotation.equals(pose.m_rotation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_translation, m_rotation);
  }

  @Override
  public Pose2dDemacia interpolate(Pose2dDemacia endValue, double t) {
    if (t < 0) {
      return this;
    } else if (t >= 1) {
      return endValue;
    } else {
      var twist = this.log(endValue);
      var scaledTwist = new Twist2d(twist.dx * t, twist.dy * t, twist.dtheta * t);
      return this.exp(scaledTwist);
    }
  }

  /** Pose2d protobuf for serialization. */
  public static final Pose2dProto proto = new Pose2dProto();

  /** Pose2d struct for serialization. */
  public static final Pose2dStruct struct = new Pose2dStruct();
}