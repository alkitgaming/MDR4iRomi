// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.ArrayList;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.CompleteSingleMove;

public class Navigation extends SubsystemBase {
  // private double x, y, heading;
  private Pose2d pose;
  private ArrayList<Point> locations;
  private int locationIndex;
  /** Creates a new Navigation. */
  public Navigation() 
  {
    resetPose();
  }

  public Command generateMovementLambdaFunctions(Drivetrain drive)
  {
    SequentialCommandGroup group = new SequentialCommandGroup();
    for (Point p : locations)
    {
      group.addCommands(new InstantCommand(() -> createCompleteSingleMove(drive, p)));
    }
    return group;
  }

  public void setLocations(ArrayList<Point> locations)
  {
    this.locations = locations;
  }

  public void getNewPath()
  {
    locations = (ArrayList<Point>) API.getPathFromAPI();
    locationIndex = 0;
  }

  public Command createCompleteSingleMove(Drivetrain drive, Point p)
  {
    Translation2d trans = new Translation2d(p.x, p.y);
    Command com = new CompleteSingleMove(drive, this, trans);
    CommandScheduler.getInstance().schedule(com);
    return com;
  }

  public Command example2(Drivetrain drive)
  {
    Translation2d point = new Translation2d(-12, 21);
    Command com = new CompleteSingleMove(drive, this, point);
    CommandScheduler.getInstance().schedule(com);
    return com;
  }

  public Command example(Drivetrain drive)
  {
    Translation2d point = new Translation2d(0, 0);
    Command com = new CompleteSingleMove(drive, this, point);
    CommandScheduler.getInstance().schedule(com);
    return com;
  }

  /*
   * This method tells the robot it is at its default 
   * home position and orientation.
   */
  public void resetPose()
  {
    pose = new Pose2d();
  }

  public void setPose(Pose2d newPose)
  {
    pose = newPose;
  }

  public void incrementRotation(double theta)
  {
    pose.rotateBy(Rotation2d.fromRadians(theta));
  }

  public void setCurrentPosition(double x, double y)
  {
    pose.getTranslation().plus(new Translation2d(x, y));
  }

  public double getX()
  {
    return pose.getX();
  }
  public double getY()
  {
    return pose.getY();
  }
  public double getHeading()
  {
    return pose.getRotation().getRadians();
  }

  public Pose2d getPose()
  {
    return pose;
  }

  public void updateAPI()
  {
    API.sendPositionToAPI(locations.get(locationIndex));
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Robot X", pose.getX());
    SmartDashboard.putNumber("Robot Y", pose.getY());
    SmartDashboard.putNumber("Robot Heading (Rad)", pose.getRotation().getRadians());
    SmartDashboard.putNumber("Robot Heading (Deg)", pose.getRotation().getDegrees());
  }
}
