// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Drivetrain;

public class TeleopDriveCommand extends Command {
  DoubleSupplier forward, turn;
  Drivetrain drive;
  /** Creates a new TeleopDriveCommand. */
  public TeleopDriveCommand(Drivetrain drive, DoubleSupplier forward, DoubleSupplier turn) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.drive = drive;
    this.forward = forward;
    this.turn = turn;
    addRequirements(drive);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    drive.setMotors(-forward.getAsDouble() * 0.6 + turn.getAsDouble() * 0.4, -forward.getAsDouble() * 0.6 - turn.getAsDouble() * 0.4);
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
