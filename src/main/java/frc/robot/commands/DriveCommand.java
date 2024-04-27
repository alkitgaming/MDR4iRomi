// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Navigation;

public class DriveCommand extends Command {
  double distance;
  Drivetrain drive;
  Navigation nav;
  double encoderInitial, encoderTarget, encoderPrevious;
  /** Creates a new DriveCommand. */
  public DriveCommand(Drivetrain drive, Navigation nav, double distance) {
    this.distance = distance;
    this.drive = drive;
    this.nav = nav;
    addRequirements(drive, nav);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
    encoderInitial = drive.getDistanceInch();
    encoderTarget = encoderInitial + distance;
    encoderPrevious = encoderInitial;
    drive.setMotors(0, 0);
    SmartDashboard.putString("Current command", "DriveCommand: " + distance);
    SmartDashboard.putNumber("encoderInitial", encoderInitial);
    SmartDashboard.putNumber("encoderTarget", encoderTarget);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    drive.setMotors(Constants.Drive.defaultDriveSpeed, Constants.Drive.defaultDriveSpeed);
    // nav.incrementPosition(drive.getDistanceInch() - encoderPrevious);
    SmartDashboard.putNumber("velocity", drive.getDistanceInch() - encoderPrevious);
    encoderPrevious = drive.getDistanceInch();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    drive.setMotors(0, 0);
  }

  // Returns true when the command should end.
  // The command should end when the command 
  // senses the encoder has passed its target.
  @Override
  public boolean isFinished() {
    return drive.getLeftDistanceInch() >= encoderTarget;
  }
}
