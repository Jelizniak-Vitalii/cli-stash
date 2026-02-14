package com.clistash.app;

import com.clistash.app.model.CommandImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandStore {
  private static final Path PATH = resolvePath();

  private final Map<String, CommandImpl> commands = new LinkedHashMap<>();
  private final ObjectMapper mapper = new ObjectMapper()
    .findAndRegisterModules();

  public CommandStore() {
    initStorage();
    load();
  }

  private static Path resolvePath() {
    if (Files.exists(Path.of("/.dockerenv"))) {
      return Path.of("/data/commands.json");
    }

    return Path.of("commands.json").toAbsolutePath();
  }

  private void save() {
    try {
      mapper.writerWithDefaultPrettyPrinter()
        .writeValue(PATH.toFile(), commands);

    } catch (IOException e) {
      System.err.println("Failed to save commands: " + e.getMessage());
    }
  }

  public Map<String, CommandImpl> list() {
    return Map.copyOf(commands);
  }

  private void initStorage() {
    try {
      Files.createDirectories(PATH.getParent());

      if (Files.notExists(PATH)) {
        Files.createFile(PATH);
        save();
      }

    } catch (IOException e) {
      throw new RuntimeException("Failed to initialize storage", e);
    }
  }

  private void load() {
    try {
      if (Files.size(PATH) == 0) {
        return;
      }

      Map<String, CommandImpl> loaded = mapper.readValue(PATH.toFile(), new TypeReference<>() {});

      commands.clear();
      commands.putAll(loaded);

    } catch (IOException e) {
      System.err.println("Failed to load commands: " + e.getMessage());
    }
  }

  public void add(String name, String command) {
    if (commands.containsKey(name)) {
      System.out.println("Command '" + name + "' already exists.");

      return;
    }

    commands.put(name, new CommandImpl(command));
    save();
    System.out.println("Added: " + name);
  }

  public void remove(String name) {
    if (commands.remove(name) != null) {
      save();
      System.out.println("Removed: " + name);
    } else {
      System.out.println("Command '" + name + "' not found.");
    }
  }

  public void update(String name, String command) {
    CommandImpl existingCommand = commands.get(name);

    if (existingCommand == null) {
      System.out.println("Command '" + name + "' not found.");

      return;
    }

    existingCommand.setCommand(command);
    save();
    System.out.println("Updated: " + name);
  }

  public String findAndMarkUsed(String name) {
    CommandImpl cmd = commands.get(name);

    if (cmd == null) return null;

    cmd.incrementUsage();
    save();

    return cmd.getCommand();
  }
}
