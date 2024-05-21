// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.TeleopDriveCommand;
import frc.robot.subsystems.API;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Navigation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final Drivetrain m_romiDrivetrain = new Drivetrain();
  private final Navigation m_nav = new Navigation();
  private final CommandJoystick joy = new CommandJoystick(0);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the button bindings
    m_nav.setLocations((ArrayList<Point>) API.getPathFromAPI());
    // m_nav.updateAPI(new Point("test", 1, 2));
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link edu.wpi.first.wpilibj.GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() 
  {
    m_romiDrivetrain.setDefaultCommand(new TeleopDriveCommand(m_romiDrivetrain, joy::getY, joy::getX));

    //When running the robot, it must start on a specific spot and know it is there.
    //After starting, it requests the positions, resets its simulated coordinate system,
    //and schedules a sequence of turn and drive commands. 
    joy.button(4).onTrue(new InstantCommand(() -> m_nav.getNewPath()).andThen(
                                new InstantCommand(() -> m_nav.resetPose()).andThen(
                                new InstantCommand(() -> m_romiDrivetrain.resetEncoders()).andThen(
                                new InstantCommand(() -> m_nav.generateMovementLambdaFunctions(m_romiDrivetrain).schedule())))
    ));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return null;
  }
}
