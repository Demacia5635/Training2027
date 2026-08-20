// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;
import frc.demacia.utils.sensors.CancoderConfig;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
	public static class OperatorConstants {
		public static final int kDriverControllerPort = 0;
	}

	public static class SimplePIDConstants {
		public static final double KP = 1;
		public static final double KS = 0.02308;
		public static final double KV = 0.12515;

		public static final TalonFXConfig CONFIG = new TalonFXConfig(
			1, Canbus.Rio, "SimplePID"
		).withPID(KP, 0.0, 0.0, KS, KV, 0.0, 0.0);
	}

	public static class ModuleSubsystemConstants {

		// Need to set only KS and KV
		public static final TalonFXConfig STEER_CONFIG = new TalonFXConfig(
			1, Canbus.Rio, "Steer Motor"
		).withPID(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

		public static final TalonFXConfig DRIVE_CONFIG = new TalonFXConfig(
			2, Canbus.Rio, "Drive Motor"
		).withPID(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	
		public static final CancoderConfig CANCODER_CONFIG = new CancoderConfig(3, null, "Cancoder");
	}
}