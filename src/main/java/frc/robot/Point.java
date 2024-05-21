package frc.robot;


// This class is here solely to give a 1 to 1 
// translation between the desired JSON doc
// and the data storage in the robot.
// Usually a 2D vector or a Translation2D would
// be preferred.
public class Point
{
  public double x, y;
  public String name;
  public Point(String name, double x, double y)
  {
    this.x = x;
    this.y = y;
    this.name = name;
  }

  public Point() {}
}