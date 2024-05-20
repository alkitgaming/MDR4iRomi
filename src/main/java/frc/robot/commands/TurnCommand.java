// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.Point;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Navigation;

public class TurnCommand extends Command {
  Point point;
  Drivetrain drive;
  Navigation nav;
  double encoderInitial, encoderTargetInches, encoderTotalDistance, encoderPrevious;
  boolean goingClockwise;

  Translation2d targetPosition;
  Translation2d transform;
  double dtheta;    

  /** Creates a new TurnCommand. */
  public TurnCommand(Drivetrain drive, Navigation nav, Point point) {
    // this.dtheta = dtheta;
    this.point = point;
    this.drive = drive;
    this.nav = nav;
    addRequirements(drive, nav);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
    drive.setMotors(0, 0);
    targetPosition = new Translation2d(point.x, point.y);
    transform = targetPosition.minus(nav.getPose().getTranslation());
    dtheta = -(transform.getAngle().getRadians() - nav.getHeading()); 

    goingClockwise = dtheta < 0 ? true : false;
    encoderInitial = drive.getDistanceInch();
    encoderPrevious = encoderInitial;
    encoderTotalDistance = dtheta * Constants.Drive.radiansToInchesConversion;
    encoderTargetInches = encoderInitial - encoderTotalDistance - (Constants.Drive.automaticOverturnAdjustmentInches * Math.signum(encoderTotalDistance));
    SmartDashboard.putString("Current command", "TurnCommand: " + dtheta);
    SmartDashboard.putNumber("encoderInitial", encoderInitial);
    SmartDashboard.putNumber("encoderTarget", encoderTargetInches);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    if (goingClockwise)
    {
      drive.setMotors(Constants.Drive.defaultTurnSpeed, -Constants.Drive.defaultTurnSpeed);
    }
    else
    {
      drive.setMotors(-Constants.Drive.defaultTurnSpeed, Constants.Drive.defaultTurnSpeed);
    }
    // nav.incrementRotation((drive.getDistanceInch() - encoderPrevious) / Constants.Drive.radiansToInchesConversion);
    encoderPrevious = drive.getDistanceInch();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    drive.setMotors(0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return 
      (drive.getDistanceInch() <= encoderTargetInches && !goingClockwise) ||
      (drive.getDistanceInch() >= encoderTargetInches && goingClockwise);
  }
}
