package com.clistash.app.model;

public interface Command {
  String getCommand();

  String getCreatedAt();

  int getUsageCount();

  void incrementUsage();
}
