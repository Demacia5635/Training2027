package frc.robot.commands;

import frc.robot.subsystems.SimpleMotorSubsystem;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class SteerPIDCommand extends Command {

    private final SimpleMotorSubsystem subsystem;

    private final PIDController pidController;


    public SteerPIDCommand(
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

        pidController.setTolerance(2.0);

        addRequirements(subsystem);
    }


    @Override
    public void initialize() {

        pidController.reset();

        System.out.println(
            "Steer PID Command started"
        );
    }


    @Override
    public void execute() {

        // Read requested angle from Elastic
        double targetAngle =
            SmartDashboard.getNumber(
                "Steer Target Angle",
                0.0
            );

        double currentAngle =
            subsystem.getMotor2Pos();


        // PID calculation
        double output =
            pidController.calculate(
                currentAngle,
                targetAngle
            );


        // Safety limit
        output = MathUtil.clamp(
            output,
            -0.3,
            0.3
        );


        subsystem.setMotor2Power(output);


        // Elastic
        SmartDashboard.putNumber(
            "Steer Target Angle",
            targetAngle
        );

        SmartDashboard.putNumber(
            "Steer Current Angle",
            currentAngle
        );

        SmartDashboard.putNumber(
            "Steer Error",
            targetAngle - currentAngle
        );

        SmartDashboard.putNumber(
            "Steer PID Output",
            output
        );

        SmartDashboard.putNumber(
            "Steer Kp",
            pidController.getP()
        );

        SmartDashboard.putNumber(
            "Steer Ki",
            pidController.getI()
        );

        SmartDashboard.putNumber(
            "Steer Kd",
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
            "=== Steer PID Debug ==="
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