package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.Constants;
import frc.robot.subsystems.SUBSYS_name_V1_FINAL;

public class DriveVelocityPIDCommand_V1_FINAL extends Command {

    private final SUBSYS_name_V1_FINAL subsystem;

    private final PIDController controller;


    public DriveVelocityPIDCommand_V1_FINAL(
            SUBSYS_name_V1_FINAL subsystem) {

        this.subsystem = subsystem;


        controller =
            new PIDController(
                Constants.SimpleMotorConstants.DRIVE_KP,
                Constants.SimpleMotorConstants.DRIVE_KI,
                Constants.SimpleMotorConstants.DRIVE_KD
            );


        // Assignment velocity accuracy:
        // 0.1 m/s
        controller.setTolerance(
            0.1
        );


        addRequirements(
            subsystem
        );


        SmartDashboard.putData(
            "PID/Drive Controller",
            controller
        );
    }


    @Override
    public void initialize() {

        controller.reset();

        System.out.println(
            "Drive Velocity PID started"
        );
    }


    @Override
    public void execute() {

        // Requested velocity from Elastic
        double targetVelocity =
            SmartDashboard.getNumber(
                "Drive PID/Target Velocity (mps)",
                0.0
            );


        // Current velocity from Demacia
        double currentVelocity =
            subsystem.getDriveVelocity();


        // WPILib PID
        double output =
            controller.calculate(
                currentVelocity,
                targetVelocity
            );


        // Safety limit
        output =
            MathUtil.clamp(
                output,
                -Constants.SimpleMotorConstants.POWER_LIMIT,
                Constants.SimpleMotorConstants.POWER_LIMIT
            );


        // Send power to drive motor
        subsystem.setDrivePower(
            output
        );


        // =========================================
        // ELASTIC DATA
        // =========================================

        SmartDashboard.putNumber(
            "Drive PID/Target Velocity (mps)",
            targetVelocity
        );


        SmartDashboard.putNumber(
            "Drive PID/Current Velocity (mps)",
            currentVelocity
        );


        SmartDashboard.putNumber(
            "Drive PID/Error (mps)",
            controller.getError()
        );


        SmartDashboard.putNumber(
            "Drive PID/Output",
            output
        );


        SmartDashboard.putNumber(
            "Drive PID/Kp",
            controller.getP()
        );


        SmartDashboard.putNumber(
            "Drive PID/Ki",
            controller.getI()
        );


        SmartDashboard.putNumber(
            "Drive PID/Kd",
            controller.getD()
        );


        SmartDashboard.putBoolean(
            "Drive PID/At Setpoint",
            controller.atSetpoint()
        );
    }


    @Override
    public boolean isFinished() {

        return false;
    }


    @Override
    public void end(
            boolean interrupted) {

        subsystem.stopDrive();

        System.out.println(
            "Drive Velocity PID ended"
        );
    }
}