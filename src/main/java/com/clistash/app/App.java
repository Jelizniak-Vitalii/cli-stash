package com.clistash.app;

import com.clistash.app.model.CommandImpl;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class App {
  private static final CommandStore store = new CommandStore();

  public static void main(String[] args) {
    if (args.length == 0) {
      startRepl();

      return;
    }

    processArgs(args);
  }

  private static void startRepl() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("cli-stash shell. Type '-help' or 'exit'.");

    while (true) {
      System.out.print("stash> ");
      String line = scanner.nextLine().trim();

      if (line.isEmpty()) continue;
      if (line.equalsIgnoreCase("exit")) break;

      String[] tokens = line.split("\\s+");

      processArgs(tokens);
    }

    System.out.println("Bye.");
  }

  private static void processArgs(String[] args) {
    switch (args[0]) {
      case "-list" -> handleList();
      case "-help" -> printHelp();
      case "-get" -> handleGet(args);
      case "-add" -> handleAdd(args);
      case "-remove" -> handleRemove(args);
      case "-update" -> handleUpdate(args);
      case "-run" -> handleRun(args);
      default -> {
        System.out.println("Unknown option: " + args[0]);
        printHelp();
      }
    }
  }

  private static void handleList() {
    Map<String, CommandImpl> cmds = store.list();

    if (cmds.isEmpty()) {
      System.out.println("No saved commands.");
      return;
    }

    System.out.printf("%-20s %-8s %-20s %s%n",
      "NAME", "USES", "CREATED", "COMMAND");

    cmds.entrySet().stream()
      .sorted((a, b) ->
        Integer.compare(
          b.getValue().getUsageCount(),
          a.getValue().getUsageCount()))
      .forEach(entry -> {

        var name = entry.getKey();
        var cmd = entry.getValue();

        System.out.printf("%-20s %-8d %-20s %s%n",
          name,
          cmd.getUsageCount(),
          cmd.getCreatedAt(),
          cmd.getCommand());
      });
  }

  private static void handleGet(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: get <name>");
      return;
    }

    String command = store.findAndMarkUsed(args[1]);

    if (command == null) {
      System.out.println("Command '" + args[1] + "' not found.");
      return;
    }

    System.out.println(command);
  }

  private static void handleAdd(String[] args) {
    if (args.length < 3) {
      System.out.println("Usage: add <name> <command...>");
      return;
    }
    String name = args[1];
    String cmd = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

    store.add(name, cmd);
  }

  private static void handleRemove(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: remove <name>");
      return;
    }
    store.remove(args[1]);
  }

  private static void handleUpdate(String[] args) {
    if (args.length < 3) {
      System.out.println("Usage: update <name> <command...>");
      return;
    }
    String name = args[1];
    String cmd = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
    store.update(name, cmd);
  }

  private static void handleRun(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: run <name>");
      return;
    }

    String command = store.findAndMarkUsed(args[1]);

    if (command == null) {
      System.out.println("Command '" + args[1] + "' not found.");
      return;
    }

    CommandRunner.run(command);
  }

  private static void printHelp() {
    System.out.println("""
                Usage:
                  cli-stash                → start shell
                  cli-stash -list
                  cli-stash -get <name>
                  cli-stash -add <name> <cmd>
                  cli-stash -update <name> <cmd>
                  cli-stash -remove <name>
                  cli-stash -run <name>
                """);
  }
}
