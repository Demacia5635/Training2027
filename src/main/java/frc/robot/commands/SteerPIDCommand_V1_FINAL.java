package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.Constants;
import frc.robot.subsystems.SUBSYS_name_V1_FINAL;

public class SteerPIDCommand_V1_FINAL extends Command {

    private final SUBSYS_name_V1_FINAL subsystem;

    private final PIDController controller;


    public SteerPIDCommand_V1_FINAL(
            SUBSYS_name_V1_FINAL subsystem) {

        this.subsystem = subsystem;


        controller =
            new PIDController(
                Constants.SimpleMotorConstants.STEER_KP,
                Constants.SimpleMotorConstants.STEER_KI,
                Constants.SimpleMotorConstants.STEER_KD
            );


        // Steering angle wraps around.
        controller.enableContinuousInput(
            -180.0,
            180.0
        );


        // Assignment accuracy target
        controller.setTolerance(
            1.0
        );


        addRequirements(
            subsystem
        );


        SmartDashboard.putData(
            "PID/Steer Controller",
            controller
        );
    }


    @Override
    public void initialize() {

        controller.reset();

        System.out.println(
            "Steer PID started"
        );
    }


    @Override
    public void execute() {

        // Requested angle from Elastic
        double targetAngle =
            SmartDashboard.getNumber(
                "Steer PID/Target Angle (deg)",
                0.0
            );


        // Current angle from Demacia
        double currentAngle =
            subsystem.getSteerAngleDegrees();


        // WPILib PID
        double output =
            controller.calculate(
                currentAngle,
                targetAngle
            );


        // Safety limit
        output =
            MathUtil.clamp(
                output,
                -Constants.SimpleMotorConstants.POWER_LIMIT,
                Constants.SimpleMotorConstants.POWER_LIMIT
            );


        // Send power to steer motor
        subsystem.setSteerPower(
            output
        );


        // =========================================
        // ELASTIC DATA
        // =========================================

        SmartDashboard.putNumber(
            "Steer PID/Target Angle (deg)",
            targetAngle
        );


        SmartDashboard.putNumber(
            "Steer PID/Current Angle (deg)",
            currentAngle
        );


        SmartDashboard.putNumber(
            "Steer PID/Error (deg)",
            controller.getError()
        );


        SmartDashboard.putNumber(
            "Steer PID/Output",
            output
        );


        SmartDashboard.putNumber(
            "Steer PID/Kp",
            controller.getP()
        );


        SmartDashboard.putNumber(
            "Steer PID/Ki",
            controller.getI()
        );


        SmartDashboard.putNumber(
            "Steer PID/Kd",
            controller.getD()
        );


        SmartDashboard.putBoolean(
            "Steer PID/At Setpoint",
            controller.atSetpoint()
        );
    }


    @Override
    public boolean isFinished() {

        // Keep running so Elastic can show the graph.
        return false;
    }


    @Override
    public void end(
            boolean interrupted) {

        subsystem.stopSteer();

        System.out.println(
            "Steer PID ended"
        );
    }
}