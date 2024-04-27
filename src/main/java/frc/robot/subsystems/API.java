// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class API extends SubsystemBase {
  /** Purely static class should not be instanced */
  private API() 
  {
    throw new IllegalStateException("Cannot instance static API class");
  }

  public static List<Point> getPathFromAPI()
  {
    String data = "data";

    try
    {
      URL url = new URL(Constants.API_PATH_URL);

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Accept", "application/json");
      connection.setDoOutput(true);
      
      BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8")); 
      StringBuilder response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
      }
      data = response.toString();
    }
    catch (Exception e)
    {
      System.out.println(e.getStackTrace());
    }

    ObjectMapper mapper = new ObjectMapper();
    TypeFactory typeFactory = mapper.getTypeFactory();
    try {
      return mapper.readValue(data, typeFactory.constructCollectionType(List.class, Point.class));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void sendPositionToAPI(Point point)
  {
    ObjectMapper mapper = new ObjectMapper();
    String data;
    try
    {
      data = mapper.writeValueAsString(point);
      URL url = new URL(Constants.API_POS_URL);

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(false);
      connection.setDoInput(false);

      try(OutputStream os = connection.getOutputStream()) {
        byte[] input = data.getBytes("utf-8");
        os.write(input, 0, input.length);			
      }
      
      connection.connect();
    }
    catch (Exception e)
    {
      System.out.println(e.getStackTrace());
    }
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}

