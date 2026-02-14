package com.clistash.app;

public class CommandRunner {
  public static void run(String command) {
    try {
      ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
      pb.inheritIO();

      Process process = pb.start();
      process.waitFor();

    } catch (Exception e) {
      System.err.println("Execution failed: " + e.getMessage());
    }
  }
}

