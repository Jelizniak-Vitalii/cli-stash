package com.clistash.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandImpl implements Command {
  private String command;
  private String createdAt;
  private int usageCount;

  public CommandImpl() {}

  public CommandImpl(String command) {
    this.command = command;
    this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    this.usageCount = 0;

    System.out.println(this.createdAt);
  }

  @Override
  public String getCommand() {
    return command;
  }

  @Override
  public String getCreatedAt() {
    return createdAt;
  }

  @Override
  public int getUsageCount() {
    return usageCount;
  }

  @Override
  public void incrementUsage() {
    usageCount++;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public void setUsageCount(int usageCount) {
    this.usageCount = usageCount;
  }
}
