package frc.demacia.utils.mechanisms;

import edu.wpi.first.wpilibj2.command.Command;
import frc.demacia.utils.motors.MotorInterface;
import frc.demacia.utils.motors.MotorInterface.ControlMode;

/**
 * A command that continuously applies the current state values of a StateBaseMechanism.
 * <p>
 * This command maps a specific ControlMode to each motor and executes the corresponding
 * set method (e.g., setPower, setVelocity) using values retrieved from the mechanism's state.
 * </p>
 */
public class DefaultCommand extends Command {
  StateBaseMechanism mechanism;
  MotorInterface[] motors;
  int length;
  Runnable[] controls;

  /** * Creates a new DefaultCommand.
   * Initializes a set of runnables to control each motor based on the provided control modes.
   * * @param mechanism The StateBaseMechanism to control
   * @param controlModes Array of ControlModes, one for each motor in the mechanism
   */
  public DefaultCommand(StateBaseMechanism mechanism, ControlMode[] controlModes) {
    this.mechanism = mechanism;
    motors = mechanism.getMotors();
    length = Math.min(motors.length, controlModes.length);
    controls = new Runnable[length];
    for (int i = 0; i < length; i++) {
      switch (controlModes[i]) {
        case DUTYCYCLE:
          final int powerIndex = i;
          controls[i] = () -> mechanism.setPower(powerIndex, mechanism.getValue(powerIndex));
          break;
        case VOLTAGE:
          final int voltageIndex = i;
          controls[i] = () -> mechanism.setVoltage(voltageIndex, mechanism.getValue(voltageIndex));
          break;
        case VELOCITY:
          final int velocityIndex = i;
          controls[i] = () -> mechanism.setVelocity(velocityIndex, mechanism.getValue(velocityIndex));
          break;
        case POSITION_VOLTAGE:
          final int positionVoltageIndex = i;
          controls[i] = () -> mechanism.setPositionVoltage(positionVoltageIndex, mechanism.getValue(positionVoltageIndex));
          break;
        case MOTION:
          final int motionIndex = i;
          controls[i] = () -> mechanism.setMotion(motionIndex, mechanism.getValue(motionIndex));
          break;
        case ANGLE:
          final int angleIndex = i;
          controls[i] = () -> mechanism.setAngle(angleIndex, mechanism.getValue(angleIndex));
          break;
        default:
          controls[i] = () -> {};
          break;
      }
  }
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(mechanism);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    for (int i = 0; i < length; i++) {
      controls[i].run();
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}