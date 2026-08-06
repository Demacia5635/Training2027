package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SimpleMotorCommand extends Command{

    // required variables:
    private final SimpleMotorSubsystem subsystem;
    private final double steerPower;
    private final double drivePower;
    private final double duration;
    private double startTime = 0;
    private Timer timer = new Timer();




    // constructor
    public SimpleMotorCommand(SimpleMotorSubsystem subsystem, double steerPower,double drivePower, double duration) {
        this.subsystem = subsystem;
        this.steerPower = steerPower;
        this.drivePower = drivePower;
        this.duration = duration;

    }


    // initialize
    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
        timer.restart();
        System.out.println("Command started at: " + startTime);
        System.out.println("Seconds for: " + duration + "second/s with steer power: " + steerPower + "And drive power: " + drivePower);

    }


    // execute
    @Override
    public void execute() {
        subsystem.setDrivePower(drivePower);
        subsystem.setSteerPower(steerPower);


    }

    // is finished?
    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() >= startTime + duration;
    }

    // end
    public void end(boolean interrupted) {
        subsystem.driveStop();
        subsystem.steerStop();
        System.out.println("Command ended at: " + Timer.getFPGATimestamp());
    }

}
