// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    public final class Drive
    {
        public static final double defaultDriveSpeed = 0.2;
        public static final double defaultTurnSpeed = 0.14;

        public static final double wheelbaseDiameterInches = 6;

        //radians should be converted to drive inches for the encoder.
        //a radian is the full circle divided by 2pi.
        //the full circle's circumference is pi * diameter
        //inches per radian can be represented as pi * d / pi * 2
        //and simplified to diamater / 2
        //in hindsight, there is a similar root in the words
        //radian and radius
        public static final double radiansToInchesConversion = wheelbaseDiameterInches / 2;

        //The robot's hardware causes a right turn 
        //when the motors are run at the same power.
        public static final double overallSpeedMulitplier = 0.96;
        public static final double leftSideSpeedMultiplier = 1.1;
        public static final double automaticOverturnAdjustmentInches = -0.30;
    }

    public static final String API_PATH_URL = "http://ibmi.scottklement.com:4546/mdrapi/robotPath";
    public static final String API_POS_URL = "http://ibmi.scottklement.com:4546/mdrapi/robotLog";
}
