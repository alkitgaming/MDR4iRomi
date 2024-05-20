// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Point;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Navigation;

public class CompleteSingleMove extends SequentialCommandGroup {
  /** Creates a new CompleteSingleMove. */

  public CompleteSingleMove(Drivetrain drive, Navigation nav, 
                            Point point) {
    Translation2d targetPosition = new Translation2d(point.x, point.y);
    Translation2d transform = targetPosition.minus(nav.getPose().getTranslation());
    double rotationRadians = Math.atan2(transform.getY(), transform.getX()) - nav.getHeading(); 
    TurnCommand turnCom = new TurnCommand(drive, nav, point);
    // - Math.signum(nav.getHeading() + 0.0001) * ;
    // double rotationRadians = transform.getRotation().getRadians() + (invertNorm ? (transform.getRotation().getRadians() > 0 ? Math.PI : -Math.PI) : 0) + nav.getHeading();
    addCommands(
      turnCom,
      new DriveCommand(drive, nav, point),
      new InstantCommand(() -> nav.setPose(new Pose2d(targetPosition, Rotation2d.fromRadians(Math.atan2(transform.getY(), transform.getX()))))),
      // new InstantCommand(() -> nav.setPose(nav.getPose().transformBy(new Transform2d(new Translation2d(), Rotation2d.fromRadians(rotationRadians))))),
      // new InstantCommand(() -> nav.setPose(nav.getPose().transformBy(new Transform2d(targetPosition, Rotation2d.fromRadians(0))))),
      new InstantCommand(() -> nav.updateAPI(point))
    );
  }
}
