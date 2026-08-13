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
  protected StateBaseMechanism mechanism;
  protected MotorInterface[] motors;
  protected int length;
  protected Runnable[] controls;

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
      final int index = i;
      controls[i] = switch (controlModes[i]) {
        case DUTYCYCLE -> () -> mechanism.setPower(index, mechanism.getValue(index));
        case VOLTAGE -> () -> mechanism.setVoltage(index, mechanism.getValue(index));
        case VELOCITY -> () -> mechanism.setVelocity(index, mechanism.getValue(index));
        case POSITION_VOLTAGE -> () -> mechanism.setPositionVoltage(index, mechanism.getValue(index));
        case MAGIC_MOTION -> () -> mechanism.setMotion(index, mechanism.getValue(index));
        case ANGLE -> () -> mechanism.setAngle(index, mechanism.getValue(index));
        default -> () -> {};
      };
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
    if (mechanism.getState().equals(mechanism.IDLE_STATE)){
      mechanism.stop();
    } else {
      for (int i = 0; i < length; i++) {
        controls[i].run();
      }
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