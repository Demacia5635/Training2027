package frc.robot.commands;

import frc.robot.subsystems.SimpleMotorSubsystem;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class DriveVelocityPIDCommand extends Command {

    private final SimpleMotorSubsystem subsystem;

    private final PIDController pidController;


    public DriveVelocityPIDCommand(
            SimpleMotorSubsystem subsystem) {

        this.subsystem = subsystem;

        // Start with P only
        double kP = 0.005;
        double kI = 0.0;
        double kD = 0.0;

        pidController = new PIDController(
            kP,
            kI,
            kD
        );

        pidController.setTolerance(0.05);

        addRequirements(subsystem);
    }


    @Override
    public void initialize() {

        pidController.reset();

        System.out.println(
            "Drive Velocity PID Command started"
        );
    }


    @Override
    public void execute() {

        double targetVelocity =
            SmartDashboard.getNumber(
                "Drive Target Velocity",
                0.0
            );


        double output =
            pidController.calculate(
                targetVelocity
            );


        // Safety limit
        output = MathUtil.clamp(
            output,
            -0.3,
            0.3
        );


        subsystem.setMotor1Power(output);


        // Elastic
        SmartDashboard.putNumber(
            "Drive Target Velocity",
            targetVelocity
        );

        SmartDashboard.putNumber(
            "Drive PID Output",
            output
        );

        SmartDashboard.putNumber(
            "Drive Kp",
            pidController.getP()
        );

        SmartDashboard.putNumber(
            "Drive Ki",
            pidController.getI()
        );

        SmartDashboard.putNumber(
            "Drive Kd",
            pidController.getD()
        );
    }


    @Override
    public boolean isFinished() {

        return false;
    }


    @Override
    public void end(boolean interrupted) {

        subsystem.stop();

        System.out.println(
            "=== Drive Velocity PID Debug ==="
        );

        System.out.println(
            "Setpoint: "
            + pidController.getSetpoint()
        );

        System.out.println(
            "Error: "
            + pidController.getError()
        );

        System.out.println(
            "P: "
            + pidController.getP()
        );

        System.out.println(
            "I: "
            + pidController.getI()
        );

        System.out.println(
            "D: "
            + pidController.getD()
        );
    }
}